package com.zjb.volley.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.zjb.volley.core.pool.ByteArrayPool;


/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */
public class PoolingByteArrayOutputStream extends ByteArrayOutputStream {
    private static final int DEFAULT_SIZE = 256;
    private final ByteArrayPool mPool;

    public PoolingByteArrayOutputStream(ByteArrayPool pool) {
        this(pool, DEFAULT_SIZE);
    }

    public PoolingByteArrayOutputStream(ByteArrayPool pool, int size) {
        this.mPool = pool;
        this.buf = this.mPool.getBuf(Math.max(size, DEFAULT_SIZE));
    }

    public void close() throws IOException {
        this.mPool.returnBuf(this.buf);
        this.buf = null;
        super.close();
    }

    public void finalize() {
        this.mPool.returnBuf(this.buf);
    }

    private void expand(int i) {
        if (this.count + i > this.buf.length) {
            byte[] newbuf = this.mPool.getBuf((this.count + i) * 2);
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.mPool.returnBuf(this.buf);
            this.buf = newbuf;
        }
    }

    public synchronized void write(byte[] buffer, int offset, int len) {
        this.expand(len);
        super.write(buffer, offset, len);
    }

    public synchronized void write(int oneByte) {
        this.expand(1);
        super.write(oneByte);
    }
}
