package de.ingrid.ibus.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class BusClientFactory {

    private static BusClient _busClient = null;

    public static BusClient createBusClient() throws Exception {
        return createBusClient(BusClientFactory.class.getResourceAsStream("/communication.xml"));
    }

    public static BusClient createBusClient(File file) throws Exception {
        return createBusClient(new FileInputStream(file));
    }

    public static BusClient createBusClient(InputStream inputStream) throws Exception {
        if (_busClient == null) {
            _busClient = new BusClient(inputStream);
        }
        return _busClient;
    }
}
