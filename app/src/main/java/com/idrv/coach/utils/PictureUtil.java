package com.idrv.coach.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * time: 2016/3/17
 * description: Thank for boredream ,
 * I made a Util before, but it is not good .
 * Yesterday I find some classes from boredream.
 * So I pick some useful to make this class.
 * <p>
 * PS:为了避免三星手机屏幕旋转造成的bug，需在 Manifest中添加
 * android:configChanges="orientation|screenSize|keyboardHidden"
 *
 * @author bigflower
 */
public class PictureUtil {
    public static final String CROP_CACHE_FOLDER = "zjb/photoCache";
    public static final String TAG = PictureUtil.class.getSimpleName();

    // 4 modes
    public static final int REQUEST_CODE_FROM_CAMERA = 901;
    public static final int REQUEST_CODE_FROM_ALBUM = 902;
    public static final int REQUEST_CODE_FROM_CROP = 903;
    public static final int REQUEST_CODE_ALBUM_CROP = 904;

    public static Uri imgUri = null;

    public static Uri TakePhoto(Activity activity) {
        return TakePhoto(activity, null);
    }

    public static Uri TakePhoto(Activity activity, Uri uri) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (uri == null)
            uri = createImageUri(activity);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(intent, REQUEST_CODE_FROM_CAMERA);

        return uri;
    }

    public static void FindPhoto(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        activity.startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
    }


    /**
     * get the photo and crop it .
     * here I find an important thing.
     * we can use this( intent.putExtra("output", uri) ) to put the pic into the uri
     *
     * @param activity
     * @param uri
     */
    public static void FindPhotoCrop(Activity activity, Uri uri) {
        if (uri == null) {
            imgUri = createImageUri(activity);
        } else {
            imgUri = uri;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(imgUri, "image/*")
                .putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
                .putExtra("crop", "true")
                .putExtra("scale", true)
                .putExtra("aspectX", 1)// 裁剪框比例
                .putExtra("aspectY", 1)
                .putExtra("outputX", 300)// 输出图片大小
                .putExtra("outputY", 300)
                .putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                .putExtra("noFaceDetection", true)
                .putExtra("scaleUpIfNeeded", true)
                .putExtra("return-data", false);
        activity.startActivityForResult(intent, REQUEST_CODE_ALBUM_CROP);
    }

    public static void FindPhotoCrop(Activity activity) {
        FindPhotoCrop(activity, null);
    }

    /**
     * 裁剪图片
     *
     * @param activity
     * @param object
     */
    public static void photoZoom(Activity activity, Object object) {
        // 路径的处理
        String path;
        if (object instanceof Uri) {
            path = PictureUtil.getPath(activity.getApplicationContext(), (Uri) object); // 获取图片的绝对路径
        } else {
            path = object.toString();
        }

        Uri imgUri;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            imgUri = Uri.parse("file:///" + path); // 将绝对路径转换为URL
        } else {
            imgUri = Uri.parse(path); // 将绝对路径转换为URL
        }
        cropImageUri(imgUri, 300, 300, PictureUtil.REQUEST_CODE_FROM_CROP, activity);
    }

    /**
     * 开始裁剪
     *
     * @return
     */
    private static void cropImageUri(Uri uri, int outputX, int outputY, int requestCode, Activity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * creat a uri for takePhoto
     * <p>
     * I don't know the name , what's the meaning of "GetWorld"?
     *
     * @param context
     * @return
     */
    public static Uri createImageUri(Context context) {
        // 根据机型来判断，这样真的好吗？
        if ("Lenovo".equals(Build.MODEL.split(" ")[0])) {
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            String path = Environment.getExternalStorageDirectory()
                    + "/zjb/photoCache" + System.currentTimeMillis() + ".jpg";
            imgUri = Uri.parse("file:///" + path);
        } else {
            String name = "GetWorld" + System.currentTimeMillis();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, name);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".jpeg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            imgUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        return imgUri;
    }

    /**
     * if we choose the cancel rather than the save picture. wo should delete the uri, which we created before.
     * TODO 这里需要重来
     *
     * @param context
     */
    public static void deleteUri(Context context) {
        if (imgUri == null)
            return;
        try {
            context.getContentResolver().delete(imgUri, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * compress
     *
     * @param imgPath
     * @author JPH
     * @date 2014-12-5下午11:30:59
     */
    public static Bitmap compressImageByPixel(String imgPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        float maxSize = 1000f;// 默认1000px
        int be = 1; // 表示不缩放
        if (width > height && width > maxSize) {// 缩放比,用高或者宽其中较大的一个数据进行计算
            be = (int) (newOpts.outWidth / maxSize);
        } else if (width < height && height > maxSize) {
            be = (int) (newOpts.outHeight / maxSize);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

        bitmap.recycle();
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
    }

    private void release() {
        imgUri = null;
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    public static boolean isPhotoReallyCropped(Uri uri) {
        File file = new File(uri.getPath());
        long length = file.length();
        return length > 0;
    }
}
