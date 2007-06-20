package de.ingrid.ibus.client.bench;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

public class StressTestBusClient {

    private static final String SEPERATOR = "\t";

    private static final DecimalFormat format = new DecimalFormat(".00");

    private IBus _bus;

    private IngridQuery _query;

    private final int _users;

    private final int _clicks;

    private BusClient _client;

    private PrintWriter _writer;

    public StressTestBusClient(File file, int user, int clicks) throws Exception {
        _users = user;
        _clicks = clicks;
        _client = BusClient.instance();
        _client.setBusUrl("/torwald-group:torwald-ibus");
        _client.setJxtaConfigurationPath(file.getAbsolutePath());
        _bus = _client.getBus();
        _query = QueryStringParser.parse("1 OR 3 datatype:address datatype:default ranking:score");
        _writer = new PrintWriter(new FileOutputStream(new File("webStress.csv")));
        _writer.println("Users" + SEPERATOR + "Clicks" + SEPERATOR + "Time" + SEPERATOR + "Hits");
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
        _client.shutdown();
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

    public static void main(String[] args) throws Exception {
        StressTestBusClient client = new StressTestBusClient(new File(args[0]), new Integer(args[1]).intValue(),
                new Integer(args[2]).intValue());

        while (true) {
            client.testBus();
        }

    }

}
