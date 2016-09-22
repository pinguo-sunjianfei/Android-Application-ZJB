package com.zjb.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.zjb.loader.core.cache.CustomDiskCache;
import com.zjb.loader.core.display.BlurBitmapDisplayer;
import com.zjb.loader.core.display.BlurFadeInBitmapDisplayer;
import com.zjb.loader.core.display.CircleBitmapDisplayer;
import com.zjb.loader.core.display.CircleBlurBitmapDisplayer;
import com.zjb.loader.core.display.CircleFadeInBitmapDisplayer;
import com.zjb.loader.core.display.CircleRingBitmapDisplayer;
import com.zjb.loader.core.display.PolygonBitmapDisplayer;
import com.zjb.loader.core.display.RoundedBitmapDisplayer;
import com.zjb.loader.core.display.RoundedBlurBitmapDisplayer;
import com.zjb.loader.core.display.RoundedFadeInBitmapDisplayer;
import com.zjb.loader.core.display.RoundedLomoBitmapDisplayer;
import com.zjb.loader.core.display.RoundedLomoBlurBitmapDisplayer;
import com.zjb.loader.core.display.RoundedLomoFadeInBitmapDisplayer;
import com.zjb.loader.core.imageaware.ImageSwitcherAware;
import com.zjb.loader.core.imageaware.SimpleViewAware;
import com.zjb.loader.core.util.NameGeneratorUtil;
import com.zjb.loader.internal.cache.disc.DiskCache;
import com.zjb.loader.internal.cache.disc.impl.UnlimitedDiskCache;
import com.zjb.loader.internal.cache.memory.MemoryCache;
import com.zjb.loader.internal.cache.memory.impl.LruMemoryCache;
import com.zjb.loader.internal.core.DisplayImageOptions;
import com.zjb.loader.internal.core.ImageLoaderConfiguration;
import com.zjb.loader.internal.core.assist.FailReason;
import com.zjb.loader.internal.core.assist.ImageScaleType;
import com.zjb.loader.internal.core.assist.ImageSize;
import com.zjb.loader.internal.core.assist.QueueProcessingType;
import com.zjb.loader.internal.core.decode.BaseImageDecoder;
import com.zjb.loader.internal.core.display.FadeInBitmapDisplayer;
import com.zjb.loader.internal.core.display.SimpleBitmapDisplayer;
import com.zjb.loader.internal.core.download.BaseImageDownloader;
import com.zjb.loader.internal.core.imageaware.ImageAware;
import com.zjb.loader.internal.core.imageaware.ImageViewAware;
import com.zjb.loader.internal.core.listener.ImageLoadingListener;
import com.zjb.loader.internal.core.listener.ImageLoadingProgressListener;
import com.zjb.loader.internal.utils.ImageSizeUtils;
import com.zjb.loader.internal.utils.L;
import com.zjb.loader.internal.utils.StorageUtils;
import com.zjb.loader.view.AnimationImageView;

import java.io.File;
import java.util.Iterator;
import java.util.Locale;

/**
 * time: 15/6/11
 * description:图片加载对外提供的入口API
 *
 * @author sunjianfei
 */
public class ZjbImageLoader {
    private static final String SUFFIX = "?imageView2/1/w/%d/h/%d";
    public static final int DISPLAY_DEFAULT = 1;
    public static final int DISPLAY_FADE_IN = 2;
    public static final int DISPLAY_ROUND = 3;
    public static final int DISPLAY_ROUND_FADE_IN = 4;
    public static final int DISPLAY_ROUND_VIGNETTE = 5;
    public static final int DISPLAY_ROUND_VIGNETTE_FADE_IN = 6;
    public static final int DISPLAY_CIRCLE = 7;
    public static final int DISPLAY_CIRCLE_FADE_IN = 8;
    public static final int DISPLAY_CIRCLE_RING = 9;
    public static final int DISPLAY_BLUR = 10;
    public static final int DISPLAY_BLUR_FADE_IN = 11;
    public static final int DISPLAY_ROUND_BLUR = 12;
    public static final int DISPLAY_ROUND_BLUR_VIGNETTE = 13;
    public static final int DISPLAY_CIRCLE_BLUR = 14;
    public static final int DISPLAY_POLYGON = 15;
    private static Context mContext;
    private static com.zjb.loader.internal.core.ImageLoader mImageLoader;
    public static ImageLoaderConfiguration sImageLoaderConfiguration;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    private ZjbImageLoader() {
    }

    public static synchronized void init(Context context, String cacheDir) {
        init(context, cacheDir, 1024L * 1024L * 1024L);
    }

    public static synchronized void init(Context context, String cacheDir, long diskCacheSize) {
        mContext = context;
        mImageLoader = com.zjb.loader.internal.core.ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            Options options = new Options();
            options.inPreferredConfig = Config.RGB_565;
            options.inSampleSize = 1;
            sImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(mContext)
                    .threadPriority(Integer.MAX_VALUE)
                    .threadPoolSize(THREAD_POOL_SIZE)
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .diskCache(createDiskCache(context, cacheDir, diskCacheSize))
                    .memoryCache(createMemoryCache(25))
                    .imageDecoder(new BaseImageDecoder(false))
                    .imageDownloader(new BaseImageDownloader(context, 10000, 60000))
                    .defaultDisplayImageOptions(
                            new DisplayImageOptions.Builder()
                                    .cacheInMemory(true)
                                    .cacheOnDisk(true)
                                    .imageScaleType(ImageScaleType.EXACTLY)
                                    .considerExifParams(true)
                                    .decodingOptions(options).build())
                    .build();
            mImageLoader.init(sImageLoaderConfiguration);
        }
        L.writeLogs(false);

    }

    private static MemoryCache createMemoryCache(int availableMemoryPercent) {
        long availableMemory = Runtime.getRuntime().maxMemory();
        return new LruMemoryCache((int) ((float) availableMemory * ((float) availableMemoryPercent / 100.0F)));
    }

    private static DiskCache createDiskCache(Context context, String cacheDir, long diskCacheSize) {
        File reserveCacheDir = StorageUtils.getIndividualCacheDirectory(context);
        File preferredCacheDir = null;
        if (!TextUtils.isEmpty(cacheDir)) {
            preferredCacheDir = new File(cacheDir);
        } else {
            preferredCacheDir = reserveCacheDir;
        }

        DiskCache diskCache = null;

        try {
            diskCache = new CustomDiskCache(preferredCacheDir, reserveCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (diskCache == null) {
            diskCache = new UnlimitedDiskCache(preferredCacheDir, reserveCacheDir);
        }

        return diskCache;
    }

    public static void resume() {
        mImageLoader.resume();
    }

    public static void pause() {
        mImageLoader.pause();
    }

    public static void stop() {
        mImageLoader.stop();
    }

    public static void destroy() {
        if (null != mImageLoader) {
            mImageLoader.destroy();
            mImageLoader = null;
        }
        if (null != sImageLoaderConfiguration) {
            sImageLoaderConfiguration = null;
        }
    }

    public static DiskCache getDiskCache() {
        return mImageLoader.getDiskCache();
    }

    public static MemoryCache getMemoryCache() {
        return mImageLoader.getMemoryCache();
    }

    public static long getUsedDiskCacheSize() {
        long size = 0L;
        DiskCache diskCache = mImageLoader.getDiskCache();
        File dir = diskCache.getDirectory();
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            int length = files.length;

            for (int i = 0; i < length; ++i) {
                File file = files[i];
                if (file.isFile()) {
                    size += file.length();
                }
            }
        }

        return size;
    }

    public static long getUsedMemoryCacheSize() {
        long size = 0L;
        MemoryCache memoryCache = mImageLoader.getMemoryCache();
        Iterator iterator = memoryCache.keys().iterator();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Bitmap bitmap = memoryCache.get(key);
            if (bitmap != null && !bitmap.isRecycled()) {
                size += (long) (bitmap.getHeight() * bitmap.getRowBytes());
            }
        }

        return size;
    }

    public static void clearDiskCache() {
        mImageLoader.clearDiskCache();
    }

    public static void clearMemoryCache() {
        mImageLoader.clearMemoryCache();
    }

    private static ImageSize getMaxImageSize() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        return new ImageSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    private static String getMemoryCacheKey(String uri, ImageAware imageAware) {
        ImageSize targetSize = null;
        if (imageAware == null) {
            targetSize = new ImageSize(0, 0);
        } else {
            ImageSizeUtils.defineTargetSizeForView(imageAware, getMaxImageSize());
        }
        return NameGeneratorUtil.generateCacheKey(uri, targetSize);
    }

    private static String getDiskCacheKey(String uri) {
        return uri;
    }

    public static boolean putMemoryCache(String key, Bitmap bitmap) {
        if (key == null) {
            return false;
        } else {
            MemoryCache memoryCache = mImageLoader.getMemoryCache();
            return memoryCache.put(getMemoryCacheKey(key, null), bitmap);
        }
    }

    public static Bitmap getMemoryCache(String memoryKey) {
        return mImageLoader.getMemoryCache().get(memoryKey);
    }


    public static Bitmap getMemoryCache(String url, View view) {
        if (url == null) {
            return null;
        } else {
            MemoryCache memoryCache = mImageLoader.getMemoryCache();
            String mKey = null;
            if (view == null) {
                mKey = getMemoryCacheKey(url, null);
            } else if (view instanceof ImageView) {
                mKey = getMemoryCacheKey(url, new ImageViewAware((ImageView) view));
            } else {
                mKey = getMemoryCacheKey(url, new SimpleViewAware(view));
            }

            return memoryCache.get(mKey);
        }
    }


    /**
     * 得到硬盘缓存
     *
     * @param url    没有加七牛信息的原始url,这个方法会在这个原始的url之后加上七牛的信息
     * @param width  指定宽度
     * @param height 指定高度
     * @return
     */
    public static String getQiniuDiskCachePath(String url, int width, int height) {
        //1.加上七牛的信息
        if (width != 0 && height != 0 && !TextUtils.isEmpty(url)) {
            if (url.startsWith("http")) {
                url = url + String.format(Locale.US, SUFFIX, width, height);
            }
        }
        //2.得到缓存
        String cacheKey;
        if (width <= 0 || height <= 0) {
            cacheKey = NameGeneratorUtil.generateCacheKey(url, getMaxImageSize());
        } else {
            cacheKey = NameGeneratorUtil.generateCacheKey(url, width, height);
        }
        DiskCache diskCache = mImageLoader.getDiskCache();
        File imageFile = diskCache.getFileByCacheKey(cacheKey);
        if (imageFile != null) {
            return imageFile.getAbsolutePath();
        }
        return null;
    }

    /**
     * 得到七牛的缓存数据
     *
     * @param url
     * @return
     */
    public static String getQiniuDiskCachePath(String url) {
        return getQiniuDiskCachePath(url, 0, 0);
    }

    /**
     * 得到url对应的硬盘缓存数据(url没有加七牛的信息)
     *
     * @param url    原始的url
     * @param width  指定宽度
     * @param height 指定高度
     * @return
     */
    public static String getDiskCachePath(String url, int width, int height) {
        String cacheKey;
        if (width <= 0 || height <= 0) {
            cacheKey = NameGeneratorUtil.generateCacheKey(url, getMaxImageSize());
        } else {
            cacheKey = NameGeneratorUtil.generateCacheKey(url, width, height);
        }
        DiskCache diskCache = mImageLoader.getDiskCache();
        File imageFile = diskCache.getFileByCacheKey(cacheKey);
        if (imageFile != null) {
            return imageFile.getAbsolutePath();
        }
        return null;
    }

    /**
     * 得到url对应的硬盘缓存数据(url没有加七牛的信息)
     *
     * @param url  原始的url
     * @param view 原始的url显示的控件，这个控件是用来计算宽高用的
     * @return
     */
    public static String getDiskCachePath(String url, View view) {
        ImageAware aware;
        if (view instanceof ImageView) {
            aware = new ImageViewAware((ImageView) view);
        } else if (view instanceof ImageSwitcher) {
            aware = new ImageSwitcherAware(view);
        } else {
            aware = new SimpleViewAware(view);
        }
        return getDiskCachePath(url, aware.getWidth(), aware.getHeight());
    }

    private synchronized static void display(String url, View view, DisplayImageOptions displayImageOptions,
                                             ImageLoadingListener imageLoadingListener, ImageLoadingProgressListener imageLoadingProgressListener) {
        try {
            if (view instanceof ImageView) {
                mImageLoader.displayImage(url, new ImageViewAware((ImageView) view), displayImageOptions,
                        imageLoadingListener, imageLoadingProgressListener);
            } else if (view instanceof ImageSwitcher) {
                mImageLoader.displayImage(url, new ImageSwitcherAware(view), displayImageOptions, imageLoadingListener,
                        imageLoadingProgressListener);
            } else {
                mImageLoader.displayImage(url, new SimpleViewAware(view), displayImageOptions, imageLoadingListener,
                        imageLoadingProgressListener);
            }
        } catch (OutOfMemoryError e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    private static void load(String url, ImageLoadingListener imageLoadingListener) {
        load(url, null, null, imageLoadingListener, null);
    }

    private static void load(String url, ImageSize targetImageSize, ImageLoadingListener imageLoadingListener) {
        load(url, targetImageSize, null, imageLoadingListener, null);
    }

    private static void load(String url, DisplayImageOptions displayImageOptions,
                             ImageLoadingListener imageLoadingListener) {
        load(url, null, displayImageOptions, imageLoadingListener, null);
    }

    private static void load(String url, ImageSize targetImageSize, DisplayImageOptions displayImageOptions,
                             ImageLoadingListener imageLoadingListener) {
        load(url, targetImageSize, displayImageOptions, imageLoadingListener, null);
    }

    private static void load(String url, ImageSize targetImageSize, DisplayImageOptions displayImageOptions,
                             ImageLoadingListener imageLoadingListener, ImageLoadingProgressListener imageLoadingProgressListener) {
        try {
            mImageLoader.loadImage(url, targetImageSize, displayImageOptions, imageLoadingListener,
                    imageLoadingProgressListener);
        } catch (OutOfMemoryError e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }

    public static Bitmap loadSync(String url, ImageSize targetImageSize, DisplayImageOptions displayImageOptions) {
        try {
            return mImageLoader.loadImageSync(url, targetImageSize, displayImageOptions);
        } catch (OutOfMemoryError e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }

        return null;
    }

    public static Builder create(String url) {
        Builder builder = new Builder(url);
        return builder;
    }

    public static class Builder {
        private String mUrl;
        private int mDefaultRes;
        private int mFailRes;
        private int mEmptyRes;
        private Drawable mDefaultDrawable;
        private Drawable mFailDrawable;
        private Drawable mEmptyDrawable;
        private int mDisplayType = 1;
        private int mRoundRadius = 5;
        private int mBlurDepth = 10;
        private int mFadeInTime = 300;
        private ImageLoadingListener mImageLoadingListener;
        private ImageLoadingProgressListener mImageLoadingProgressListener;
        private ImageSize mImageSize;
        private ImageScaleType mImageScaleType;
        private Options decodingOptions;
        private float mStrokeWidth;
        private int mRingColor;
        private float mRingPadding;
        private int mQiniuWidth;
        private int mQiniuHeight;
        private float mPolygonHeight;
        private boolean isCompress = true;

        private Builder(String url) {
            this.mImageScaleType = ImageScaleType.EXACTLY;
            this.decodingOptions = new Options();
            this.mUrl = url;
            this.decodingOptions.inPreferredConfig = Config.RGB_565;
            this.decodingOptions.inSampleSize = 1;
        }

        public Builder setQiniu(int width, int height) {
            this.mQiniuHeight = height;
            this.mQiniuWidth = width;
            if (width != 0 && height != 0 && !TextUtils.isEmpty(mUrl)) {
                if (mUrl.startsWith("http")) {
                    mUrl = mUrl + String.format(Locale.US, SUFFIX, width, height);
                }
            }
            return this;
        }

        public Builder setInSampleSize(int inSampleSize) {
            this.decodingOptions.inSampleSize = inSampleSize;
            return this;
        }

        public Builder setBitmapConfig(Config config) {
            this.decodingOptions.inPreferredConfig = config;
            return this;
        }

        public Builder setImageScaleType(ImageScaleType scaleType) {
            this.mImageScaleType = scaleType;
            return this;
        }

        public Builder setDefaultDrawable(Drawable drawable) {
            this.mDefaultDrawable = drawable;
            return this;
        }

        public Builder setFailDrawable(Drawable drawable) {
            this.mFailDrawable = drawable;
            return this;
        }

        public Builder setEmptyDrawable(Drawable drawable) {
            this.mEmptyDrawable = drawable;
            return this;
        }

        public Builder setDefaultRes(int res) {
            this.mDefaultRes = res;
            return this;
        }

        public Builder setFailRes(int res) {
            this.mFailRes = res;
            return this;
        }

        public Builder setEmptyRes(int res) {
            this.mEmptyRes = res;
            return this;
        }

        public Builder setDisplayType(int displayType) {
            this.mDisplayType = displayType;
            return this;
        }

        public Builder setRoundRadius(int roundRadius) {
            this.mRoundRadius = roundRadius;
            return this;
        }

        public Builder setBlurDepth(int blurDepth) {
            this.mBlurDepth = blurDepth;
            return this;
        }

        public Builder setFadeInTime(int fadeInTime) {
            this.mFadeInTime = fadeInTime;
            return this;
        }

        public Builder setImageLoadinglistener(ImageLoadingListener listener) {
            this.mImageLoadingListener = listener;
            return this;
        }

        public Builder setImageLoadingProgressListener(ImageLoadingProgressListener listener) {
            this.mImageLoadingProgressListener = listener;
            return this;
        }

        public Builder setImageSize(ImageSize imageSize) {
            this.mImageSize = imageSize;
            return this;
        }

        public Builder setStrokeWidth(float strokeWidth) {
            this.mStrokeWidth = strokeWidth;
            return this;
        }

        public Builder setRingColor(int color) {
            this.mRingColor = color;
            return this;
        }

        public Builder setRingPadding(float padding) {
            this.mRingPadding = padding;
            return this;
        }

        public Builder setPolygonHeight(float height) {
            mPolygonHeight = height;
            return this;
        }

        public Builder isCompress(boolean isCompress) {
            this.isCompress = isCompress;
            return this;
        }

        private DisplayImageOptions build() {
            if (this.mFailRes <= 0) {
                this.mFailRes = this.mDefaultRes;
            }

            if (this.mEmptyRes <= 0) {
                this.mEmptyRes = this.mDefaultRes;
            }

            if (this.mFailDrawable == null) {
                this.mFailDrawable = this.mDefaultDrawable;
            }

            if (this.mEmptyDrawable == null) {
                this.mEmptyDrawable = this.mDefaultDrawable;
            }

            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder()
                    .showImageOnFail(this.mFailDrawable)
                    .showImageForEmptyUri(this.mEmptyDrawable)
                    .showImageOnLoading(this.mDefaultDrawable)
                    .showImageOnFail(this.mFailRes)
                    .showImageForEmptyUri(this.mEmptyRes)
                    .showImageOnLoading(this.mDefaultRes)
                    .imageScaleType(this.mImageScaleType)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .isCompress(isCompress)
                    .decodingOptions(this.decodingOptions)
                    .considerExifParams(true);
            DisplayImageOptions displayImageOptions = null;
            switch (this.mDisplayType) {
                case DISPLAY_DEFAULT:// 简单
                default:
                    displayImageOptions = builder.displayer(new SimpleBitmapDisplayer()).build();
                    break;
                case DISPLAY_FADE_IN:// 淡入
                    displayImageOptions = builder.displayer(new FadeInBitmapDisplayer(this.mFadeInTime)).build();
                    break;
                case DISPLAY_ROUND:// 圆角矩形
                    displayImageOptions = builder.displayer(new RoundedBitmapDisplayer(this.mRoundRadius)).build();
                    break;
                case DISPLAY_ROUND_FADE_IN:// 圆角矩形淡入
                    displayImageOptions = builder.displayer(
                            new RoundedFadeInBitmapDisplayer(this.mRoundRadius, this.mFadeInTime)).build();
                    break;
                case DISPLAY_ROUND_VIGNETTE:// 圆角阴影(LOMO)
                    displayImageOptions = builder.displayer(new RoundedLomoBitmapDisplayer(this.mRoundRadius)).build();
                    break;
                case DISPLAY_ROUND_VIGNETTE_FADE_IN:// 圆角阴影淡入
                    displayImageOptions = builder.displayer(
                            new RoundedLomoFadeInBitmapDisplayer(this.mRoundRadius, this.mFadeInTime)).build();
                    break;
                case DISPLAY_CIRCLE:// 圆形
                    displayImageOptions = builder.displayer(new CircleBitmapDisplayer()).build();
                    break;
                case DISPLAY_CIRCLE_FADE_IN:// 圆形淡入
                    displayImageOptions = builder.displayer(new CircleFadeInBitmapDisplayer(this.mFadeInTime)).build();
                    break;
                case DISPLAY_CIRCLE_RING:// 圆形带环
                    displayImageOptions = builder.displayer(
                            new CircleRingBitmapDisplayer().setStrokeWidth(mStrokeWidth).setColor(mRingColor)
                                    .setRingPadding(mRingPadding)).build();
                    break;
                case DISPLAY_BLUR:// 高斯模糊
                    displayImageOptions = builder.displayer(new BlurBitmapDisplayer(this.mBlurDepth)).build();
                    break;
                case DISPLAY_BLUR_FADE_IN:// 高斯模糊淡入
                    displayImageOptions = builder.displayer(
                            new BlurFadeInBitmapDisplayer(this.mBlurDepth, this.mFadeInTime)).build();
                    break;
                case DISPLAY_ROUND_BLUR:// 圆角高斯模糊
                    displayImageOptions = builder.displayer(
                            new RoundedBlurBitmapDisplayer(this.mRoundRadius, this.mBlurDepth)).build();
                    break;
                case DISPLAY_ROUND_BLUR_VIGNETTE:// 圆角高斯模糊的LOMO
                    displayImageOptions = builder.displayer(
                            new RoundedLomoBlurBitmapDisplayer(this.mRoundRadius, this.mBlurDepth)).build();
                    break;
                case DISPLAY_CIRCLE_BLUR:// 圆形高斯模糊
                    displayImageOptions = builder.displayer(new CircleBlurBitmapDisplayer(this.mBlurDepth)).build();
                    break;
                case DISPLAY_POLYGON://六边形
                    displayImageOptions = builder.displayer(new PolygonBitmapDisplayer().setHeight(mPolygonHeight)).build();
                    break;
            }

            return displayImageOptions;
        }

        public void load() {
            if (TextUtils.isEmpty(mUrl) || mUrl.startsWith("?imageView2")) {
                return;
            }
            ZjbImageLoader.load(this.mUrl, this.mImageSize, this.build(), this.mImageLoadingListener,
                    this.mImageLoadingProgressListener);
        }

        /**
         * url的格式如下
         * http://site.com/image.png // from Web
         * file:///mnt/sdcard/image.png // from SD card
         * file:///mnt/sdcard/video.mp4 // from SD card (video thumbnail)
         * content://media/external/images/media/13 // from content provider
         * content://media/external/video/media/13 // from content provider (video thumbnail)
         * assets://image.png // from assets
         * drawable:// + R.drawable.img // from drawables (non-9patch images)
         *
         * @param view
         */
        public void into(View view) {
            if (null != view) {
                //1.设置尺寸
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    if (mQiniuWidth > 0 && mQiniuHeight > 0) {
                        imageView.setMaxHeight(mQiniuHeight);
                        imageView.setMaxWidth(mQiniuWidth);
                    }
                    if (mDefaultDrawable != null) {
                        imageView.setImageDrawable(mDefaultDrawable);
                    } else if (mDefaultRes != 0) {
                        imageView.setImageResource(mDefaultRes);
                    }
                } else if (view instanceof AnimationImageView) {
                    AnimationImageView imageSwitcher = (AnimationImageView) view;
                    if (mQiniuWidth > 0 && mQiniuHeight > 0) {
                        imageSwitcher.setQiniuHeight(mQiniuHeight);
                        imageSwitcher.setQiniuWidth(mQiniuWidth);
                    }
                    if (mDefaultDrawable != null) {
                        imageSwitcher.setImageDrawable(mDefaultDrawable);
                    } else if (mDefaultRes != 0) {
                        imageSwitcher.setImageResource(mDefaultRes);
                    }
                }
            }

            ZjbImageLoader.display(this.mUrl, view, this.build(),
                    this.mImageLoadingListener, this.mImageLoadingProgressListener);
        }
    }

    public abstract static class DefaultLoadingListener implements ImageLoadingListener {
        public DefaultLoadingListener() {
        }

        public void onLoadingStarted(String s, View view) {
        }

        public void onLoadingFailed(String s, View view, FailReason failReason) {
        }

        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        }

        public void onLoadingCancelled(String s, View view) {
        }
    }
}
