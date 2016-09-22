/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
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
package com.zjb.loader.internal.cache.disc;

import android.graphics.Bitmap;

import com.zjb.loader.internal.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for disk cache
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.9.2
 */
public interface DiskCache {
    /**
     * Returns root directory of disk cache
     *
     * @return Root directory of disk cache
     */
    File getDirectory();

    /**
     * Returns file of cached image
     *
     * @param imageUri Original image URI
     * @return File of cached image or <b>null</b> if image wasn't cached
     */
    File get(String imageUri);

    File getFileByCacheKey(String cacheKey);

    /**
     * Saves image stream in disk cache.
     * Incoming image stream shouldn't be closed in this method.
     *
     * @param imageUri    Original image URI
     * @param imageStream Input stream of image (shouldn't be closed in this method)
     * @param listener    Listener for saving progress, can be ignored if you don't use
     *                    {@linkplain com.zjb.loader.internal.core.listener.ImageLoadingProgressListener
     *                    progress listener} in ZjbImageLoader calls
     * @return <b>true</b> - if image was saved successfully; <b>false</b> - if image wasn't saved in disk cache.
     * @throws java.io.IOException
     */
    boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException;

    /**
     * Saves image bitmap in disk cache.
     *
     * @param imageUri Original image URI
     * @param bitmap   Image bitmap
     * @return <b>true</b> - if bitmap was saved successfully; <b>false</b> - if bitmap wasn't saved in disk cache.
     * @throws IOException
     */
    boolean save(String imageUri, Bitmap bitmap) throws IOException;

    /**
     * Removes image file associated with incoming URI
     *
     * @param imageUri Image URI
     * @return <b>true</b> - if image file is deleted successfully; <b>false</b> - if image file doesn't exist for
     * incoming URI or image file can't be deleted.
     */
    boolean remove(String imageUri);

    /**
     * 从memorycache当中拿到bitmap,然后保存到sd卡上面
     *
     * @param cacheKey 图片对应的内存缓存的key和sd卡上面的缓存key
     * @return 是否保存成功
     * @throws IOException
     */
    boolean save(String cacheKey) throws IOException;

    boolean saveByCacheKey(String cacheKey, Bitmap bitmap) throws IOException;

    /**
     * 保存原始文件
     *
     * @param cacheKey 图片对应的内存缓存的key和sd卡上面的缓存key
     * @param stream   图片的输入流
     * @return 是否保存成功
     * @throws IOException
     */
    boolean saveRawData(String cacheKey, InputStream stream);

    /**
     * Closes disk cache, releases resources.
     */
    void close();

    /**
     * Clears disk cache.
     */
    void clear();
}
