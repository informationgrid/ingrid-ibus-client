package de.ingrid.ibus.client;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CacheableInvocationHandler implements InvocationHandler {

    private CacheManager _cacheManager;

    private Cache _cache;

    private static final Logger LOG = Logger.getLogger(CacheableInvocationHandler.class);

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
            }
            object = _defaultHandler.invoke(proxy, method, args);
            _cache.put(new Element(cacheKey, (Serializable) object));
        } else {
            object = element.getValue();
            if (LOG.isDebugEnabled()) {
                LOG.debug("found element in cache, with cacheKey: " + cacheKey);
            }
        }
        return object;
    }

    private String computeCacheKey(Object proxy, Method method, Object[] args) {
        return method.toString() + "_" + Arrays.hashCode(args);
    }

    private Element getFromCache(String cacheKey) {
        Element element = null;
        try {
            element = _cache.get(cacheKey);
        } catch (Exception e) {
            LOG.log(Level.ERROR, "can not load element from cache", e);
        }
        return element;
    }
}
