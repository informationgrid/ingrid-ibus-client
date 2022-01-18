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
package de.ingrid.bus.client;

import java.io.File;

import de.ingrid.ibus.Bus;
import junit.framework.TestCase;
import net.weta.components.communication.ICommunication;
import net.weta.components.communication.reflect.ProxyService;
import net.weta.components.communication.tcp.StartCommunication;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IPlug;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.queryparser.QueryStringParser;

public class BusClientTest extends TestCase {

    @Mock
    private IPlug _plug_1;

    @Mock
    private IPlug _plug_2;

    private ICommunication _server;
    private BusClient _client;

    @Override
    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        // new
        _server = StartCommunication.create(BusClientTest.class.getResourceAsStream("/communication-server.xml"));
        _server.startup();
        ProxyService.createProxyServer(_server, IBus.class, new Bus(new DummyProxyFactory()));
        

        // singleton
        File file = new File("src/test/resources/communication-client.xml");
        _client = BusClientFactory.createBusClient(file);
        if (!_client.allConnected()) {
            _client.start();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        _server.shutdown();
    }

    public void testIsConnected() throws Exception {
        assertTrue(_client.allConnected());
    }

    public void testShutdown() throws Exception {
        _client.shutdown();
        assertFalse(_client.allConnected());
    }

    public void testRestart() throws Exception {
        assertTrue(_client.allConnected());
        _client.restart();
        assertTrue(_client.allConnected());
    }

    public void testSetPlug() throws Exception {
        _client.setIPlug(_plug_1);
        assertNotSame(_plug_1, _plug_2);
        assertEquals(_plug_1, _client.getIPlug());

        _client.setIPlug(_plug_2);
        // should be plug_1
        assertEquals(_plug_1, _client.getIPlug());
    }

    public void testGetUrlAndPeerName() throws Exception {
        assertEquals("/test-group:test-server", _client.getMotherBusUrl());
        assertEquals("/test-group:test-client", _client.getPeerName());
    }

    public void testGetBusses() throws Exception {
        assertNotNull(_client.getCacheableIBus());
        assertNotNull(_client.getNonCacheableIBus());
    }
    
    public void testRestartIBus() throws Exception {
        IngridHits hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
        _server.shutdown();
        Thread.sleep(2000);
        try {
        	hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        	fail("Server is shut down. Client should throw an exception.");
        } catch (net.weta.components.communication.tcp.TimeoutException e) {
        }
        
        _server.startup();
        Thread.sleep(2000);
        hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
    }

    
    public void testRestartClient() throws Exception {
        IngridHits hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
        _client.shutdown();
        Thread.sleep(2000);
        try {
        	hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        	fail("Client is not connected and should throw an exception.");
        } catch (NullPointerException e) {
        }
        _client.start();
        Thread.sleep(2000);
        hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
    }

    public void testStartClientBeforeIBus() throws Exception {
        IngridHits hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
        _server.shutdown();
        _client.shutdown();
        Thread.sleep(2000);
        try {
            hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
            fail("Client is not connected and should throw an exception.");
        } catch (NullPointerException e) {
        }
        _client.start();
        Thread.sleep(2000);
        try {
            hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
            fail("Client is not connected and should throw an exception.");
        } catch (NullPointerException e) {
        } catch (net.weta.components.communication.tcp.TimeoutException e) {
        } catch (Exception e) {
            fail("Unexpected Exception.");
        }
        _server.startup();
        Thread.sleep(2000);
        hits = _client.getNonCacheableIBus().search(QueryStringParser.parse("fische"), 10, 0, 10, 1000);
        assertNotNull(hits);
    }
    
    
}
