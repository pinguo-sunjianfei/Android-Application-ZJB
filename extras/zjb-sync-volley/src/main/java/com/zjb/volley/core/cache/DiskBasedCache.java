package com.zjb.volley.core.cache;

/**
 * time: 2015/8/19
 * description:
 *
 * @author sunjianfei
 */

import android.os.SystemClock;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class DiskBasedCache implements Cache {
    private final Map<String, CacheHeader> mEntries;
    private long mTotalSize;
    private final File mRootDirectory;
    private final int mMaxCacheSizeInBytes;
    private static final int DEFAULT_DISK_USAGE_BYTES = 5242880;
    private static final float HYSTERESIS_FACTOR = 0.9F;
    private static final int CACHE_MAGIC = 538247942;

    public DiskBasedCache(File rootDirectory, int maxCacheSizeInBytes) {
        this.mEntries = new LinkedHashMap(16, 0.75F, true);
        this.mTotalSize = 0L;
        this.mRootDirectory = rootDirectory;
        this.mMaxCacheSizeInBytes = maxCacheSizeInBytes;
    }

    public DiskBasedCache(File rootDirectory) {
        this(rootDirectory, DEFAULT_DISK_USAGE_BYTES);
    }

    public synchronized void clear() {
        File[] files = this.mRootDirectory.listFiles();
        if (files != null) {
            File[] arr$ = files;
            int len$ = files.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                File file = arr$[i$];
                file.delete();
            }
        }

        this.mEntries.clear();
        this.mTotalSize = 0L;
    }

    public synchronized Entry get(String key) {
        DiskBasedCache.CacheHeader entry = (DiskBasedCache.CacheHeader) this.mEntries.get(key);
        if (entry == null) {
            return null;
        } else {
            File file = this.getFileForKey(key);
            DiskBasedCache.CountingInputStream cis = null;

            Entry var6;
            try {
                cis = new DiskBasedCache.CountingInputStream(new BufferedInputStream(new FileInputStream(file)));
                DiskBasedCache.CacheHeader.readHeader(cis);
                byte[] e = streamToBytes(cis, (int) (file.length() - (long) cis.bytesRead));
                var6 = entry.toCacheEntry(e);
                return var6;
            } catch (IOException var18) {
                this.remove(key);
                var6 = null;
                return var6;
            } catch (NegativeArraySizeException var19) {
                this.remove(key);
                var6 = null;
            } finally {
                if (cis != null) {
                    try {
                        cis.close();
                    } catch (IOException var17) {
                        return null;
                    }
                }

            }

            return var6;
        }
    }

    public synchronized void initialize() {
        if (!this.mRootDirectory.exists()) {
            if (!this.mRootDirectory.mkdirs()) {
            }

        } else {
            File[] files = this.mRootDirectory.listFiles();
            if (files != null) {
                File[] arr$ = files;
                int len$ = files.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    File file = arr$[i$];
                    BufferedInputStream fis = null;

                    try {
                        fis = new BufferedInputStream(new FileInputStream(file));
                        DiskBasedCache.CacheHeader ignored = DiskBasedCache.CacheHeader.readHeader(fis);
                        ignored.size = file.length();
                        this.putEntry(ignored.key, ignored);
                    } catch (IOException var16) {
                        if (file != null) {
                            file.delete();
                        }
                    } finally {
                        try {
                            if (fis != null) {
                                fis.close();
                            }
                        } catch (IOException var15) {
                            ;
                        }

                    }
                }

            }
        }
    }

    public synchronized void invalidate(String key, boolean fullExpire) {
        Entry entry = this.get(key);
        if (entry != null) {
            entry.softTtl = 0L;
            if (fullExpire) {
                entry.ttl = 0L;
            }

            this.put(key, entry);
        }

    }

    public synchronized void put(String key, Entry entry) {
        this.pruneIfNeeded(entry.data.length);
        File file = this.getFileForKey(key);

        try {
            BufferedOutputStream deleted1 = new BufferedOutputStream(new FileOutputStream(file));
            DiskBasedCache.CacheHeader e = new DiskBasedCache.CacheHeader(key, entry);
            boolean success = e.writeHeader(deleted1);
            if (!success) {
                deleted1.close();
                throw new IOException();
            } else {
                deleted1.write(entry.data);
                deleted1.close();
                this.putEntry(key, e);
            }
        } catch (IOException var7) {
            boolean deleted = file.delete();
            if (!deleted) {
            }

        }
    }

    public synchronized void remove(String key) {
        boolean deleted = this.getFileForKey(key).delete();
        this.removeEntry(key);

    }

    private String getFilenameForKey(String key) {
        int firstHalfLength = key.length() / 2;
        String localFilename = String.valueOf(key.substring(0, firstHalfLength).hashCode());
        localFilename = localFilename + String.valueOf(key.substring(firstHalfLength).hashCode());
        return localFilename;
    }

    public File getFileForKey(String key) {
        return new File(this.mRootDirectory, this.getFilenameForKey(key));
    }

    private void pruneIfNeeded(int neededSpace) {
        if (this.mTotalSize + (long) neededSpace >= (long) this.mMaxCacheSizeInBytes) {

            long before = this.mTotalSize;
            int prunedFiles = 0;
            long startTime = SystemClock.elapsedRealtime();
            Iterator iterator = this.mEntries.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                DiskBasedCache.CacheHeader e = (DiskBasedCache.CacheHeader) entry.getValue();
                boolean deleted = this.getFileForKey(e.key).delete();
                if (deleted) {
                    this.mTotalSize -= e.size;
                }

                iterator.remove();
                ++prunedFiles;
                if ((float) (this.mTotalSize + (long) neededSpace) < (float) this.mMaxCacheSizeInBytes * HYSTERESIS_FACTOR) {
                    break;
                }
            }

        }
    }

    private void putEntry(String key, DiskBasedCache.CacheHeader entry) {
        if (!this.mEntries.containsKey(key)) {
            this.mTotalSize += entry.size;
        } else {
            DiskBasedCache.CacheHeader oldEntry = this.mEntries.get(key);
            this.mTotalSize += entry.size - oldEntry.size;
        }

        this.mEntries.put(key, entry);
    }

    private void removeEntry(String key) {
        DiskBasedCache.CacheHeader entry = this.mEntries.get(key);
        if (entry != null) {
            this.mTotalSize -= entry.size;
            this.mEntries.remove(key);
        }

    }

    private static byte[] streamToBytes(InputStream in, int length) throws IOException {
        byte[] bytes = new byte[length];

        int count;
        int pos;
        for (pos = 0; pos < length && (count = in.read(bytes, pos, length - pos)) != -1; pos += count) {
            ;
        }

        if (pos != length) {
            throw new IOException("Expected " + length + " bytes, read " + pos + " bytes");
        } else {
            return bytes;
        }
    }

    private static int read(InputStream is) throws IOException {
        int b = is.read();
        if (b == -1) {
            throw new EOFException();
        } else {
            return b;
        }
    }

    static void writeInt(OutputStream os, int n) throws IOException {
        os.write(n >> 0 & 255);
        os.write(n >> 8 & 255);
        os.write(n >> 16 & 255);
        os.write(n >> 24 & 255);
    }

    static int readInt(InputStream is) throws IOException {
        byte n = 0;
        int n1 = n | read(is) << 0;
        n1 |= read(is) << 8;
        n1 |= read(is) << 16;
        n1 |= read(is) << 24;
        return n1;
    }

    static void writeLong(OutputStream os, long n) throws IOException {
        os.write((byte) ((int) (n >>> 0)));
        os.write((byte) ((int) (n >>> 8)));
        os.write((byte) ((int) (n >>> 16)));
        os.write((byte) ((int) (n >>> 24)));
        os.write((byte) ((int) (n >>> 32)));
        os.write((byte) ((int) (n >>> 40)));
        os.write((byte) ((int) (n >>> 48)));
        os.write((byte) ((int) (n >>> 56)));
    }

    static long readLong(InputStream is) throws IOException {
        long n = 0L;
        n |= ((long) read(is) & 255L) << 0;
        n |= ((long) read(is) & 255L) << 8;
        n |= ((long) read(is) & 255L) << 16;
        n |= ((long) read(is) & 255L) << 24;
        n |= ((long) read(is) & 255L) << 32;
        n |= ((long) read(is) & 255L) << 40;
        n |= ((long) read(is) & 255L) << 48;
        n |= ((long) read(is) & 255L) << 56;
        return n;
    }

    static void writeString(OutputStream os, String s) throws IOException {
        byte[] b = s.getBytes("UTF-8");
        writeLong(os, (long) b.length);
        os.write(b, 0, b.length);
    }

    static String readString(InputStream is) throws IOException {
        int n = (int) readLong(is);
        byte[] b = streamToBytes(is, n);
        return new String(b, "UTF-8");
    }

    static void writeStringStringMap(Map<String, String> map, OutputStream os) throws IOException {
        if (map != null) {
            writeInt(os, map.size());
            Iterator i$ = map.entrySet().iterator();

            while (i$.hasNext()) {
                Map.Entry entry = (Map.Entry) i$.next();
                writeString(os, (String) entry.getKey());
                writeString(os, (String) entry.getValue());
            }
        } else {
            writeInt(os, 0);
        }

    }

    static Map<String, String> readStringStringMap(InputStream is) throws IOException {
        int size = readInt(is);
        Object result = size == 0 ? Collections.emptyMap() : new HashMap(size);

        for (int i = 0; i < size; ++i) {
            String key = readString(is).intern();
            String value = readString(is).intern();
            ((Map) result).put(key, value);
        }

        return (Map) result;
    }

    private static class CountingInputStream extends FilterInputStream {
        private int bytesRead;

        private CountingInputStream(InputStream in) {
            super(in);
            this.bytesRead = 0;
        }

        public int read() throws IOException {
            int result = super.read();
            if (result != -1) {
                ++this.bytesRead;
            }

            return result;
        }

        public int read(byte[] buffer, int offset, int count) throws IOException {
            int result = super.read(buffer, offset, count);
            if (result != -1) {
                this.bytesRead += result;
            }

            return result;
        }
    }

    static class CacheHeader {
        public long size;
        public String key;
        public String etag;
        public long serverDate;
        public long lastModified;
        public long ttl;
        public long softTtl;
        public Map<String, String> responseHeaders;

        private CacheHeader() {
        }

        public CacheHeader(String key, Entry entry) {
            this.key = key;
            this.size = (long) entry.data.length;
            this.etag = entry.etag;
            this.serverDate = entry.serverDate;
            this.lastModified = entry.lastModified;
            this.ttl = entry.ttl;
            this.softTtl = entry.softTtl;
            this.responseHeaders = entry.responseHeaders;
        }

        public static DiskBasedCache.CacheHeader readHeader(InputStream is) throws IOException {
            DiskBasedCache.CacheHeader entry = new DiskBasedCache.CacheHeader();
            int magic = DiskBasedCache.readInt(is);
            if (magic != CACHE_MAGIC) {
                throw new IOException();
            } else {
                entry.key = DiskBasedCache.readString(is);
                entry.etag = DiskBasedCache.readString(is);
                if (entry.etag.equals("")) {
                    entry.etag = null;
                }

                entry.serverDate = DiskBasedCache.readLong(is);
                entry.lastModified = DiskBasedCache.readLong(is);
                entry.ttl = DiskBasedCache.readLong(is);
                entry.softTtl = DiskBasedCache.readLong(is);
                entry.responseHeaders = DiskBasedCache.readStringStringMap(is);
                return entry;
            }
        }

        public Entry toCacheEntry(byte[] data) {
            Entry e = new Entry();
            e.data = data;
            e.etag = this.etag;
            e.serverDate = this.serverDate;
            e.lastModified = this.lastModified;
            e.ttl = this.ttl;
            e.softTtl = this.softTtl;
            e.responseHeaders = this.responseHeaders;
            return e;
        }

        public boolean writeHeader(OutputStream os) {
            try {
                DiskBasedCache.writeInt(os, CACHE_MAGIC);
                DiskBasedCache.writeString(os, this.key);
                DiskBasedCache.writeString(os, this.etag == null ? "" : this.etag);
                DiskBasedCache.writeLong(os, this.serverDate);
                DiskBasedCache.writeLong(os, this.lastModified);
                DiskBasedCache.writeLong(os, this.ttl);
                DiskBasedCache.writeLong(os, this.softTtl);
                DiskBasedCache.writeStringStringMap(this.responseHeaders, os);
                os.flush();
                return true;
            } catch (IOException var3) {
                return false;
            }
        }
    }
}

