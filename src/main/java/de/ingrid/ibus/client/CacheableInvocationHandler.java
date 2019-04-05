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
package de.ingrid.ibus.client;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CacheableInvocationHandler implements InvocationHandler {

    private static final int MAX_DEEP = 50;

    private CacheManager _cacheManager;

    private Cache _cache;

    private static final Logger LOG = LogManager.getLogger(CacheableInvocationHandler.class);

    private final InvocationHandler _defaultHandler;

    public CacheableInvocationHandler(InvocationHandler defaultHandler) throws Exception {
        _defaultHandler = defaultHandler;
        _cacheManager = CacheManager.getInstance();
        _cache = _cacheManager.getCache("ingrid-cache");
        if (_cache == null) {
            if (!_cacheManager.cacheExists("default")) {
                _cache = new Cache("default", 1000, false, false, 600, 600);
                _cacheManager.addCache(_cache);
            } else {
                _cache = _cacheManager.getCache("default");
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object object = null;
        String cacheKey = computeCacheKey(proxy, method, args);
        Element element = getFromCache(cacheKey);
        if (element == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("do not found element in cache, with cacheKey: " + cacheKey);
                LOG.debug("call [" + method + "] method with default handler.");
            }
            object = _defaultHandler.invoke(proxy, method, args);
            if (object != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("put element [" + object + "] in cache with cache key: " + cacheKey);
                }
                _cache.put(new Element(cacheKey, (Serializable) object));
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("default handler return null for method: " + method);
                }
            }
        } else {
            object = element.getValue();
            if (LOG.isDebugEnabled()) {
                LOG.debug("found element [" + object + "] in cache, with cacheKey: " + cacheKey);
            }
        }
        return object;
    }

    private String computeCacheKey(Object proxy, Method method, Object[] args) {
        // return method.toString() + "_" + Arrays.deepHashCode(args);
        return method.toString() + "_" + computeHashcode(args, 1);
    }

    private int computeHashcode(Object object, int deep) {
        final int prime = 31;
        int result = 1;
        if ((object instanceof Object[]) && deep < MAX_DEEP) {
            Object[] objects = (Object[]) object;
            for (Object object2 : objects) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("compute hashcode recursive with deep: " + deep);
                }
                result = prime * result + computeHashcode(object2, deep + 1);
            }
        } else {
            result = prime * result + ((object == null) ? 0 : object.hashCode());
        }
        return result;
    }

    private Element getFromCache(String cacheKey) {
        Element element = null;
        try {
            element = _cache.get(cacheKey);
        } catch (Exception e) {
            LOG.error("can not load element from cache", e);
        }
        return element;
    }
}
