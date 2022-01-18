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
package de.ingrid.ibus.client;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.weta.components.communication.messaging.IMessageHandler;
import net.weta.components.communication.messaging.Message;
import net.weta.components.communication.reflect.ReflectMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageHandlerCache implements IMessageHandler {

    private final IMessageHandler _messageHandler;

    private CacheManager _cacheManager;

    private static final Logger LOG = LogManager.getLogger(MessageHandlerCache.class);

    private static final int CACHE_OFF = 0;

    private static final int CACHE_ON = 1;

    public MessageHandlerCache(IMessageHandler messageHandler) throws Exception {
        _messageHandler = messageHandler;
        _cacheManager = CacheManager.getInstance();
        if (!_cacheManager.cacheExists("ingrid-cache") && !_cacheManager.cacheExists("default")) {
            final Cache cache = new Cache("default", 1000, false, false, 600, 600);
            _cacheManager.addCache(cache);
        }
    }

    @Override
    public Message handleMessage(Message message) {
        long start = System.currentTimeMillis();
        int cacheKey = message.hashCode();
        if (message instanceof ReflectMessage) {
            cacheKey = ((ReflectMessage) message).hashCode();
        }
        Message ret = null;
        int status = message.toString().indexOf("cache: false") > -1 ? CACHE_OFF : CACHE_ON;
        final Cache cache = getCache();
        switch (status) {
        case CACHE_OFF:
            if (LOG.isDebugEnabled()) {
                LOG.debug("cache option is turned off. searching started...");
            }
            break;
        case CACHE_ON:
            if (LOG.isDebugEnabled()) {
                LOG.debug("cache option is turned on. search element in cache...");
            }
            Element element = getFromCache(cache, cacheKey);
            if (element != null) {
                ret = (Message) element.getValue();
                // set new id
                ret.setId(message.getId());
            }
            break;
        }

        if (status == CACHE_OFF || ret == null) {
            // not found in cache
            ret = _messageHandler.handleMessage(message);
            if (cache != null) {
                cache.put(new Element(cacheKey, ret));
            }
        }
        long end = System.currentTimeMillis();
        if(LOG.isDebugEnabled()) {
            LOG.debug("time to handle message: " + (end - start) + " ms.");
        }
        
        return ret;
    }

    private Element getFromCache(final Cache cache, int cacheKey) {
        Element element = null;
        try {
            if (cache != null) {
                element = cache.get(cacheKey);
            }
        } catch (Exception e) {
            LOG.error("error while searching in cache", e);
        }
        if (LOG.isDebugEnabled()) {
            if (element != null) {
                LOG.debug("found element in cache, with cacheKey: " + cacheKey);
            } else {
                LOG.debug("dont found element in cache, with cacheKey: " + cacheKey);
            }
        }
        return element;
    }

    private Cache getCache() {
        Cache cache = _cacheManager.getCache("ingrid-cache");
        if (cache == null) {
            cache = _cacheManager.getCache("default");
        }
        return cache;
    }
}
