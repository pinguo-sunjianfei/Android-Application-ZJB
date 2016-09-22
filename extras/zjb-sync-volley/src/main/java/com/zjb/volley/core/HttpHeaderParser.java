package com.zjb.volley.core;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import java.util.Map;

import com.zjb.volley.core.cache.Cache;
import com.zjb.volley.core.response.NetworkResponse;


public class HttpHeaderParser {
    public HttpHeaderParser() {
    }

    public static Cache.Entry parseCacheHeaders(NetworkResponse response) {
        long now = System.currentTimeMillis();
        Map headers = response.headers;
        long serverDate = 0L;
        long lastModified = 0L;
        long serverExpires = 0L;
        long softExpire = 0L;
        long finalExpire = 0L;
        long maxAge = 0L;
        long staleWhileRevalidate = 0L;
        boolean hasCacheControl = false;
        boolean mustRevalidate = false;
        String serverEtag = null;
        String headerValue = (String) headers.get("Date");
        if (headerValue != null) {
            serverDate = parseDateAsEpoch(headerValue);
        }

        headerValue = (String) headers.get("Cache-Control");
        if (headerValue != null) {
            hasCacheControl = true;
            String[] entry = headerValue.split(",");

            for (int i = 0; i < entry.length; ++i) {
                String token = entry[i].trim();
                if (token.equals("no-cache") || token.equals("no-store")) {
                    return null;
                }

                if (token.startsWith("max-age=")) {
                    try {
                        maxAge = Long.parseLong(token.substring(8));
                    } catch (Exception e1) {
                        ;
                    }
                } else if (token.startsWith("stale-while-revalidate=")) {
                    try {
                        staleWhileRevalidate = Long.parseLong(token.substring(23));
                    } catch (Exception e2) {
                    }
                } else if (token.equals("must-revalidate") || token.equals("proxy-revalidate")) {
                    mustRevalidate = true;
                }
            }
        }

        headerValue = (String) headers.get("Expires");
        if (headerValue != null) {
            serverExpires = parseDateAsEpoch(headerValue);
        }

        headerValue = (String) headers.get("Last-Modified");
        if (headerValue != null) {
            lastModified = parseDateAsEpoch(headerValue);
        }

        serverEtag = (String) headers.get("ETag");
        if (hasCacheControl) {
            softExpire = now + maxAge * 1000L;
            finalExpire = mustRevalidate ? softExpire : softExpire + staleWhileRevalidate * 1000L;
        } else if (serverDate > 0L && serverExpires >= serverDate) {
            softExpire = now + (serverExpires - serverDate);
            finalExpire = softExpire;
        }

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = finalExpire;
        entry.serverDate = serverDate;
        entry.lastModified = lastModified;
        entry.responseHeaders = headers;
        return entry;
    }

    public static long parseDateAsEpoch(String dateStr) {
        try {
            return DateUtils.parseDate(dateStr).getTime();
        } catch (DateParseException var2) {
            return 0L;
        }
    }

    public static String parseCharset(Map<String, String> headers, String defaultCharset) {
        String contentType = headers.get("Content-Type");
        if (contentType != null) {
            String[] params = contentType.split(";");

            for (int i = 1; i < params.length; ++i) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2 && pair[0].equals("charset")) {
                    return pair[1];
                }
            }
        }

        return defaultCharset;
    }

    public static String parseCharset(Map<String, String> headers) {
        return parseCharset(headers, "ISO-8859-1");
    }
}
