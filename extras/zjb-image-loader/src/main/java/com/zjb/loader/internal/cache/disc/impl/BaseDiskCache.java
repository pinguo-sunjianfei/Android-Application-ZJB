/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.zjb.loader.internal.cache.disc.impl;

import android.graphics.Bitmap;

import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.core.util.NameGeneratorUtil;
import com.zjb.loader.internal.cache.disc.DiskCache;
import com.zjb.loader.internal.cache.disc.naming.FileNameGenerator;
import com.zjb.loader.internal.utils.IoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base disk cache.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see FileNameGenerator
 * @since 1.0.0
 */
public class BaseDiskCache implements DiskCache {
    /**
     * {@value
     */
    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 Kb
    /**
     * {@value
     */
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    /**
     * {@value
     */
    public static final int DEFAULT_COMPRESS_QUALITY = 100;

    private static final String ERROR_ARG_NULL = " argument must be not null";
    private static final String TEMP_IMAGE_POSTFIX = ".tmp";

    protected final File cacheDir;
    protected final File reserveCacheDir;

    protected int bufferSize = DEFAULT_BUFFER_SIZE;

    protected Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
    protected int compressQuality = DEFAULT_COMPRESS_QUALITY;

    /**
     * @param cacheDir Directory for file caching
     */
    public BaseDiskCache(File cacheDir) {
        this(cacheDir, null);
    }


    /**
     * @param cacheDir        Directory for file caching
     * @param reserveCacheDir null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
     */
    public BaseDiskCache(File cacheDir, File reserveCacheDir) {
        if (cacheDir == null) {
            throw new IllegalArgumentException("cacheDir" + ERROR_ARG_NULL);
        }
        this.cacheDir = cacheDir;
        this.reserveCacheDir = reserveCacheDir;
    }

    @Override
    public File getDirectory() {
        return cacheDir;
    }

    @Override
    public File get(String imageUri) {
        return getFile(imageUri);
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        File imageFile = getFile(imageUri);
        File tmpFile = new File(imageFile.getAbsolutePath() + TEMP_IMAGE_POSTFIX);
        boolean loaded = false;
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile), bufferSize);
            try {
                loaded = IoUtils.copyStream(imageStream, os, listener, bufferSize);
            } finally {
                IoUtils.closeSilently(os);
            }
        } finally {
            if (loaded && !tmpFile.renameTo(imageFile)) {
                loaded = false;
            }
            if (!loaded) {
                tmpFile.delete();
            }
        }
        return loaded;
    }

    @Override
    public boolean saveByCacheKey(final String cacheKey, final Bitmap bitmap) throws IOException {
        boolean savedSuccessfully = false;
        try {
            File imageFile = getFileByCacheKey(cacheKey);
            if (null != imageFile && imageFile.exists() && imageFile.length() > 100) {
                return true;
            }
            OutputStream os = new FileOutputStream(imageFile);
            try {
                savedSuccessfully = bitmap.compress(compressFormat, compressQuality, os);
            } finally {
                IoUtils.closeSilently(os);
                if (!savedSuccessfully) {
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savedSuccessfully;
    }

    @Override
    public boolean saveRawData(String cacheKey,InputStream stream) {
        return false;
    }

    @Override
    public boolean save(final String imageUri, final Bitmap bitmap) throws IOException {
        ZjbImageLoader.sImageLoaderConfiguration.taskExecutorForCachedImages.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean savedSuccessfully = false;
                    File imageFile = getFile(imageUri);
                    OutputStream os = new FileOutputStream(imageFile);
                    try {
                        savedSuccessfully = bitmap.compress(compressFormat, compressQuality, os);
                    } finally {
                        IoUtils.closeSilently(os);
                        if (!savedSuccessfully) {
                            if (imageFile.exists()) {
                                imageFile.delete();
                            }
                        }
                    }
                    bitmap.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return true;
    }

    @Override
    public boolean remove(String imageUri) {
        return getFile(imageUri).delete();
    }

    @Override
    public void close() {
        // Nothing to do
    }

    @Override
    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }


    public File getFile(String imageUri) {
        String cacheKey = NameGeneratorUtil.generateCacheKey(imageUri);
        return getFileByCacheKey(cacheKey);
    }

    @Override
    public File getFileByCacheKey(String cacheKey) {
        File dir = cacheDir;
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            if (reserveCacheDir != null && (reserveCacheDir.exists() || reserveCacheDir.mkdirs())) {
                dir = reserveCacheDir;
            }
        }
        return new File(dir, cacheKey);
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
    }

    public void setCompressQuality(int compressQuality) {
        this.compressQuality = compressQuality;
    }

    @Override
    public boolean save(String cacheKey) throws IOException {
        return false;
    }

}