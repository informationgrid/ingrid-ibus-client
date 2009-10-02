package de.ingrid.ibus.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

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

	private IBus _nonCacheableIBus;

    private IBus _cacheableIBus;

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
		}
	}

	public IPlug getIPlug() {
		return _iPlug;
	}

    public IBus getNonCacheableIBus() {
        return _nonCacheableIBus;
    }

    public IBus getCacheableIBus() {
        return _cacheableIBus;
    }

    public void close() throws Exception {
        _cacheableIBus.close();
        _nonCacheableIBus.close();
		_communication.shutdown();
    }

	public final String getMotherBusUrl() {
		if (_communication != null) {
			return (String) ((TcpCommunication) _communication).getServerNames().get(0);
		}
		return null;
	}

	public boolean isConnected() {
		if (_communication != null) {
			return _communication.isConnected(getMotherBusUrl());
		}
		return false;
	}

    public void start() throws Exception {
		if (!isConnected() && _communicationXml.exists()) {
			// connect
			LOG.info("create communication");
			_communication = StartCommunication.create(new FileInputStream(_communicationXml));
			LOG.info("start communication");
			_communication.startup();
			// sleep until connected
			for (int i = 0; i < 10; i++) {
				if (isConnected()) {
					break;
				}
				Thread.sleep(500);
			}
			if (!isConnected()) {
				throw new Exception("start communication failed");
			}
			// create iBusses
			createIBusProxies(_communication);
			// set plug
			setCommunicationPlug(_iPlug);
		}
	}

	public void shutdown() throws Exception {
		if (_communication != null && isConnected()) {
			LOG.info("shutdown communication");
			_communication.shutdown();
			// sleep until connected
			for (int i = 0; i < 10; i++) {
				if (!isConnected()) {
					break;
				}
				Thread.sleep(500);
			}
			if (isConnected()) {
				throw new Exception("shutdown communication failed");
			}
		}
	}

	public void restart() throws Exception {
		LOG.info("restart communication");

		shutdown();

		start();
	}

	private void createIBusProxies(final ICommunication communication) throws Exception {
		final String busUrl = (String) ((TcpCommunication) communication).getServerNames().get(0);
		final InvocationHandler nonCacheableHandler = new ReflectInvocationHandler(communication, busUrl);
		final InvocationHandler cacheableInvocationHandler = new CacheableInvocationHandler(nonCacheableHandler);
		_nonCacheableIBus = (IBus) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { IBus.class }, nonCacheableHandler);
		_cacheableIBus = (IBus) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { IBus.class }, cacheableInvocationHandler);
	}

	private void setCommunicationPlug(final IPlug plug) {
		if (plug != null && _communication != null) {
			final IMessageQueue messageQueue = _communication.getMessageQueue();
			final IMessageHandler messageHandler = new ReflectMessageHandler();
			LOG.info("add iplug [" + plug.getClass().getSimpleName() + "] to message handler");
			((ReflectMessageHandler) messageHandler).addObjectToCall(IPlug.class, plug);
			LOG.info("add message handler to message queue");
			messageQueue.addMessageHandler(ReflectMessageHandler.MESSAGE_TYPE, messageHandler);
		}
	}
}
