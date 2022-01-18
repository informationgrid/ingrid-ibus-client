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
package de.ingrid.ibus.client.bench;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

public class StressTestBusClient {

    private static final String SEPERATOR = "\t";

    static final DecimalFormat format = new DecimalFormat(".00");

    private IBus _bus;

    private IngridQuery _query;

    private final int _users;

    private final int _clicks;

    private PrintWriter _writer;

    private static long _benchStartTime = System.currentTimeMillis();

    public StressTestBusClient(File file, int user, int clicks) throws Exception {
        _users = user;
        _clicks = clicks;
        _bus = BusClientFactory.createBusClient().getCacheableIBus();
        _query = QueryStringParser.parse("1 OR 3 datatype:address datatype:default ranking:score");
        _writer = new PrintWriter(new FileOutputStream(new File("webStress.csv")));
        _writer.println("Users" + SEPERATOR + "Clicks" + SEPERATOR + "Time" + SEPERATOR + "Hits" + SEPERATOR
                + "TotalHits");
    }

    public void testBus() throws Exception {

        for (int i = 1; i < _users + 1; i++) {
            for (int j = 1; j < _clicks + 1; j++) {
                System.out.println(i + " Users - " + j + " clicks.");
                click(i, j, _writer);
            }
        }

        for (int i = _users; i >= 1; i--) {
            for (int j = _clicks; j >= 1; j--) {
                System.out.println(i + " Users -  with " + j + " clicks.");
                click(i, j, _writer);
            }
        }
        _writer.flush();

    }

    private void shutDown() {
        _writer.close();
    }

    /**
     * @param writer
     * @throws Exception
     */
    public long click(int threadCount, int clickCount, PrintWriter writer) throws Exception {

        long start = System.currentTimeMillis();
        CallThread[] callThreads = new CallThread[threadCount];
        for (int i = 0; i < callThreads.length; i++) {
            callThreads[i] = new CallThread(_bus, _query, threadCount, clickCount, writer);
            callThreads[i].start();
        }

        for (int i = 0; i < callThreads.length; i++) {
            callThreads[i].join();
            if (callThreads[i].getException() != null) {
                System.out.println("exception: " + callThreads[i].getException().getMessage());
            }
        }

        long end = System.currentTimeMillis();
        return end - start;
    }

    private class CallThread extends Thread {

        private int fUser;

        private Exception fException;

        private final PrintWriter fWriter;

        private final IBus bus;

        private final IngridQuery ingridQuery;

        private final int fClickCount;

        /**
         * @param path
         * @param count
         * @param clickCount
         * @param writer
         */
        public CallThread(IBus bus, IngridQuery ingridQuery, int user, int clickCount, PrintWriter writer) {
            this.bus = bus;
            this.ingridQuery = ingridQuery;
            this.fUser = user;
            this.fClickCount = clickCount;
            this.fWriter = writer;
        }

        /**
         * @return null or a thrown exception
         */
        public Exception getException() {
            return this.fException;
        }

        public void run() {

            try {
                for (int i = 0; i < this.fClickCount; i++) {
                    long start = System.currentTimeMillis();
                    IngridHits hits = bus.search(ingridQuery, 10, 1, 1, 30000);
                    long time = System.currentTimeMillis() - start;
                    System.out.println(format.format((time / 1000.0)) + SEPERATOR + hits.getHits().length);
                    fWriter.println(Duration.getDuration(start - _benchStartTime) + SEPERATOR + fUser + SEPERATOR
                            + fClickCount + SEPERATOR + format.format((time / 1000.0)) + SEPERATOR
                            + hits.getHits().length + SEPERATOR + hits.length());
                    fWriter.flush();
                }
            } catch (Exception e) {
                fWriter.println(fUser + SEPERATOR + fClickCount + SEPERATOR + format.format((100000 / 1000.0))
                        + SEPERATOR + 0);
                System.out.println(format.format((100000 / 1000.0)) + SEPERATOR + 0);
                this.fException = e;
            }
        }

    }

    public static void main(String[] args) throws Exception {
        StressTestBusClient client = new StressTestBusClient(new File(args[0]), new Integer(args[1]).intValue(),
                new Integer(args[2]).intValue());

        while (true) {
            client.testBus();
        }

    }

}
