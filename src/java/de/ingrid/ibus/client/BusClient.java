/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus.client;

import java.io.File;
import java.io.IOException;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ProxyService;
import net.weta.components.peer.PeerService;
import net.weta.components.peer.StartJxtaConfig;
import de.ingrid.utils.IBus;

/**
 * A facade of a {@link de.ingrid.ibus.Bus} for easier access.
 * 
 * Usage:<br/>
 * 
 * Bus bus=BusClient.instance().getBus(); try{ bus.search(query,10,0,10000,10); }catch(Exception e) {
 * bus=BusClient.instance().reconnect(); bus.search(query,10,0,10000,10); }
 * 
 * <p/>created on 30.03.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class BusClient extends BusClientConfiguration {

    private IBus fBus;

    private ICommunication fCommunication;

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
     * 
     * @return the bus
     * @throws IOException
     */
    public IBus getBus() throws IOException {
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
    public IBus reconnect() {
        fLogger.info("reconnect bus client");
        shutdown();
        initBus();
        return this.fBus;
    }

    /**
     * Closes the bus and shutdown it communication.
     */
    public void shutdown() {
        if (this.fBus == null) {
            return;
        }
        fLogger.info("shutting the ibus client down...");
        this.fBus = null;
        if (this.fCommunication instanceof PeerService) {
            try {
                ((PeerService) this.fCommunication).shutdown();
            } catch (IllegalStateException e) {
                fLogger.warn("shutdown communication: communication was already shut down");
            }
        }
        this.fCommunication = null;
    }

    // private void printThreadStatistics() {
    // System.out.println("---------------------------------");
    // System.out.println("threads:" +
    // Thread.currentThread().getThreadGroup().activeCount());
    // ThreadGroup[] threadGroups = new
    // ThreadGroup[Thread.currentThread().getThreadGroup().activeGroupCount()];
    // Thread.currentThread().getThreadGroup().enumerate(threadGroups);
    // for (int i = 0; i < threadGroups.length; i++) {
    // System.out.println(threadGroups[i].getName() + " - " +
    // threadGroups[i].activeCount());
    // Thread[] threads = new Thread[threadGroups[i].activeCount()];
    // threadGroups[i].enumerate(threads);
    // for (int j = 0; j < threads.length; j++) {
    // System.out.println(" " + threads[j].getName());
    // }
    // }
    // System.out.println("---------------------------------");
    // }

    private void initBus() {
        fLogger.info("initiating the ibus client ...");
        try {
            if (null == this.fCommunication) {
                this.fCommunication = startJxtaCommunication(getJxtaConfigurationPath());
                this.fCommunication.subscribeGroup(getBusUrl());
            }
            this.jxtaHome = ((PeerService) this.fCommunication).getJxtaHome();

            this.fBus = (IBus) ProxyService.createProxy(this.fCommunication, IBus.class, getBusUrl());
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

    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }

}
