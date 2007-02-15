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
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

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
    


    /**
     * @throws IOException
     * @throws ParseException 
     */
    public void testConnectToTorwald() throws Exception {
        BusClient client = BusClient.instance();
        String busUrl = "/torwald-ibus:ibus-torwald";
        String jxtaConf = "/de/ingrid/torwald.jxta.properties";
        client.setBusUrl(busUrl);
        client.setJxtaConfigurationPath(jxtaConf);

        IBus bus = client.getBus();
        assertNotNull(bus);
        
        String query = "datatype:management management_request_type:1";
        IngridQuery ingridQuery = QueryStringParser.parse(query);
        System.err.println("before");
        IngridHits hits = bus.search(ingridQuery, 0, 0, 10, 120000);
        for (int i = 0; i < hits.size(); i++) {
            Object object = hits.get(i);
            System.out.println(object);
        }
        System.err.println("after");
        System.out.println(bus.toString());
    }

 
}
