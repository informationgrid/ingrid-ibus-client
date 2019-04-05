/*
 * **************************************************-
 * Ingrid iBus Client
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.bus.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import junit.framework.TestCase;
import de.ingrid.ibus.client.CacheableInvocationHandler;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.QueryStringParser;

public class CachableInvocationHandlerTest extends TestCase {

    private int _counter = 0;
    
    public class DummyHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            _counter++;
            return new IngridHits();
        }
    }
    
    public void testCache() throws Exception {

        InvocationHandler invocationHandler = new CacheableInvocationHandler(new DummyHandler());
        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { IBus.class }, invocationHandler);
        IBus bus = (IBus) proxyInstance;
        IngridQuery query1 = QueryStringParser.parse("foo:bar");
        IngridQuery query2 = QueryStringParser.parse("foo:bar");
        IngridQuery query3 = QueryStringParser.parse("foo:bar1");
        
        assertEquals(0, _counter);
        
        bus.search(query1, 10, 1, 10, 10);
        assertEquals(1, _counter);

        bus.search(query1, 10, 1, 10, 10);
        assertEquals(1, _counter);
        
        bus.search(query2, 10, 1, 10, 10);
        assertEquals(1, _counter);
        
        bus.search(query3, 10, 1, 10, 10);
        assertEquals(2, _counter);
        
        
    }
}
