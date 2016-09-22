package com.zjb.loader.internal.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * time: 2015/10/14
 * description:
 *
 * @author sunjianfei
 */
public class BitmapUtils {
    /**
     * 根据一张图片的输入流和显示视图的宽高，返回指定宽高的bitmap
     *
     * @param is     原始的图片数据
     * @param width  目标宽度
     * @param height 目标高度
     * @return 指定尺寸的位图
     */
    public static Bitmap createScaledBitmap(InputStream is, int width, int height, Bitmap.Config bitmapConfig) {
        try {
            //1.加到一个byte数组当中
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IoUtils.copyStream(is, bos, null);
            byte[] buffer = bos.toByteArray();
            bos.close();
            is.close();
            //2.计算宽高
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
            float width_ratio = options.outWidth / (float) width;
            float height_ratio = options.outHeight / (float) height;
//            int ratio = (int) (width_ratio > height_ratio ? width_ratio : height_ratio);
            int ratio = calculateInSampleSize(options, width, height);
            //3.decode
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            options.inPreferredConfig = bitmapConfig;
            options.inSampleSize = ratio;
            return BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further.
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
}
