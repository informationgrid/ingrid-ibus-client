package de.ingrid.ibus.client;

import java.io.File;
import java.io.InputStream;

import net.weta.components.communication.ICommunication;
import de.ingrid.utils.IPlug;

public class BusClientFactory {

    private static BusClient _busClient = null;

	@Deprecated
    public static BusClient createBusClient() throws Exception {
        return createBusClient(BusClientFactory.class.getResourceAsStream("/communication.xml"));
    }

    public static BusClient createBusClient(final File file) throws Exception {
		if (_busClient == null) {
			_busClient = new BusClient(file);
		}
		return _busClient;
    }

	public static BusClient createBusClient(final File file, final IPlug iplug) throws Exception {
		if (_busClient == null) {
			_busClient = new BusClient(file, iplug);
		}
		return _busClient;
	}

	public static BusClient getBusClient() {
		return _busClient;
	}

	@Deprecated
    public static BusClient createBusClient(final InputStream inputStream) throws Exception {
        if (_busClient == null) {
            _busClient = new BusClient(inputStream);
        }
        return _busClient;
    }

	@Deprecated
    public static BusClient createBusClient(final ICommunication communication) throws Exception {
        if (_busClient == null) {
            _busClient = new BusClient(communication);
        }
        return _busClient;
    }
}
