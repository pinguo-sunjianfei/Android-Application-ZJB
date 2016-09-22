package com.idrv.coach.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.utils.helper.ResHelper;
import com.zjb.loader.ZjbImageLoader;
import com.zjb.loader.core.util.GaussianBlur;
import com.zjb.loader.internal.core.download.BaseImageDownloader;
import com.zjb.loader.internal.core.download.ImageDownloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;

import static com.idrv.coach.ZjbApplication.gContext;

/**
 * time: 15/6/7
 * description: 对bitmap的基本操作
 *
 * @author sunjianfei
 */
public class BitmapUtil extends BaseUtil {

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static String flashFileToLocal(Drawable drawable) {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (null == drawable || !sdCardExist) {
            return null;
        }
        String filePath = FileUtil.getPathByType(FileUtil.DIR_TYPE_TEMP);
        Bitmap bitmap = BitmapUtil.drawable2Bitmap(drawable);
        String dst = filePath + "/" + System.currentTimeMillis() + ".jpg";
        File newFile = new File(dst);
        FileOutputStream mFileOutputStream = null;
        try {
            newFile.createNewFile();
            mFileOutputStream = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    mFileOutputStream);
            return newFile.getAbsolutePath();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        } finally {
            if (null != mFileOutputStream) {
                try {
                    mFileOutputStream.flush();
                    mFileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Bitmap loadAvailBitmap(String fileName) {
        if (new File(fileName).exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, options);
            int width = gContext.getResources().getDisplayMetrics().widthPixels;
            int ratio = Math.round(options.outWidth / (float) width);
            options.inSampleSize = ratio;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            return BitmapFactory.decodeFile(fileName, options);
        }
        return BitmapFactory.decodeResource(gContext.getResources(),
                R.mipmap.ic_app);
    }

    /**
     * 将bitmap按照比例进行裁剪
     *
     * @param bitmap      原始的bitmap
     * @param widthRatio  宽度的比例值
     * @param heightRatio 高度的比例值
     * @return
     */
    public static Bitmap clipBitmap(Bitmap bitmap, int widthRatio, int heightRatio) {
        try {
            if (bitmap == null || bitmap.isRecycled()) {
                return null;
            }
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            //1.如果是长图
            if (h / (float) w > heightRatio / (float) widthRatio) {
                float targetH = w / (float) widthRatio * heightRatio;
                float y = (h - targetH) / 2.f;
                Bitmap bmp = Bitmap.createBitmap(bitmap, 0, (int) y, w, (int) targetH);
                bitmap.recycle();
                return bmp;
            } else if (w / (float) h > widthRatio / (float) heightRatio) {
                float targetW = h * widthRatio / (float) heightRatio;
                float x = (w - targetW) / 2.f;
                Bitmap bmp = Bitmap.createBitmap(bitmap, (int) x, 0, (int) targetW, h);
                bitmap.recycle();
                return bmp;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 将图片裁剪成正方形
     */
    public static Bitmap clipBitmap(Bitmap bitmap) {
        try {
            if (bitmap == null || bitmap.isRecycled()) {
                return null;
            }
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int length = Math.min(width, height);
            if (height > width) {
                int start = (height - length) / 2;
                Bitmap bmp = Bitmap.createBitmap(bitmap, 0, start, length, length);
                bitmap.recycle();
                return bmp;
            } else if (height < width) {
                int start = (width - length) / 2;
                Bitmap bmp = Bitmap.createBitmap(bitmap, start, 0, length, length);
                bitmap.recycle();
                return bmp;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap getThumbBitmap(Bitmap bitmap, int size) {
        Bitmap cirBitmap = clipBitmap(bitmap);
        if (null != cirBitmap) {
            Bitmap thumbBmp = Bitmap.createScaledBitmap(cirBitmap, size, size, true);
            cirBitmap.recycle();
            return thumbBmp;
        } else {
            return null;
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 对图片进行缩放操作
     *
     * @param bitmap 要缩放的图片
     * @param reW    缩放后的宽度
     * @param reH    缩放后的高度
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int reW, int reH) {
        if (bitmap == null) {
            return null;
        }
        int bmpW = bitmap.getWidth();
        int bmpH = bitmap.getHeight();
        float wScale = reW * 1.0f / bmpW;
        float hScale = reH * 1.0f / bmpH;
        float scale = Math.max(wScale, hScale);
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bmpW, bmpH, matrix, true);
    }

    /**
     * 对某个view截屏
     *
     * @param view 需要截屏的视图
     * @return
     */
    public static Bitmap getBitmap(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(canvas);
        return bitmap;
    }

    /**
     * 对某个位图保存成文件
     *
     * @param bitmap 需要保存的位图
     * @return 保存文件的路径
     */
    public static String saveBitmap(Bitmap bitmap) {
        return saveBitmap(bitmap, FileUtil.DIR_TYPE_IMAGE);
    }

    /**
     * 对某个位图保存成文件
     *
     * @param bitmap 需要保存的位图
     * @return 保存文件的路径
     */
    public static String saveBitmap(Bitmap bitmap, int dirType) {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (null == bitmap || !sdCardExist) {
            return null;
        }
        String dirPath = FileUtil.getPathByType(dirType);
        String dst = dirPath + "/" + System.currentTimeMillis() + ".jpg";
        File newFile = new File(dst);
        FileOutputStream mFileOutputStream = null;
        try {
            newFile.createNewFile();
            mFileOutputStream = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    mFileOutputStream);
            //如果是保存到系统相册,发广播,更新相册
            if (dirType == FileUtil.DIR_TYPE_SYS_IMAGE) {
                MediaScannerConnection.scanFile(gContext, new String[]{dst},
                        new String[]{"image/jpeg"}, null);
            }
            return newFile.getAbsolutePath();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        } finally {
            if (null != mFileOutputStream) {
                try {
                    mFileOutputStream.flush();
                    mFileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 异步保存图片
     * 注意,默认是I/O线程执行,如果订阅之后有View相关操作,请指明主线程
     *
     * @param bitmap
     * @return
     */
    public static Observable<String> saveBitmapAsync(Bitmap bitmap) {
        return makeObservable(() -> BitmapUtil.saveBitmap(bitmap));
    }

    public static Observable<String> saveBitmapAsync(Bitmap bitmap, int dirType) {
        return makeObservable(() -> BitmapUtil.saveBitmap(bitmap, dirType));
    }

    /**
     * 处理高斯模糊效果
     *
     * @param sentBitmap 需要高斯模糊的Bitmap对象
     * @param blurWidth  高斯模糊的大小
     * @param blurHeight 高斯模糊的大小
     * @param radius     高斯模糊的半径
     * @return 返回一个高斯模糊的Bitmap对象
     */
    public static Bitmap fastblur(Bitmap sentBitmap, int blurWidth, int blurHeight, float radius) {
        if (sentBitmap == null || sentBitmap.isRecycled()) {
            return sentBitmap;
        }
        int bmpWidth = sentBitmap.getWidth();
        int bmpHeight = sentBitmap.getHeight();
        GaussianBlur blurProcess = new GaussianBlur();
        if (bmpWidth <= blurWidth && bmpHeight <= blurHeight) {
            return blurProcess.blur(sentBitmap, radius);
        } else {
            Bitmap scaleBitmap = scaleBitmap(sentBitmap, blurWidth, blurHeight);
            return blurProcess.blur(scaleBitmap, radius);
        }
    }

    /**
     * loading large bitmaps efficiently
     * 从开发者网站直接copy过来的
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) throws IOException {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (ImageDownloader.Scheme.ofUri(filePath) == ImageDownloader.Scheme.UNKNOWN) {
            filePath = "file://" + filePath;
        }

        BaseImageDownloader imageDownloader = new BaseImageDownloader(ZjbApplication.gContext);
        BitmapFactory.decodeStream(imageDownloader.getStream(filePath, null), null, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeStream(imageDownloader.getStream(filePath, null), null, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * 获取RGB_565的bitmap
     */
    public static Bitmap getRGB565Bitmap(int resource_id, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resource_id, options);
        int ratio = calculateInSampleSize(options, context.getResources().getDisplayMetrics().widthPixels,
                context.getResources().getDisplayMetrics().heightPixels);
        options.inSampleSize = ratio;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        return BitmapFactory.decodeResource(context.getResources(), resource_id, options);
    }

    /**
     * 添加水印，返回路径
     *
     * @param context   上下文
     * @param imagePath 图片路径
     * @return
     */
    public static String getShareImagePath(Context context, String imagePath, String nickName) {
        //1.得到原始的bitmap
        File file = new File(imagePath);
        if (file != null && file.exists() && file.length() > 100) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Bitmap share = getShareBitmap(context, bitmap, nickName);
                String path = FileUtil.getPathByType(FileUtil.DIR_TYPE_CACHE) + System.currentTimeMillis() + ".jpg";
                File target = new File(path);
                if (!target.exists()) {
                    target.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(path);
                share.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                return path;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return imagePath;
    }

    /**
     * 获取缓存图片地址
     *
     * @return path
     */
    public static String getImagePath(String imageUrl) {
        String filePath = ZjbImageLoader.getQiniuDiskCachePath(imageUrl);
        File imageFile = new File(filePath);
        if (null != imageFile && imageFile.exists()) {
            filePath = imageFile.getAbsolutePath();
        } else {
            filePath = BitmapUtil.saveBitmap(BitmapFactory.decodeResource(ZjbApplication.gContext.getResources(), R.mipmap.ic_app));
        }
        return filePath;
    }

    /**
     * 获取缓存图片地址
     *
     * @param width  宽
     * @param height 高
     * @return path
     */
    public static String getImagePath(String imageUrl, int width, int height) {
        String filePath = ZjbImageLoader.getQiniuDiskCachePath(imageUrl, width, height);
        File imageFile = new File(filePath);
        if (null != imageFile && imageFile.exists()) {
            filePath = imageFile.getAbsolutePath();
        } else {
            filePath = BitmapUtil.saveBitmap(BitmapFactory.decodeResource(ZjbApplication.gContext.getResources(), R.mipmap.ic_app));
        }
        return filePath;
    }

    /**
     * 添加水印分享出去
     *
     * @param bitmap 位图
     * @return
     */
    public static Bitmap getShareBitmap(Context context, Bitmap bitmap, String nickName) {
        //1.构建一个新的bitmap
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        //2.绘制原图
        canvas.drawBitmap(bitmap, 0, 0, null);
        //3.添加水印的画笔
        Paint paint = new Paint();
        paint.setDither(true);
        //4.绘制水印图片
        Bitmap shareBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_water_mark);
        float top = bitmap.getHeight() - 10 - shareBitmap.getHeight();
        canvas.drawBitmap(shareBitmap, 10, top, paint);
        //5.绘制文字
        String title = ResHelper.getString(R.string.comment_no_name);
        if (!TextUtils.isEmpty(nickName)) {
            title = nickName;
        }
        Paint textPaint = new Paint();
        textPaint.setTextSize(16.0f);
        textPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "watermark/font/01_fangzheng_lanting_xianhei.ttf"));
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setAlpha(200);
        textPaint.setShadowLayer(1, .5f, .5f, 0x30000000);
        if (!TextUtils.isEmpty(title)) {
            title = "by:" + title;
            int left = 10 + shareBitmap.getWidth() + 10;
            top = top + 16;
            canvas.drawText(title, left, top, textPaint);
        }
        shareBitmap.recycle();
        bitmap.recycle();
        //6.保存canvas状态
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newBitmap;
    }

    /**
     * 以CenterCrop方式resize图片
     *
     * @param src        原始图片
     * @param destWidth  目标图片宽度
     * @param destHeight 目标图片高度
     * @return
     */
    public static Bitmap resizeBitmapByCenterCrop(Bitmap src, int destWidth, int destHeight) {
        if (null == src || src.isRecycled() || destWidth == 0 || destHeight == 0) {
            return null;
        }
        // 图片宽度
        int w = src.getWidth();
        // 图片高度
        int h = src.getHeight();
        // ImageView宽度
        int x = destWidth;
        // ImageView高度
        int y = destHeight;

        // 高宽比之差
        float temp = (y * 1.0f / x) - (h * 1.0f / w);
        //判断高宽比例，如果目标高宽比例大于原图，则原图高度不变，宽度为(w1 = (h * x) / y)拉伸
        // 画布宽高(w1,h),在原图的((w - w1) / 2, 0)位置进行切割
        if (temp > 0) {
            // 计算画布宽度
            int w1 = (h * x) / y;

            if (w1 > w) {
                //如果画布的宽大于原图的宽
                w1 = w;
            }
            // 创建一个指定高宽的图片
            Bitmap newb = Bitmap.createBitmap(src, (w - w1) / 2, 0, w1, h);
            return newb;
        } else {
            // 如果目标高宽比小于原图，则原图宽度不变，高度为(h1 = (y * w) / x),
            //画布宽高(w, h1), 原图切割点(0, (h - h1) / 2)

            // 计算画布高度
            int h1 = (y * w) / x;
            if (h1 > h) {
                h1 = h;
            }
            // 创建一个指定高宽的图片
            Bitmap newb = Bitmap.createBitmap(src, 0, (h - h1) / 2, w, h1);
            return newb;
        }
    }


}
