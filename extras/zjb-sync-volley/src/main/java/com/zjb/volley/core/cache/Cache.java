package com.zjb.volley.core.cache;

import java.util.Collections;
import java.util.Map;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public interface Cache {
    Cache.Entry get(String key);

    void put(String key, Cache.Entry value);

    void initialize();

    void invalidate(String var1, boolean var2);

    void remove(String var1);

    void clear();

    public static class Entry {
        public byte[] data;
        public String etag;
        public long serverDate;
        public long lastModified;
        public long ttl;
        public long softTtl;
        public Map<String, String> responseHeaders = Collections.emptyMap();

        public Entry() {
        }

        public boolean isExpired() {
            return this.ttl < System.currentTimeMillis();
        }

        public boolean refreshNeeded() {
            return this.softTtl < System.currentTimeMillis();
        }
    }
}
