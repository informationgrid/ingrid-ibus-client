/*
 * **************************************************-
 * Ingrid iBus Client
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
/**
 * 
 */
package de.ingrid.ibus.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages multiple bus clients, usefull if a component access more than one
 * iBus.
 * 
 * @author joachim@wemove.com
 * 
 */
public class MultipleBusClientFactory {

    static Map<String, BusClient> busClients = new HashMap<String, BusClient>();

    /**
     * Returns a {@link BusClient} instance associated with the file.
     * 
     * @param file
     * @return
     * @throws Exception
     */
    public static BusClient getBusClient(final File file) throws Exception {
        if (!busClients.containsKey(file.getAbsolutePath())) {
            busClients.put(file.getAbsolutePath(), new BusClient(file));
        }
        return busClients.get(file.getAbsolutePath());
    }
}
