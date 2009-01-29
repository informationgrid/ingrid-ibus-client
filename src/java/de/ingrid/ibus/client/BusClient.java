package de.ingrid.ibus.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ReflectInvocationHandler;
import net.weta.components.communication.tcp.StartCommunication;
import net.weta.components.communication.tcp.TcpCommunication;
import de.ingrid.utils.IBus;

public class BusClient {

    private IBus _nonCacheableIBus;

    private IBus _cacheableIBus;

    private ICommunication _communication;

    BusClient() throws Exception {
        this(BusClient.class.getResourceAsStream("/communication.xml"));
    }

    BusClient(File communicationXml) throws Exception {
        this(new FileInputStream(communicationXml));
    }

    BusClient(InputStream inputStream) throws Exception {
        _communication = StartCommunication.create(inputStream);
        _communication.startup();
        createIBusProxies(_communication);
    }

    public void createIBusProxies(ICommunication communication) throws Exception {
        String busUrl = (String) ((TcpCommunication) communication).getServerNames().get(0);
        InvocationHandler nonCacheableHandler = new ReflectInvocationHandler(communication, busUrl);
        InvocationHandler cacheableInvocationHandler = new CacheableInvocationHandler(nonCacheableHandler);
        _nonCacheableIBus = (IBus) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { IBus.class }, nonCacheableHandler);
        _cacheableIBus = (IBus) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { IBus.class }, cacheableInvocationHandler);
    }

    public IBus getNonCacheableIBus() {
        return _nonCacheableIBus;
    }

    public IBus getCacheableIBus() {
        return _cacheableIBus;
    }

    public void close() throws IOException {
        _communication.shutdown();
    }

}
