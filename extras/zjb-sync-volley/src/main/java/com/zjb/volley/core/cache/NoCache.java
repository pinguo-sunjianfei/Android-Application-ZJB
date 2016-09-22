package com.zjb.volley.core.cache;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */

public class NoCache implements Cache {
    public NoCache() {
    }

    public void clear() {
    }

    public Entry get(String key) {
        return null;
    }

    public void put(String key, Entry entry) {
    }

    public void invalidate(String key, boolean fullExpire) {
    }

    public void remove(String key) {
    }

    public void initialize() {
    }
}
