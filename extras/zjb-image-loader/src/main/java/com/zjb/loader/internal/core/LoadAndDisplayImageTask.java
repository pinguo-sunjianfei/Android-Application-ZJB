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
package com.zjb.loader.internal.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.zjb.loader.internal.core.assist.FailReason;
import com.zjb.loader.internal.core.assist.FailReason.FailType;
import com.zjb.loader.internal.core.assist.ImageScaleType;
import com.zjb.loader.internal.core.assist.ImageSize;
import com.zjb.loader.internal.core.assist.LoadedFrom;
import com.zjb.loader.internal.core.assist.ViewScaleType;
import com.zjb.loader.internal.core.decode.ImageDecoder;
import com.zjb.loader.internal.core.decode.ImageDecodingInfo;
import com.zjb.loader.internal.core.download.ImageDownloader;
import com.zjb.loader.internal.core.download.ImageDownloader.Scheme;
import com.zjb.loader.internal.core.imageaware.ImageAware;
import com.zjb.loader.internal.core.listener.ImageLoadingListener;
import com.zjb.loader.internal.core.listener.ImageLoadingProgressListener;
import com.zjb.loader.internal.utils.BitmapUtils;
import com.zjb.loader.internal.utils.DiskCacheUtils;
import com.zjb.loader.internal.utils.IoUtils;
import com.zjb.loader.internal.utils.L;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Presents load'n'display image task. Used to load image from Internet or file system, decode it to {@link Bitmap}, and
 * display it in {@link com.zjb.loader.internal.core.imageaware.ImageAware} using {@link DisplayBitmapTask}.
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoaderConfiguration
 * @see ImageLoadingInfo
 * @since 1.3.1
 */
final class LoadAndDisplayImageTask implements Runnable, IoUtils.CopyListener {

    private static final String LOG_WAITING_FOR_RESUME = "ZjbImageLoader is paused. Waiting...  [%s]";
    private static final String LOG_RESUME_AFTER_PAUSE = ".. Resume loading [%s]";
    private static final String LOG_DELAY_BEFORE_LOADING = "Delay %d ms before loading...  [%s]";
    private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
    private static final String LOG_WAITING_FOR_IMAGE_LOADED = "Image already is loading. Waiting... [%s]";
    private static final String LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING = "...Get cached bitmap from memory after waiting. [%s]";
    private static final String LOG_LOAD_IMAGE_FROM_NETWORK = "Load image from network [%s]";
    private static final String LOG_LOAD_IMAGE_FROM_DISK_CACHE = "Load image from disk cache [%s]";
    private static final String LOG_RESIZE_CACHED_IMAGE_FILE = "Resize image in disk cache [%s]";
    private static final String LOG_PREPROCESS_IMAGE = "PreProcess image before caching in memory [%s]";
    private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
    private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
    private static final String LOG_CACHE_IMAGE_ON_DISK = "Cache image on disk [%s]";
    private static final String LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISK = "Process image before cache on disk [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";
    private static final String LOG_TASK_INTERRUPTED = "Task was interrupted [%s]";

    private static final String ERROR_NO_IMAGE_STREAM = "No stream for image [%s]";
    private static final String ERROR_PRE_PROCESSOR_NULL = "Pre-processor returned null [%s]";
    private static final String ERROR_POST_PROCESSOR_NULL = "Post-processor returned null [%s]";
    private static final String ERROR_PROCESSOR_FOR_DISK_CACHE_NULL = "Bitmap processor for disk cache returned null [%s]";

    private final ImageLoaderEngine engine;
    private final ImageLoadingInfo imageLoadingInfo;
    private final Handler handler;

    // Helper references
    private final ImageLoaderConfiguration configuration;
    private final ImageDownloader downloader;
    private final ImageDownloader networkDeniedDownloader;
    private final ImageDownloader slowNetworkDownloader;
    private final ImageDecoder decoder;
    protected String uri;
    private final String memoryCacheKey;
    final ImageAware imageAware;
    private final ImageSize targetSize;
    final DisplayImageOptions options;
    final ImageLoadingListener listener;
    final ImageLoadingProgressListener progressListener;
    private final boolean syncLoading;

    // State vars
    private LoadedFrom loadedFrom = LoadedFrom.NETWORK;

    public LoadAndDisplayImageTask(ImageLoaderEngine engine, ImageLoadingInfo imageLoadingInfo, Handler handler) {
        this.engine = engine;
        this.imageLoadingInfo = imageLoadingInfo;
        this.handler = handler;
        configuration = engine.configuration;
        downloader = configuration.downloader;
        networkDeniedDownloader = configuration.networkDeniedDownloader;
        slowNetworkDownloader = configuration.slowNetworkDownloader;
        decoder = configuration.decoder;
        uri = imageLoadingInfo.uri;
        memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        imageAware = imageLoadingInfo.imageAware;
        targetSize = imageLoadingInfo.targetSize;
        options = imageLoadingInfo.options;
        listener = imageLoadingInfo.listener;
        progressListener = imageLoadingInfo.progressListener;
        syncLoading = options.isSyncLoading();
    }

    @Override
    public void run() {
        //1.处理在滑动的时候不加载图片，只有在idle状态之下才会加载图片
        AtomicBoolean pause = engine.getPause();
        if (pause.get()) {
            synchronized (engine.pauseLock) {
                try {
                    engine.pauseLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Bitmap bmp;
        try {
            checkTaskNotActual();
            bmp = configuration.memoryCache.get(memoryCacheKey);
            if (bmp == null || bmp.isRecycled()) {
                bmp = tryLoadBitmap();
                if (bmp == null) return; // listener callback already was fired
                checkTaskNotActual();
                checkTaskInterrupted();
                if (options.shouldPreProcess()) {
                    bmp = options.getPreProcessor().process(bmp);
                }
                if (bmp != null && options.isCacheInMemory()) {
                    configuration.memoryCache.put(memoryCacheKey, bmp);
                }
            } else {
                loadedFrom = LoadedFrom.MEMORY_CACHE;
            }

            if (bmp != null && options.shouldPostProcess()) {
                bmp = options.getPostProcessor().process(bmp);
            }
            checkTaskNotActual();
            checkTaskInterrupted();
        } catch (TaskCancelledException e) {
            fireCancelEvent();
            return;
        }

        DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(bmp, imageLoadingInfo, engine, loadedFrom);
        runTask(displayBitmapTask, syncLoading, handler, engine);

    }

    /**
     * @return <b>true</b> - if task should be interrupted; <b>false</b> - otherwise
     */
    private boolean waitIfPaused() {
        AtomicBoolean pause = engine.getPause();
        if (pause.get()) {
            synchronized (engine.getPauseLock()) {
                if (pause.get()) {
                    try {
                        engine.getPauseLock().wait();
                    } catch (InterruptedException e) {
                        return true;
                    }
                }
            }
        }
        return isTaskNotActual();
    }

    /**
     * @return <b>true</b> - if task should be interrupted; <b>false</b> - otherwise
     */
    private boolean delayIfNeed() {
        if (options.shouldDelayBeforeLoading()) {
            try {
                Thread.sleep(options.getDelayBeforeLoading());
            } catch (InterruptedException e) {
                return true;
            }
            return isTaskNotActual();
        }
        return false;
    }

    private Bitmap tryLoadBitmap() throws TaskCancelledException {
        Bitmap bitmap = null;
        try {
            //1.加上宽高信息
            //2.从sd卡上面得到带宽高的缓存文件
            File imageFile = configuration.diskCache.getFileByCacheKey(memoryCacheKey);
            //3.如果没有带宽高的缓存文件，那么
            if (imageFile != null && imageFile.exists() && imageFile.length() > 0) {
                loadedFrom = LoadedFrom.DISC_CACHE;
                checkTaskNotActual();
                bitmap = decodeImage(Scheme.FILE.wrap(imageFile.getAbsolutePath()));
            }
            if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                loadedFrom = LoadedFrom.NETWORK;
                if (options.isCacheOnDisk()) {
                    bitmap = tryCacheImageOnDisk();
                }
                if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                    fireFailEvent(FailType.DECODING_ERROR, null);
                }
            }
        } catch (IllegalStateException e) {
            fireFailEvent(FailType.NETWORK_DENIED, null);
        } catch (TaskCancelledException e) {
            throw e;
        } catch (OutOfMemoryError e) {
            L.e(e);
            fireFailEvent(FailType.OUT_OF_MEMORY, e);
        } catch (Throwable e) {
            L.e(e);
            fireFailEvent(FailType.UNKNOWN, e);
        }
        return bitmap;
    }

    private Bitmap decodeImage(String imageUri) throws IOException {
        ViewScaleType viewScaleType = imageAware.getScaleType();
        ImageDecodingInfo decodingInfo = new ImageDecodingInfo(memoryCacheKey, imageUri, uri, targetSize, viewScaleType,
                getDownloader(), options);
        return decoder.decode(decodingInfo);
    }

    /**
     * @return <b>true</b> - if image was downloaded successfully; <b>false</b> - otherwise
     */
    private Bitmap tryCacheImageOnDisk() throws TaskCancelledException {
        L.d(LOG_CACHE_IMAGE_ON_DISK, memoryCacheKey);
        Bitmap bitmap = null;
        try {
            bitmap = downloadImage();
        } catch (Exception e) {
            L.e(e);
        }
        return bitmap;
    }

    private Bitmap downloadImage() throws IOException {
        //原始数据
        InputStream is = getDownloader().getStream(uri, options.getExtraForDownloader());
        //转换bitmap的流
        InputStream mBitmapStream = null;
        //用来存文件的流
        InputStream mCacheStream = null;

        if (is == null) {
            L.e(ERROR_NO_IMAGE_STREAM, memoryCacheKey);
            return null;
        } else {
            try {
                //先转成ByteArrayOutputStream 实现InputStream复用
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > -1) {
                    baos.write(buffer, 0, len);
                }

                byte[] bytes = baos.toByteArray();
                mBitmapStream = new ByteArrayInputStream(bytes);

                Bitmap bitmap = null;
                int width = targetSize.getWidth();
                int height = targetSize.getHeight();
                if (width > 0 || height > 0) {
                    bitmap = BitmapUtils.createScaledBitmap(mBitmapStream, width, height, options.getDecodingOptions().inPreferredConfig);
                }
                if (bitmap == null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    bitmap = BitmapFactory.decodeStream(mBitmapStream, null, options);
                }
                if (bitmap != null) {
                    //1.存放在内存当中
                    if (options.isCacheInMemory()) {
                        configuration.memoryCache.put(memoryCacheKey, bitmap);
                    }
                    //2.存放到sd卡上面
                    if (options.isCompress()) {
                        //如果需要缓存压缩后的图片到SD卡
                        if (uri.startsWith("content") || uri.startsWith("http")) {
                            configuration.diskCache.save(memoryCacheKey);
                        }
                    } else {
                        //存入原始数据
                        mCacheStream = new ByteArrayInputStream(bytes);
                        configuration.diskCache.saveRawData(memoryCacheKey, mCacheStream);
                    }
                }
                return bitmap;
            } finally {
                IoUtils.closeSilently(is);
                IoUtils.closeSilently(mBitmapStream);
                IoUtils.closeSilently(mCacheStream);
            }
        }
    }

    /**
     * Decodes image file into Bitmap, resize it and save it back
     */
    private boolean resizeAndSaveImage(int maxWidth, int maxHeight) throws IOException {
        // Decode image file, compress and re-save it
        boolean saved = false;
        String encodeURL = DiskCacheUtils.encodeURL(uri, maxWidth, maxHeight);
        File targetFile = configuration.diskCache.get(uri);
        if (targetFile != null && targetFile.exists()) {
            ImageSize targetImageSize = new ImageSize(maxWidth, maxHeight);
            DisplayImageOptions specialOptions = new DisplayImageOptions.Builder().cloneFrom(options)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
            ImageDecodingInfo decodingInfo = new ImageDecodingInfo(memoryCacheKey,
                    Scheme.FILE.wrap(targetFile.getAbsolutePath()),
                    Scheme.FILE.wrap(targetFile.getAbsolutePath()),
                    targetImageSize, ViewScaleType.FIT_INSIDE,
                    getDownloader(), specialOptions);
            Bitmap bmp = decoder.decode(decodingInfo);
            if (bmp != null && configuration.processorForDiskCache != null) {
                L.d(LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISK, memoryCacheKey);
                bmp = configuration.processorForDiskCache.process(bmp);
                if (bmp == null) {
                    L.e(ERROR_PROCESSOR_FOR_DISK_CACHE_NULL, memoryCacheKey);
                }
            }
            if (bmp != null) {
                saved = configuration.diskCache.save(encodeURL, bmp);
                bmp.recycle();
            }
            if (saved) {
                targetFile.delete();
            }
        }
        return saved;
    }

    @Override
    public boolean onBytesCopied(int current, int total) {
        return syncLoading || fireProgressEvent(current, total);
    }

    /**
     * @return <b>true</b> - if loading should be continued; <b>false</b> - if loading should be interrupted
     */
    private boolean fireProgressEvent(final int current, final int total) {
        if (isTaskInterrupted() || isTaskNotActual()) return false;
        if (progressListener != null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    progressListener.onProgressUpdate(uri, imageAware.getWrappedView(), current, total);
                }
            };
            runTask(r, false, handler, engine);
        }
        return true;
    }

    private void fireFailEvent(final FailType failType, final Throwable failCause) {
        if (syncLoading || isTaskInterrupted() || isTaskNotActual()) return;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (options.shouldShowImageOnFail()) {
                    imageAware.setImageDrawable(options.getImageOnFail(configuration.resources));
                }
                listener.onLoadingFailed(uri, imageAware.getWrappedView(), new FailReason(failType, failCause));
            }
        };
        runTask(r, false, handler, engine);
    }

    private void fireCancelEvent() {
        if (syncLoading || isTaskInterrupted()) return;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                listener.onLoadingCancelled(uri, imageAware.getWrappedView());
            }
        };
        runTask(r, false, handler, engine);
    }

    private ImageDownloader getDownloader() {
        ImageDownloader d;
        if (engine.isNetworkDenied()) {
            d = networkDeniedDownloader;
        } else if (engine.isSlowNetwork()) {
            d = slowNetworkDownloader;
        } else {
            d = downloader;
        }
        return d;
    }

    /**
     * @throws TaskCancelledException if task is not actual (target ImageAware is collected by GC or the image URI of
     *                                this task doesn't match to image URI which is actual for current ImageAware at
     *                                this moment)
     */
    private void checkTaskNotActual() throws TaskCancelledException {
        checkViewCollected();
        checkViewReused();
    }

    /**
     * @return <b>true</b> - if task is not actual (target ImageAware is collected by GC or the image URI of this task
     * doesn't match to image URI which is actual for current ImageAware at this moment)); <b>false</b> - otherwise
     */
    private boolean isTaskNotActual() {
        return isViewCollected() || isViewReused();
    }

    /**
     * @throws TaskCancelledException if target ImageAware is collected
     */
    private void checkViewCollected() throws TaskCancelledException {
        if (isViewCollected()) {
            throw new TaskCancelledException();
        }
    }

    /**
     * @return <b>true</b> - if target ImageAware is collected by GC; <b>false</b> - otherwise
     */
    private boolean isViewCollected() {
        if (imageAware.isCollected()) {
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED, memoryCacheKey);
            return true;
        }
        return false;
    }

    /**
     * @throws TaskCancelledException if target ImageAware is collected by GC
     */
    private void checkViewReused() throws TaskCancelledException {
        if (isViewReused()) {
            throw new TaskCancelledException();
        }
    }

    /**
     * @return <b>true</b> - if current ImageAware is reused for displaying another image; <b>false</b> - otherwise
     */
    private boolean isViewReused() {
        String currentCacheKey = engine.getLoadingUriForView(imageAware);
        // Check whether memory cache key (image URI) for current ImageAware is actual.
        // If ImageAware is reused for another task then current task should be cancelled.
        boolean imageAwareWasReused = !memoryCacheKey.equals(currentCacheKey);
        if (imageAwareWasReused) {
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_REUSED, memoryCacheKey);
            return true;
        }
        return false;
    }

    /**
     * @throws TaskCancelledException if current task was interrupted
     */
    private void checkTaskInterrupted() throws TaskCancelledException {
        if (isTaskInterrupted()) {
            throw new TaskCancelledException();
        }
    }

    /**
     * @return <b>true</b> - if current task was interrupted; <b>false</b> - otherwise
     */
    private boolean isTaskInterrupted() {
        if (Thread.interrupted()) {
            L.d(LOG_TASK_INTERRUPTED, memoryCacheKey);
            return true;
        }
        return false;
    }

    String getLoadingUri() {
        return uri;
    }

    String getMemoryCacheKey() {
        return memoryCacheKey;
    }

    static void runTask(Runnable r, boolean sync, Handler handler, ImageLoaderEngine engine) {
        if (sync) {
            r.run();
        } else if (handler == null) {
            engine.fireCallback(r);
        } else {
            handler.post(r);
        }
    }

    /**
     * Exceptions for case when task is cancelled (thread is interrupted, image view is reused for another task, view is
     * collected by GC).
     *
     * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
     * @since 1.9.1
     */
    class TaskCancelledException extends Exception {
    }
}
