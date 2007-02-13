/*
 * Copyright (c) 1997-2006 by media style GmbH
 */
package de.ingrid.ibus.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Holds configuration values which are needed to acces an ibus.
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 */
public class BusClientConfiguration {

    /**
     * The name of the configuration file for this facade.
     */
    public static final String CONFIG_FILE_PATH = "/ibus-client.properties";

    /**
     * The key of the bus-url configuration property.
     */
    public static final String CONFIG_BUS_URL = "bus-url";

    /**
     * The key of the jxta-configuration-path configuration property.
     */
    public static final String CONFIG_JXTA_CONFIGURATION_PATH = "jxta-configuration";

    protected static Log fLogger = LogFactory.getLog(BusClient.class);

    private String fBusUrl;

    private String fJxtaConfigurationPath;

    /**
     * Load configuration from {@link #CONFIG_FILE_PATH}.
     * 
     * @throws IOException
     */
    public void loadFromFile() throws IOException {
        loadFromFile(CONFIG_FILE_PATH);
    }

    /**
     * Load configuration from given configPath.
     * 
     * @param configPath
     * @throws IOException
     */
    public void loadFromFile(String configPath) throws IOException {
        fLogger.info("try loading facade configuration from '"+configPath+"'");
        InputStream inputStream = BusClient.class.getResourceAsStream(configPath);
        if (inputStream == null) {
            fLogger.warn("could not find property file '" + configPath + "'");
            this.fBusUrl = "";
            this.fJxtaConfigurationPath = "";
        } else {
          Properties properties = new Properties();
          properties.load(inputStream);
          this.fBusUrl = getProperty(properties, CONFIG_BUS_URL);
          this.fJxtaConfigurationPath = getProperty(properties, CONFIG_JXTA_CONFIGURATION_PATH);
        }
    }

    /**
     * Returns the bus url.
     * @return Returns the bus url.
     */
    public String getBusUrl() {
        return this.fBusUrl;
    }

    /**
     * The bus url to set.
     * @param busUrl
     *            The bus url to set.
     */
    public void setBusUrl(String busUrl) {
        this.fBusUrl = busUrl;
    }

    /**
     * Returns the jxta configuration path.
     * @return The jxta configuration path.
     */
    public String getJxtaConfigurationPath() {
        return this.fJxtaConfigurationPath;
    }

    /**
     * The jxta configuration path to set.
     * @param jxtaConfigurationPath
     *            The jxta configuration path to set.
     */
    public void setJxtaConfigurationPath(String jxtaConfigurationPath) {
        this.fJxtaConfigurationPath = jxtaConfigurationPath;
    }

    protected static String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("property with key '" + key + "' not found");
        }
        return value;
    }

    /**
     * Loads a given resource and looks first in the classpath, secondly in the physical
     * file system.
     * 
     * @param path
     *            The resource to load.
     * @return Access to the given resource in form of a input stream.
     * @throws FileNotFoundException If the resource isn't found.
     */
    protected InputStream getResource(String path) throws FileNotFoundException {
        fLogger.info("loading resource '" + path + "'");
        InputStream inputStream = BusClientConfiguration.class.getResourceAsStream(path);
        if (inputStream == null) {
            fLogger.warn("could not find resource '" + path + "' in classpath, trying physical path");
            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalArgumentException("could not load file '" + file.getAbsolutePath() + "'");
            }
            if (file.isDirectory()) {
                throw new IllegalArgumentException("given file '" + file.getAbsolutePath() + "' is a directory");
            }
            inputStream = new FileInputStream(file);
        }

        return inputStream;
    }
}
