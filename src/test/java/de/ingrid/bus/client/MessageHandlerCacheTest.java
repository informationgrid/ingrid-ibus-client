package de.ingrid.bus.client;

import junit.framework.TestCase;
import net.weta.components.communication.messaging.IMessageHandler;
import net.weta.components.communication.messaging.Message;
import net.weta.components.communication.reflect.ReflectMessage;
import de.ingrid.ibus.client.MessageHandlerCache;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

public class MessageHandlerCacheTest extends TestCase {

    public class TestHandler implements IMessageHandler {

        private int _counter;

        @Override
        public Message handleMessage(final Message arg0) {
            _counter++;
            return new Message();
        }

    }

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