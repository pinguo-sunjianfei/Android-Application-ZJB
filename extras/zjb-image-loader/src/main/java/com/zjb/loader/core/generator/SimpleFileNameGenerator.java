package com.zjb.loader.core.generator;

import android.text.TextUtils;

import com.zjb.loader.internal.cache.disc.naming.FileNameGenerator;

/**
 * time: 15/6/11
 * description:图片缓存名称生成器
 *
 * @author sunjianfei
 */
public class SimpleFileNameGenerator implements FileNameGenerator {
    private static final int MAX_URL_LENGTH = 128;

    public SimpleFileNameGenerator() {
    }

    public String generate(String imageUri) {
        return this.replaceInvalidChar(imageUri);
    }

    private String replaceInvalidChar(String str) {
        if(TextUtils.isEmpty(str)) {
            return "_";
        } else {
            str = str.replaceAll("[^a-z0-9_-]{1,}", "_");
            str = str.substring(Math.max(0, str.length() - MAX_URL_LENGTH));
            return str;
        }
    }
}
