/*
 * Copyright 2004-2005 weta group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  $Source:  $
 */

package de.ingrid;

import java.io.IOException;

import junit.framework.TestCase;
import de.ingrid.ibus.client.BusClient;
import de.ingrid.utils.IBus;

/**
 * Test for connecting to several servers.
 * 
 * <p/>created on 18.04.2006
 * 
 * @version $Revision: $
 * @author jz
 * @author $Author: ${lastedit}
 * 
 */
public class ConnectTest extends TestCase {

    private static final boolean ENABLED = false;
    
//    /**
//     * @throws IOException
//     */
//    public void testConnectToTorwaldOverProxy() throws IOException {
//      
//        BusClient client = BusClient.instance();
//        String busUrl = "/torwald-ibus:ibus-torwald";
//        String jxtaConf = "/de/ingrid/torwald.proxy.jxta.properties";
//        client.setBusUrl(busUrl);
//        client.setJxtaConfigurationPath(jxtaConf);
//        
//
//        IBus bus = client.getBus();
//        assertNotNull(bus);
//    }


    /**
     * @throws IOException
     */
    public void testConnectToTorwald() throws IOException {
        if (!ENABLED) {
            System.out.println("skipping " + getName());
            return;
        }
        BusClient client = BusClient.instance();
        String busUrl = "/torwald-ibus:ibus-torwald";
        String jxtaConf = "/de/ingrid/torwald.jxta.properties";
        client.setBusUrl(busUrl);
        client.setJxtaConfigurationPath(jxtaConf);

        IBus bus = client.getBus();
        assertNotNull(bus);
    }

    /**
     * @throws IOException
     */
    public void testConnectToKug() throws IOException {
        if (!ENABLED) {
            System.out.println("skipping " + getName());
            return;
        }

        BusClient client = BusClient.instance();
        String busUrl = "/kug-group:kug-ibus";
        String jxtaConf = "/de/ingrid/kug.jxta.properties";
        client.setBusUrl(busUrl);
        client.setJxtaConfigurationPath(jxtaConf);

        IBus bus = client.getBus();
        assertNotNull(bus);
        client.shutdown();
    }
}
