package de.ingrid.ibus.client;

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
        IngridQuery ingridQuery = QueryStringParser.parse("management_request_type:1  datatype:management ranking:off grouped:null");

        BusClient busClient = BusClientFactory.createBusClient();
        Thread.sleep(2000);

        IBus bus = busClient.getNonCacheableIBus();
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

        busClient.close();

    }
}
