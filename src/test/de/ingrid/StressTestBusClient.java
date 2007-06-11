package de.ingrid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import junit.framework.TestCase;
import de.ingrid.ibus.client.BusClient;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

public class StressTestBusClient extends TestCase {

    private static final String SEPERATOR = "\t";

    private static final DecimalFormat format = new DecimalFormat(".00");

    private IBus _bus;

    private IngridQuery _query;

    protected void setUp() throws Exception {
        BusClient client = BusClient.instance();
        client.setBusUrl("/torwald-group:torwald-ibus");
        client.setJxtaConfigurationPath("src/conf/communication.properties");
        _bus = client.getBus();
        _query = QueryStringParser.parse("1 OR 3 datatype:default ranking:score");
    }

    public void testBus() throws Exception {

        PrintWriter writer = new PrintWriter(new FileOutputStream(new File("webStress.csv")));
        int threadCount = 20;
        int clickCount = 1;
        writer.println("Users" + SEPERATOR + "Clicks" + SEPERATOR + "Time" + SEPERATOR + "Hits");
        for (int i = 1; i < threadCount + 1; i++) {
            for (int j = 1; j < clickCount + 1; j++) {
                System.out.println(i + " Users - " + j + " clicks.");
                click(i, j, writer);
                // flush(i, j, writer);
            }
        }

        for (int i = threadCount; i >= 1; i--) {
            for (int j = clickCount; j >= 1; j--) {
                System.out.println(i + " Users -  with " + j + " clicks.");
                click(i, j, writer);
                // flush(i, j, writer);
            }
        }

        writer.close();
    }

    private void flush(int user, int clicks, PrintWriter writer) {
        for (int i = 0; i < 5; i++) {
            writer.println(user + SEPERATOR + clicks + SEPERATOR + "0.0" + SEPERATOR + "0");
        }
        writer.flush();
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
                    fWriter.println(fUser + SEPERATOR + fClickCount + SEPERATOR + format.format((time / 1000.0))
                            + SEPERATOR + hits.getHits().length);
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

}
