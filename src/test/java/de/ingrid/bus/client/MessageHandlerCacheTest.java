/*
 * **************************************************-
 * Ingrid iBus Client
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.bus.client;

import net.weta.components.communication.messaging.IMessageHandler;
import net.weta.components.communication.messaging.Message;
import net.weta.components.communication.reflect.ReflectMessage;
import org.junit.jupiter.api.Test;

import de.ingrid.ibus.client.MessageHandlerCache;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageHandlerCacheTest {

    public class TestHandler implements IMessageHandler {

        private int _counter;

        @Override
        public Message handleMessage(final Message arg0) {
            _counter++;
            return new Message();
        }

    }

    @Test
    public void testReflectMessage() throws Exception {

        final TestHandler testHandler = new TestHandler();
        final MessageHandlerCache cache = new MessageHandlerCache(testHandler);

        assertEquals(0, testHandler._counter);
        for (int i = 0; i < 10; i++) {
            final IngridQuery ingridQuery = QueryStringParser.parse("foo" + i + " cache: true");
            ingridQuery.put("BUS_URL", "a_bus");
            cache.handleMessage(new ReflectMessage("search", IPlug.class.getName(), new Object[] { ingridQuery, 10, i }));
        }
        assertEquals(10, testHandler._counter);

        for (int i = 0; i < 10; i++) {
            final IngridQuery ingridQuery = QueryStringParser.parse("foo" + i + " cache: true");
            ingridQuery.put("BUS_URL", "a_bus");
            cache.handleMessage(new ReflectMessage("search", IPlug.class.getName(), new Object[] { ingridQuery, 10, i }));
        }
        assertEquals(10, testHandler._counter);

        for (int i = 0; i < 10; i++) {
            final IngridQuery ingridQuery = QueryStringParser.parse("foo" + i + " cache: true");
            ingridQuery.put("BUS_URL", "another_bus");
            cache.handleMessage(new ReflectMessage("search", IPlug.class.getName(), new Object[] { ingridQuery, 10, i }));
        }
        assertEquals(20, testHandler._counter);
    }
}
