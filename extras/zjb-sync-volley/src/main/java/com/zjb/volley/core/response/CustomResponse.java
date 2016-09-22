package com.zjb.volley.core.response;

import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;

import java.util.Locale;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public class CustomResponse extends BasicHttpResponse {
    private byte[] data;

    public CustomResponse(StatusLine statusline, ReasonPhraseCatalog catalog, Locale locale) {
        super(statusline, catalog, locale);
    }

    public CustomResponse(StatusLine statusline) {
        super(statusline);
    }

    public CustomResponse(ProtocolVersion ver, int code, String reason) {
        super(ver, code, reason);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
