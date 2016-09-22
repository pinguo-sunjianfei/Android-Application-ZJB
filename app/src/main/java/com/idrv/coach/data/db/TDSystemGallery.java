package com.idrv.coach.data.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.Gallery;
import com.idrv.coach.utils.helper.ResHelper;

import java.io.File;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * time: 2015/9/23
 * description:扫描本地图片数据库
 *
 * @author sunjianfei
 */
public class TDSystemGallery extends TDBase {


    /**
     * 异步获取到相册的所有照片，及其缩略图
     *
     * @return
     */
    public static Observable<HashMap<Integer, String>> syncFindThumbnail() {
        return makeObservable(TDSystemGallery::syncThumbnails)
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 得到文件系统的所有图片，返回一个map结构的数据
     *
     * @return <p>
     * map:key是照片在images表当中的id，value是对应的缩略图的文件路径，缩略图可以通过<br/>
     * <p>
     * 而Image可以通过
     * PinguoImageLoader.url("content://media/external/images/media/62026")得到
     * </p>
     */
    public static Observable<Map.Entry<Integer, String>> asyncThumbnails() {
        return Observable.<Map.Entry<Integer, String>>create(subscriber -> {
            //2.得到ContentResolver
            ContentResolver resolver = ZjbApplication.gContext.getContentResolver();
            //3.得到查找条件
            String[] columns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
            String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC" + " LIMIT 0,303";
            Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns, MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "<>?",
                    new String[]{ResHelper.getString(R.string.app_name)}, sortOrder);
            //4.得到缩略图查询条件
            String[] thumb_projection = {MediaStore.Images.Thumbnails.DATA,
                    MediaStore.Images.Thumbnails.IMAGE_ID};
            //5.查询
            Cursor thumb_cursor = null;
            try {
                if (cursor.moveToFirst()) {
                    int imageIdIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    int imagePathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    do {
                        try {
                            int imageId = cursor.getInt(imageIdIndex);
                            String imagePath = cursor.getString(imagePathIndex);
                            if (TextUtils.isEmpty(imagePath)) continue;
                            File file = new File(imagePath);
                            if (TextUtils.isEmpty(imagePath) || !file.exists() || file.length() < 100) {
                                continue;
                            }
                            thumb_cursor = resolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, thumb_projection,
                                    MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                                    new String[]{imageId + ""},
                                    null);
                            String thumbPath = null;
                            if (thumb_cursor.moveToFirst()) {
                                int thumbIndex = thumb_cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                                thumbPath = thumb_cursor.getString(thumbIndex);
                            }
                            if (!TextUtils.isEmpty(thumbPath)) {
                                File thumbFile = new File(thumbPath);
                                if (null == thumbFile || !thumbFile.exists() || thumbFile.length() < 1024) {
                                    thumbPath = null;
                                }
                            }
                            if (null != subscriber) {
                                Map.Entry<Integer, String> entry = new AbstractMap.SimpleEntry<Integer, String>(imageId, thumbPath);
                                subscriber.onNext(entry);
                            }
                        } finally {
                            if (null != thumb_cursor) {
                                thumb_cursor.close();
                            }
                        }
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (null != subscriber) {
                    subscriber.onError(e);
                }
            } finally {
                if (null != cursor) {
                    cursor.close();
                }

                if (null != subscriber) {
                    subscriber.onCompleted();
                }
            }

        }).subscribeOn(Schedulers.computation());

    }

    /**
     * 扫描系统相册得到图片的相册
     *
     * @return
     */
    public static Observable<Gallery> asyncFindGallery() {
        return Observable.<Gallery>create(subscriber -> {
            //2.得到ContentResolver
            ContentResolver resolver = ZjbApplication.gContext.getContentResolver();
            //3.得到查找条件
            String[] columns = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                /*, "COUNT(1) AS count"*/};
            //String selection = "0==0) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID;
            String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;
            Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columns, null, null, sortOrder);
            Cursor thumb_cursor = null;
            //4.得到缩略图查询条件
            String[] thumb_projection = {MediaStore.Images.Thumbnails.DATA,
                    MediaStore.Images.Thumbnails.IMAGE_ID};
            //5.进行查找
            try {

                if (cursor.moveToFirst()) {
                    int id_column = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    int image_id_column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    int bucket_id_column = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                    int bucket_name_column = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    do {
                        try {
                            int id = cursor.getInt(id_column);
                            String image_path = cursor.getString(image_id_column);
                            if (TextUtils.isEmpty(image_path)) continue;
                            int bucket_id = cursor.getInt(bucket_id_column);
                            String bucket_name = cursor.getString(bucket_name_column);
                            File file = new File(image_path);
                            if (!file.exists() || file.length() < 100 || TextUtils.isEmpty(bucket_name)) {
                                continue;
                            }
                            //6.得到缩略图
                            thumb_cursor = resolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, thumb_projection,
                                    MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                                    new String[]{id + ""},
                                    null);
                            String thumbPath = null;
                            if (thumb_cursor.moveToFirst()) {
                                int thumbIndex = thumb_cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                                thumbPath = thumb_cursor.getString(thumbIndex);
                            }
                            //7.处理数据
                            Gallery gallery = new Gallery();
                            gallery.setId(id);
                            if (!TextUtils.isEmpty(thumbPath)) {
                                File thumbFile = new File(thumbPath);
                                if (thumbFile != null && thumbFile.exists() && thumbFile.length() > 1024) {
                                    gallery.setPath(thumbPath);
                                }
                            }
                            gallery.setGalleryId(bucket_id);
                            gallery.setGalleryName(bucket_name);
                            if (null != subscriber) {
                                subscriber.onNext(gallery);
                            }
                        } finally {
                            if (thumb_cursor != null) {
                                thumb_cursor.close();
                            }
                        }
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (null != subscriber) {
                    subscriber.onError(e);
                }
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
                if (null != subscriber) {
                    subscriber.onCompleted();
                }
            }

        }).subscribeOn(Schedulers.computation());
    }

    /**
     * 得到文件系统的所有图片，返回一个map结构的数据
     *
     * @return <p>
     * map:key是照片在images表当中的id，value是对应的缩略图的文件路径，缩略图可以通过<br/>
     * <p>
     * 而Image可以通过
     * PinguoImageLoader.url("content://media/external/images/media/62026")得到
     * </p>
     */
    private static HashMap<Integer, String> syncThumbnails() {
        //1.初始化容器
        HashMap<Integer, String> map = new HashMap<>();
        //2.得到ContentResolver
        ContentResolver resolver = ZjbApplication.gContext.getContentResolver();
        //3.得到查找条件
        String[] columns = {MediaStore.Images.Media._ID};
        //String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, null);
        //4.得到缩略图查询条件
        String[] thumb_projection = {MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails.IMAGE_ID};
        //5.查询
        Cursor thumb_cursor = null;
        try {
            if (cursor.moveToFirst()) {
                int imageIdIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                do {
                    int imageId = cursor.getInt(imageIdIndex);
                    thumb_cursor = resolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, thumb_projection,
                            MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                            new String[]{imageId + ""},
                            null);
                    String thumbPath = null;
                    if (thumb_cursor.moveToFirst()) {
                        int thumbIndex = thumb_cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                        thumbPath = thumb_cursor.getString(thumbIndex);
                    }
                    map.put(imageId, thumbPath);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
            if (null != thumb_cursor) {
                thumb_cursor.close();
            }
        }
        return map;
    }
}
