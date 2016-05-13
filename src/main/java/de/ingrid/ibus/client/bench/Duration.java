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
