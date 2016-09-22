package com.zjb.loader.core.util;

import android.text.TextUtils;

import com.zjb.loader.internal.cache.disc.naming.FileNameGenerator;
import com.zjb.loader.internal.cache.disc.naming.Md5FileNameGenerator;
import com.zjb.loader.internal.core.assist.ImageSize;

/**
 * time: 15/10/17
 * description: 缓存key生成器
 *
 * @author sunjianfei
 */
public class NameGeneratorUtil {
    private static FileNameGenerator mFileNameGenerator;

    static {
        mFileNameGenerator = new Md5FileNameGenerator();
    }

    /**
     * 生成缓存的key，包括内存缓存和sd卡缓存
     *
     * @param imageURI 原始的imageUrl
     * @param width    视图的宽度
     * @param height   视图的高度
     * @return
     */
    public synchronized static String generateCacheKey(String imageURI, int width, int height) {
        imageURI = encodeURL(imageURI, width, height);
        return mFileNameGenerator.generate(imageURI);
    }

    /**
     * 生成缓存的key，包括内存缓存和sd卡缓存
     *
     * @param imageURI  原始的imageUrl
     * @param imageSize 视图的尺寸
     * @return
     */
    public synchronized static String generateCacheKey(String imageURI, ImageSize imageSize) {
        imageURI = encodeURL(imageURI, imageSize.getWidth(), imageSize.getHeight());
        return mFileNameGenerator.generate(imageURI);
    }

    /**
     * 生成缓存的key，包括内存缓存和sd卡缓存
     *
     * @param imageURI 原始的imageUrl
     * @return
     */
    public synchronized static String generateCacheKey(String imageURI) {
        return mFileNameGenerator.generate(imageURI);
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
