/*
 * **************************************************-
 * Ingrid iBus Client
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
