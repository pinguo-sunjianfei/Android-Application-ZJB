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
package com.zjb.loader.internal.utils;

import android.text.TextUtils;

import java.io.File;

import com.zjb.loader.internal.cache.disc.DiskCache;

/**
 * Utility for convenient work with disk cache.<br />
 * <b>NOTE:</b> This utility works with file system so avoid using it on application main thread.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.8.0
 */
public final class DiskCacheUtils {

    private DiskCacheUtils() {
    }

    /**
     * Returns {@link File} of cached image or <b>null</b> if image was not cached in disk cache
     */
    public static File findInCache(String imageUri, DiskCache diskCache) {
        File image = diskCache.get(imageUri);
        return image != null && image.exists() ? image : null;
    }

    /**
     * Removed cached image file from disk cache (if image was cached in disk cache before)
     *
     * @return <b>true</b> - if cached image file existed and was deleted; <b>false</b> - otherwise.
     */
    public static boolean removeFromCache(String imageUri, DiskCache diskCache) {
        File image = diskCache.get(imageUri);
        return image != null && image.exists() && image.delete();
    }

    /**
     * 根据宽高信息将原来的url转变成?width=1080&height=1920
     *
     * @param url    原来的url
     * @param width  缓存的宽度
     * @param height 缓存的高度
     * @return 添加宽高信息的url
     */
    public static String encodeURL(String url, int width, int height) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        if (width <= 0 && height <= 0) {
            return url;
        }
        StringBuilder builder = new StringBuilder(url);
        if (!builder.toString().contains("?")) {
            builder.append("?");
        }
        url = builder.toString();
        if (!url.endsWith("&") && !url.endsWith("?")) {
            builder.append("&");
        }
        builder.append("width=")
                .append(width)
                .append("&")
                .append("height=")
                .append(height);
        return builder.toString();
    }
}
