package de.ingrid.ibus.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.messaging.IMessageHandler;
import net.weta.components.communication.messaging.IMessageQueue;
import net.weta.components.communication.reflect.ReflectInvocationHandler;
import net.weta.components.communication.reflect.ReflectMessageHandler;
import net.weta.components.communication.tcp.StartCommunication;
import net.weta.components.communication.tcp.TcpCommunication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.IBus;
import de.ingrid.utils.IPlug;

public class BusClient {

    private final List<IBus> _nonCacheableIBusses = new ArrayList<IBus>();

    private final List<IBus> _cacheableIBusses = new ArrayList<IBus>();

    private ICommunication _communication;

    private File _communicationXml;

    private IPlug _iPlug;

    private static final Log LOG = LogFactory.getLog(BusClient.class);

    @Deprecated
    BusClient() throws Exception {
        this(BusClient.class.getResourceAsStream("/communication.xml"));
    }

    BusClient(final File communicationXml) throws Exception {
        _communicationXml = communicationXml;
        start();
    }

    BusClient(final File communicationXml, final IPlug iplug) throws Exception {
        _communicationXml = communicationXml;
        _iPlug = iplug;
        start();
        setCommunicationPlug(_iPlug, _communication);
    }

    @Deprecated
    BusClient(final InputStream inputStream) throws Exception {
        _communication = StartCommunication.create(inputStream);
        _communication.startup();
        createIBusProxies(_communication);
    }

    @Deprecated
    BusClient(final ICommunication communication) throws Exception {
        _communication = communication;
        createIBusProxies(_communication);
    }

    public String getPeerName() {
        return _communication.getPeerName();
    }

    public void setIPlug(final IPlug iPlug) throws Exception {
        if (_iPlug != null) {
            LOG.warn("iPlug is already set: " + _iPlug.getClass().getName());
        } else {
            _iPlug = iPlug;
            setCommunicationPlug(_iPlug, _communication);
        }
    }

    public IPlug getIPlug() {
        return _iPlug;
    }

    public List<IBus> getNonCacheableIBusses() {
        return _nonCacheableIBusses;
    }

    public IBus getNonCacheableIBus() {
        return _nonCacheableIBusses.size() > 0 ? _nonCacheableIBusses.get(0) : null;
    }

    public List<IBus> getCacheableIBusses() {
        return _cacheableIBusses;
    }

    public IBus getCacheableIBus() {
        return _cacheableIBusses.size() > 0 ? _cacheableIBusses.get(0) : null;
    }

    public final String getMotherBusUrl() {
        return getBusUrl(0);
    }

    public final String getBusUrl(final int index) {
        if (_communication != null) {
            return (String) ((TcpCommunication) _communication).getServerNames().get(index);
        }
        return null;
    }

    public boolean allConnected() {
        boolean bit = _communication != null;
        if (bit) {
            final List serverNames = ((TcpCommunication) _communication).getServerNames();
            for (final Object serverName : serverNames) {
                if (!_communication.isConnected(serverName.toString())) {
                    bit = false;
                    break;
                }
            }
        }
        return bit;
    }

    public boolean allDisconnected() {
        boolean bit = _communication != null;
        if (bit) {
            final List serverNames = ((TcpCommunication) _communication).getServerNames();
            for (final Object serverName : serverNames) {
                if (_communication.isConnected(serverName.toString())) {
                    bit = false;
                    break;
                }
            }
        }
        return bit;
    }

    public boolean isConnected(final int index) {
        if (_communication != null) {
            return _communication.isConnected(getBusUrl(index));
        }
        return false;
    }

    public void start() throws Exception {
        if (_communicationXml.exists()) {
            if ((allDisconnected() || _communication == null)) {
                // connect
                LOG.info("create communication");
                _communication = StartCommunication.create(new FileInputStream(_communicationXml));
                LOG.info("start communication");
                _communication.startup();
                // sleep until connected
                for (int i = 0; i < 10; i++) {
                    if (allConnected()) {
                        break;
                    }
                    Thread.sleep(500);
                }
                if (!allConnected()) {
                    throw new Exception("start communication failed");
                }
                // create iBusses
                createIBusProxies(_communication);
                // set plug
                setCommunicationPlug(_iPlug, _communication);
            } else if (!allConnected()) {
                restart();
            }
        }
    }

    public void shutdown() throws Exception {
        if (_communication != null) {
            LOG.info("shutdown communication");

            // shutdown communication
            _communication.shutdown();

            // clear busses
            _nonCacheableIBusses.clear();
            _cacheableIBusses.clear();

            // sleep until connected
            for (int i = 0; i < 10; i++) {
                if (allDisconnected()) {
                    break;
                }
                Thread.sleep(500);
            }
            if (!allDisconnected()) {
                throw new Exception("shutdown communication failed");
            }
        }
    }

    public void restart() throws Exception {
        LOG.info("restart communication");
        shutdown();
        start();
    }

    @SuppressWarnings("unchecked")
    private void createIBusProxies(final ICommunication communication) throws Exception {
        final List<String> serverNames = ((TcpCommunication) communication).getServerNames();
        _nonCacheableIBusses.clear();
        _cacheableIBusses.clear();
        for (final String name : serverNames) {
            final InvocationHandler nonCacheableHandler = new ReflectInvocationHandler(communication, name);
            final IBus nonCacheableIBus = (IBus) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[] { IBus.class }, nonCacheableHandler);
            _nonCacheableIBusses.add(nonCacheableIBus);

            final InvocationHandler cacheableInvocationHandler = new CacheableInvocationHandler(nonCacheableHandler);
            final IBus cacheableIBus = (IBus) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[] { IBus.class }, cacheableInvocationHandler);
            _cacheableIBusses.add(cacheableIBus);
        }
    }

    private void setCommunicationPlug(final IPlug plug, final ICommunication communication) throws Exception {
        if (plug != null && communication != null) {
            final IMessageQueue messageQueue = communication.getMessageQueue();
            IMessageHandler messageHandler = new ReflectMessageHandler();
            ((ReflectMessageHandler) messageHandler).addObjectToCall(IPlug.class, plug);
            messageHandler = new MessageHandlerCache(messageHandler);
            LOG.info("add iplug [" + plug.getClass().getSimpleName() + "] to message handler ["
                    + messageHandler.getClass().getSimpleName() + "]");
            LOG.info("add message handler to message queue");
            messageQueue.addMessageHandler(ReflectMessageHandler.MESSAGE_TYPE, messageHandler);
        }
    }
}
