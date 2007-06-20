/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ProxyService;
import net.weta.components.communication.tcp.StartCommunication;
import de.ingrid.utils.IBus;
import de.ingrid.utils.messages.CategorizedKeys;

/**
 * A facade of a {@link de.ingrid.ibus.Bus} for easier access.
 * 
 * Usage:<br/>
 * Bus bus=BusClient.instance().getBus();<br/>
 * try{<br/>
 *  bus.search(query,10,0,10000,10);<br/>
 * } catch(Exception e) {<br/>
 *  bus=BusClient.instance().reconnect();<br/>
 *  bus.search(query,10,0,10000,10);<br/>
 * }<br/>
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
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
     * A BusClient instance.
     * @return A BusClient instance.
     * @throws IOException
     */
    public static BusClient instance() throws IOException {
        if (fInstance == null) {
            fInstance = new BusClient();
        }
        return fInstance;
    }

    /**
     * A bus instance.
     * @return A bus instance.
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
     * Reconnect this facade and its bus object to the ibus server.
     * 
     * @return The reconnected bus.
     */
    public IBus reconnect() {
        fLogger.info("reconnect bus client");
        shutdown();
        initBus();
        return this.fBus;
    }

    /**
     * Closes the bus and shutdown its communication.
     */
    public void shutdown() {
        if (this.fBus == null) {
            return;
        }
        fLogger.info("shutting the ibus client down...");
        this.fBus = null;
        this.fCommunication.shutdown();
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
                this.fCommunication = startCommunication(getJxtaConfigurationPath());
                this.fCommunication.subscribeGroup(getBusUrl());
            }

            this.fBus = (IBus) ProxyService.createProxy(this.fCommunication, IBus.class, getBusUrl());
        } catch (Throwable t) {
            shutdown();
            throw new RuntimeException(t);
        }
    }

    private ICommunication startCommunication(String fileName) throws Exception {
        ICommunication communication = StartCommunication.create(getResource(fileName));
        communication.startup();
        return communication;
    }

    /**
     * Returns the used communication.
     * @return The used communication.
     */
    public ICommunication getCommunication() {
        return this.fCommunication;
    }

    /**
     * Set the communication.
     * @param communication The communication to set.
     */
    public void setCommunication(ICommunication communication) {
        this.fCommunication = communication;
    }

    /**
     * Remove the clients JXTA home directory.
     */
    public void removeJXTAHome() {
        if (this.jxtaHome != null) {
            deleteDirectoryRec(this.jxtaHome);
        }
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

    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }
    
    public static void main(String[] args) throws IOException {
      
        Properties properties = new Properties();
        File file = new File(System.getProperty("java.io.tmpdir"), "provider.properties");
        FileOutputStream stream = new FileOutputStream(file, false);
        properties.store(stream, "");
        stream.close();
        FileInputStream stream2 = new FileInputStream(file);
        CategorizedKeys keys = CategorizedKeys.get("provider.properties", stream2);
        
        
    }

}
