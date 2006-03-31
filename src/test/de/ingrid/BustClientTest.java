/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import net.weta.components.peer.PeerService;
import de.ingrid.ibus.Bus;
import de.ingrid.ibus.client.BusClient;

/**
 * Test for {@link de.ingrid.ibus.client.BusClient}.
 * 
 * <p/>created on 30.03.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class BustClientTest extends TestCase {

    private static final boolean ENABLED = false;

    /**
     * @throws Throwable
     */
    public void testZombieThreafs() throws Throwable {
        if (!ENABLED) {
            System.out.println("skipping testZombieThreads");
            return;
        }
        BusClient client = BusClient.instance();
        String busUrl = "wetag:///IBUS-SERVER-INDERECKE:IBus-inderecke";
        String jxtaConf = "/de/ingrid/localtest.jxta.conf.xml";
        client.setBusUrl(busUrl);
        client.setJxtaConfigurationPath(jxtaConf);

        printThreadStatistic();
        Bus bus = client.getBus();
        assertNotNull(bus);

        client.shutdown();
        Thread.sleep(1000);
        printThreadStatistic();
    }

    private void printThreadStatistic() {
        System.out.println("**********************************");
        System.out.println(Thread.currentThread().getThreadGroup());
        System.out.println("threads:" + Thread.currentThread().getThreadGroup().activeCount());
        System.out.println("groups:" + Thread.currentThread().getThreadGroup().activeGroupCount());
        ThreadGroup[] threadGroups = new ThreadGroup[Thread.currentThread().getThreadGroup().activeGroupCount()];
        Thread.currentThread().getThreadGroup().enumerate(threadGroups);
        System.out.println("------------thread groups-----------------");
        for (int i = 0; i < threadGroups.length; i++) {
            System.out.println(threadGroups[i].getName() + " - parent :" + threadGroups[i].getParent().getName());
        }
        System.out.println("**********************************");
    }

    /**
     * @throws IOException
     */
    public void testGetRemoteBus() throws IOException {
        if (!ENABLED) {
            System.out.println("skipping testGetRemoteBus");
            return;
        }

        BusClient client = BusClient.instance();
        String busUrl = "wetag:///IBUS-SERVER-INDERECKE:IBus-inderecke";
        String jxtaConf = "/de/ingrid/localtest.jxta.conf.xml";
        client.setBusUrl(busUrl);
        client.setJxtaConfigurationPath(jxtaConf);

        Bus bus = client.getBus();
        assertNotNull(bus);
        assertNull(bus.getIPlug("nixIplug"));

        client.setJxtaConfigurationPath("/irgendwas/falsches.ccp");
        try {
            client.reconnect();
            fail("jxta conf not exists");
        } catch (RuntimeException e) {
            //            
        }

        client.setJxtaConfigurationPath(jxtaConf);
        bus = client.reconnect();
        assertNotNull(bus);
        assertNull(bus.getIPlug("nixIplug"));

        PeerService peerService = (PeerService) client.getCommunication();
        String jxtaHome = peerService.getJxtaHome();
        client.shutdown();
        deleteDirectoryRec(jxtaHome);
    }

    private static void deleteDirectoryRec(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            String[] files = dir.list();
            for (int i = 0; i < files.length; i++) {
                File file = new File(dir, files[i]);
                if (file.isDirectory()) {
                    deleteDirectoryRec(file.getAbsolutePath());
                } else {
                    if (!file.delete()) {
                        System.out.println("Cannot delete file: " + file.getAbsolutePath());
                    }
                }
            }
            if (!dir.delete()) {
                System.out.println("Cannot delete directory: " + dir.getAbsolutePath());
            }
        }
    }

    // private static final String BUS_URL="wetag:///group:ibus";
    //    
    // private static final String
    // FACADE_JXTA_CONFIG="/de/ingrid/test.facade.jxta.conf.xml";

    // ProxyService proxyService;
    //    
    // PeerService peerService;
    // Bus bus;
    //    
    // protected void setUp() throws Exception {
    // String jxtaHome = "target/busserver_.jxta";
    // deleteDirectoryRec(jxtaHome);
    // this.peerService = new PeerService();
    // setPeerServiceVariables(this.peerService, jxtaHome, 9701, 9700);
    // this.peerService.boot();
    // // this.peerService.subscribeGroup(BUS_URL);
    //        
    // this.proxyService = new ProxyService();
    // this.proxyService.setCommunication(this.peerService);
    // this.proxyService.startup();
    //        
    // IPlugProxyFactory proxyFactory = new IPlugProxyFactoryImpl(peerService);
    // bus = new Bus(proxyFactory);
    // Registry registry = bus.getIPlugRegistry();
    // registry.setCommunication(peerService);
    // }

    // protected void tearDown() throws Exception {
    // this.proxyService.shutdown();
    // this.peerService.shutdown();
    // }

    // public void testGetBus() throws IOException {
    // Facade facade=Facade.instance();
    // facade.setBusUrl(BUS_URL);
    // facade.setJxtaConfigurationPath(FACADE_JXTA_CONFIG);
    // Bus bus=facade.getBus();
    // assertNotNull(bus.getAllIPlugs());
    // }

    // public void testGetTorwaldBus() throws IOException {
    // BusClient client = BusClient.instance();
    // String jxtaConf = "/jxta.conf.xml";
    // client.setJxtaConfigurationPath(jxtaConf);
    //
    // Bus bus = client.getBus();
    // assertNotNull(bus);
    // assertNull(bus.getIPlug("nixIplug"));
    //
    // client.setJxtaConfigurationPath("/irgendwas/falsches.ccp");
    // try {
    // client.reconnect();
    // fail("jxta conf not exists");
    // } catch (RuntimeException e) {
    // //
    // }
    //
    // client.setJxtaConfigurationPath(jxtaConf);
    // bus = client.reconnect();
    // assertNotNull(bus);
    // assertNull(bus.getIPlug("nixIplug"));
    //
    // PeerService peerService = (PeerService) client.getCommunication();
    // String jxtaHome = peerService.getJxtaHome();
    // client.shutdown();
    // deleteDirectoryRec(jxtaHome);
    //
    // }

    // private static void setPeerServiceVariables(AbstractJxtaConfiguration p,
    // String jh, int tcpPort, int httpPort) {
    // p.setDescription("bla");
    // p.setJxtaHome(jh);
    // p.setPassword("pw");
    // p.setPrincipal("pp");
    //
    // p.setTcpClientEnable(true);
    // p.setTcpMulticastEnable(true);
    // p.setTcpPort(tcpPort);
    // p.setTcpMode("auto");
    // p.setTcpServerEnable(true);
    // p.setTcpServerPort(tcpPort);
    //
    // p.setHttpClientEnable(true);
    // p.setHttpPort(httpPort);
    // p.setHttpServerEnable(false);
    // p.setHttpMode("auto");
    //
    // p.setSeedRendezvous("tcp://209.128.126.120:9701");
    // p.setSeedRelay("tcp://209.128.126.120:9701");
    // p.setIsRelay(false);
    // p.setUseRelay(false);
    // }

}
