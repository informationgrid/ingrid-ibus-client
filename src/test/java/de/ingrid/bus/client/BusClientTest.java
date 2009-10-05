package de.ingrid.bus.client;

import java.io.File;

import junit.framework.TestCase;
import net.weta.components.communication.ICommunication;
import net.weta.components.communication.tcp.StartCommunication;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.utils.IPlug;

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

}
