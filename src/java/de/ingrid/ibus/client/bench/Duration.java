package de.ingrid.ibus.client.bench;

class Duration {

    private static final int DAYS_PER_WEEK = 7;

    private static final int HOURS_PER_DAY = 24;

    private static final int MINUTES_PER_HOUR = 60;

    private static final int SECONDS_PER_MINUTE = 60;

    private static final int MILLISECONDS_PER_SECOND = 1000;

    private static final int[] FACTORS = new int[] { MILLISECONDS_PER_SECOND, SECONDS_PER_MINUTE, MINUTES_PER_HOUR,
            HOURS_PER_DAY, DAYS_PER_WEEK };

    private static final String[] LABELS = new String[] { "ms", "s", "m", "h", "d", "w" };

    public static String getDuration(long duration) {
        if (duration == 0) {
            return "0" + LABELS[0];
        }
        StringBuffer result = new StringBuffer();
        long n = duration;
        for (int i = 0; n > 0 && i < FACTORS.length; i++) {
            int r = (int) (n % FACTORS[i]);
            n /= FACTORS[i];
            result.insert(0, " " + LABELS[i] + " ");
            result.insert(0, r);
        }
        if (n > 0) {
            result.insert(0, LABELS[FACTORS.length]);
            result.insert(0, n);
        }
        return result.toString();
    }
}
