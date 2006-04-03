/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus.client;

import java.io.File;
import java.io.IOException;

import net.weta.components.communication.ICommunication;
import net.weta.components.peer.PeerService;
import net.weta.components.peer.StartJxtaConfig;
import net.weta.components.proxies.ProxyService;
import net.weta.components.proxies.remote.RemoteInvocationController;
import de.ingrid.ibus.Bus;

/**
 * A facade of a {@link de.ingrid.ibus.Bus} for easier access.
 * 
 * Usage:<br/>
 * 
 * Bus bus=BusClient.instance().getBus(); try{ bus.search(query,10,0,10000,10);
 * }catch(Exception e) { bus=BusClient.instance().reconnect();
 * bus.search(query,10,0,10000,10); }
 * 
 * <p/>created on 30.03.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class BusClient extends BusClientConfiguration {

    private Bus fBus;

    private ICommunication fCommunication;

    private ProxyService fProxyService;

    private static BusClient fInstance;
    
    private String jxtaHome = null;

    private BusClient() throws IOException {
        loadFromFile();
    }

    /**
     * @return the bus
     * @throws IOException
     */
    public static BusClient instance() throws IOException {
        if (fInstance == null) {
            fInstance = new BusClient();
        }
        return fInstance;
    }

    /**
     * TODO not better return IBus?
     * 
     * @return the bus
     * @throws IOException
     */
    public Bus getBus() throws IOException {
        if (this.fBus == null) {
            if (getBusUrl() == null) {
                loadFromFile();
            }
            if (this.fBus == null) {
                initBus();
            }
        }
        return this.fBus;
    }

    /**
     * Reconnect this facade and it bus object to the ibus server.
     * 
     * @return the reconnected bus
     */
    public Bus reconnect() {
        fLogger.info("reconnect bus client");
        shutdown();
        initBus();
        return this.fBus;
    }

    /**
     * Closes the bus and shutdown it communication.
     */
    public void shutdown() {
        this.fBus = null;
        if (this.fProxyService != null) {
            this.fProxyService.shutdown();
            this.fProxyService = null;
        }
        if (this.fCommunication instanceof PeerService) {
            try {
                ((PeerService) this.fCommunication).shutdown();
            } catch (IllegalStateException e) {
                fLogger.warn("shutdown communication: communication was already shut down");
            }
        }
        this.fCommunication = null;
    }

    private void initBus() {
        try {
            this.fCommunication = startJxtaCommunication(getJxtaConfigurationPath());
            this.jxtaHome = ((PeerService)this.fCommunication).getJxtaHome();
            this.fCommunication.subscribeGroup(getBusUrl());

            // start the proxy server
            this.fProxyService = new ProxyService();
            this.fProxyService.setCommunication(this.fCommunication);
            this.fProxyService.startup();

            RemoteInvocationController ric = this.fProxyService.createRemoteInvocationController(getBusUrl());
            this.fBus = (Bus) ric.invoke(Bus.class, Bus.class.getMethod("getInstance", null), null);
        } catch (Throwable t) {
            shutdown();
            throw new RuntimeException(t);
        }
    }

    private ICommunication startJxtaCommunication(String fileName) throws Exception {
        return StartJxtaConfig.start(getResource(fileName));
    }

    /**
     * @return the communication
     */
    public ICommunication getCommunication() {
        return this.fCommunication;
    }

    /**
     * @param communication
     */
    public void setCommunication(ICommunication communication) {
        this.fCommunication = communication;
    }

    /**
     * @return the proxy service
     */
    public ProxyService getProxyService() {
        return this.fProxyService;
    }

    /**
     * @param proxyService
     */
    public void setProxyService(ProxyService proxyService) {
        this.fProxyService = proxyService;
    }
    
    /**
     * Remove the clients JXTA home directory
     * 
     */
    public void removeJXTAHome() {
        if (this.jxtaHome != null) {
            deleteDirectoryRec(this.jxtaHome);
        }
    }

    /**
     * Helper. Remove directory recursivly.
     * 
     * @param dirPath
     */
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
    
    
}
