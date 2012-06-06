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
