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
package de.ingrid.ibus.client;

import de.ingrid.utils.DeepUtil;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

class BusClientApplication {

    public static void main(String[] args) throws Exception {

        String currentAddressId = "1";
        // IngridQuery ingridQuery =
        // QueryStringParser.parse("http dataype:default");
        IngridQuery ingridQuery = QueryStringParser.parse("Weltraum +datatype:sns cache:off");
        System.out.println(ingridQuery);

        BusClient busClient = BusClientFactory.createBusClient();
        Thread.sleep(2000);

        IBus bus = busClient.getNonCacheableIBus();
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();

            try {
                IngridHits ingridHits = bus.search(ingridQuery, 10, 1, 10, 100);
                IngridHit[] hits = ingridHits.getHits();
                System.out.println("#" + DeepUtil.deepString(ingridHits, 1) + "#");
                IngridHitDetail[] hitDetails = bus.getDetails(hits, ingridQuery, new String[] { "title" });
                for (IngridHitDetail ingridHitDetail : hitDetails) {
                    System.out.println(ingridHitDetail.getPlugId());
                }
            } catch (Exception e) {
                System.out.println("error");
            }

            long end = System.currentTimeMillis();
            System.out.println("No Cache: " + (end - start) + " ms.");
        }

        bus = busClient.getCacheableIBus();
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();

            try {
                IngridHits ingridHits = bus.search(ingridQuery, 10, 1, 10, 100);
                IngridHit[] hits = ingridHits.getHits();
                IngridHitDetail[] hitDetails = bus.getDetails(hits, ingridQuery, new String[] { "title" });
                for (IngridHitDetail ingridHitDetail : hitDetails) {
                    System.out.println(ingridHitDetail.getPlugId());
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

            long end = System.currentTimeMillis();
            System.out.println("With Cache: " + (end - start) + " ms.");
        }

        busClient.shutdown();

    }
}
