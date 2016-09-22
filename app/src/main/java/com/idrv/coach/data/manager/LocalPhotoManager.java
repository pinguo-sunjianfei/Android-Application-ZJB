package com.idrv.coach.data.manager;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.Gallery;
import com.idrv.coach.data.db.TDSystemGallery;
import com.idrv.coach.utils.FileUtil;
import com.idrv.coach.utils.Logger;
import com.idrv.coach.utils.ValidateUtil;
import com.idrv.coach.utils.helper.ResHelper;
import com.zjb.loader.core.util.NameGeneratorUtil;
import com.zjb.loader.internal.utils.BitmapUtils;
import com.zjb.loader.internal.utils.IoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * time: 2015/9/23
 * description:本地照片的管理器，包含了扫描出来的所有的图片信息
 *
 * @author sunjianfei
 */
public class LocalPhotoManager {
    /*全局的单例*/
    private static LocalPhotoManager gInstance;
    /* map:key是Image的id，value是缩略图的id,缩略图可以通过<br/>
     * Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),thumbnailId, Thumbnails.MICRO_KIND, null)
     * 而Image可以通过
     * PinguoImageLoader.url("content://media/external/images/media/62026")得到
     * */
    private TreeMap<Integer, String> mLocalPhotos;
    /*封装了本地所有相册的路径*/
    private List<Gallery> mLocalGallery;
    /*在sd卡上面缓存的尺寸大小*/
    private int mItemSize;

    private ConcurrentLinkedQueue<Integer> mQueue;
    //标识是否正在轮循
    private boolean mIsPoll;
    //标识是否销毁
    private boolean mIsDestroy;

    private LocalPhotoManager() {
        this.mLocalGallery = new ArrayList<>();
        this.mLocalPhotos = new TreeMap<>((lhs, rhs) -> rhs - lhs);
        this.mItemSize = (int) (ResHelper.getScreenWidth() / 4.f + 0.5f - 3.f);
        this.mQueue = new ConcurrentLinkedQueue<Integer>();
    }

    private void save(Integer imageId) {
        if (!mQueue.contains(imageId)) {
            mQueue.add(imageId);
        }
        if (!mIsPoll) {
            mIsPoll = true;
            CacheThread thread = new CacheThread();
            thread.start();
        }
    }

    public static LocalPhotoManager getInstance() {
        if (gInstance == null) {
            gInstance = new LocalPhotoManager();
        }
        return gInstance;
    }

    /**
     * 将一张照片存放到图库里面去
     *
     * @param code content id
     * @param path file 路径
     */
    public void put2Map(int code, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (!ValidateUtil.isValidate(mLocalPhotos)) {
            this.mLocalPhotos = new TreeMap<>((lhs, rhs) -> rhs - lhs);
        }
        this.mLocalPhotos.put(code, path);
        this.put2Gallery(code, path);
    }

    /**
     * 将一张图片存放到gallery里面
     *
     * @param code
     * @param path
     */
    public void put2Gallery(int code, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        //1.得到相册名称
        String tempPath = path.substring(0, path.lastIndexOf("/"));
        if (TextUtils.isEmpty(tempPath)) {
            return;
        }
        String galleryName = tempPath.substring(tempPath.lastIndexOf("/") + 1);
        if (TextUtils.isEmpty(galleryName)) {
            return;
        }
        //2.放入gallery
        int i = 0;
        int size = mLocalGallery.size();
        for (; i < size; i++) {
            Gallery gallery = mLocalGallery.get(i);
            if (galleryName.equals(gallery.getGalleryName())) {
                gallery.getPictures().put(code, gallery.getPath());
                break;
            }
        }
        if (i >= size) {
            Gallery gallery = new Gallery();
            gallery.setGalleryName(galleryName);
            gallery.getPictures().put(code, gallery.getPath());
            mLocalGallery.add(0, gallery);
        }
    }

    /**
     * 初始化，这个方法会扫描当前手机的所有图片，加载到容器当中
     */
    public void initialize() {
        //0.清空数据
        this.mLocalPhotos.clear();
        this.mLocalGallery.clear();
        //1.异步扫描所有的缩略图
        TDSystemGallery.asyncThumbnails()
                .subscribe(entry -> {
                    mLocalPhotos.put(entry.getKey(), entry.getValue());
                    if (TextUtils.isEmpty(entry.getValue())) {
                        save(entry.getKey());
                    }
                }, Logger::e);
        //2.扫描所有的相册
        TDSystemGallery.asyncFindGallery()
                .subscribe(this::onFindGallery, Logger::e);
    }

    public static void destroy() {
        if (null != gInstance) {
            gInstance.mIsDestroy = true;
            gInstance = null;
        }
    }

    /**
     * 当异步获取到一个相册，就会调用这个方法将gallery添加到本地集合当中
     *
     * @param gallery
     */
    private void onFindGallery(Gallery gallery) {
        //1.加入内存当中
        int i = 0, size = mLocalGallery.size();
        for (; i < size; i++) {
            Gallery g = mLocalGallery.get(i);
            if (g.getGalleryId() == gallery.getGalleryId()) {
                g.getPictures().put(gallery.getId(), gallery.getPath());
                break;
            }
        }
        if (i >= size) {
            gallery.getPictures().put(gallery.getId(), gallery.getPath());
            mLocalGallery.add(gallery);
        }
        //2.放到sd卡上面
        if (TextUtils.isEmpty(gallery.getPath())) {
            save(gallery.getId());
        }
    }

    private void initializeListNext(List<Gallery> galleries) {
        mLocalGallery.clear();
        mLocalGallery.addAll(galleries);
    }

    private void initializeMapNext(HashMap<Integer, String> map) {
        mLocalPhotos.clear();
        mLocalPhotos.putAll(map);
    }


    public Map<Integer, String> getLocalPhotos() {
        return mLocalPhotos;
    }

    public boolean isGalleryValidate() {
        return ValidateUtil.isValidate(mLocalGallery);
    }

    public boolean isPhotoValidate() {
        return ValidateUtil.isValidate(mLocalPhotos);
    }


    public List<Gallery> getLocalGallery() {
        return mLocalGallery;
    }

    private class CacheThread extends Thread {

        protected InputStream getStreamFromContent(String imageUri) throws FileNotFoundException {
            ContentResolver res = ZjbApplication.gContext.getContentResolver();
            return res.openInputStream(Uri.parse(imageUri));
        }

        protected Bitmap getScaledBitmap(InputStream is, int width, int height) {
            return BitmapUtils.createScaledBitmap(is, width, height, Bitmap.Config.RGB_565);
        }

        public File getFileByCacheKey(String cacheKey) throws IOException {
            return new File(FileUtil.getPathByType(FileUtil.DIR_TYPE_CACHE), cacheKey);
        }

        @Override
        public void run() {
            try {
                Integer imageId = mQueue.poll();
                do {
                    //1.如果销毁就跳出线程
                    if (mIsDestroy) {
                        break;
                    }
                    boolean savedSuccessfully = false;
                    //2.从内存当中拿出缓存
                    String uri = "content://media/external/images/media/" + imageId;
                    InputStream is = getStreamFromContent(uri);
                    Bitmap bitmap = getScaledBitmap(is, mItemSize, mItemSize);
                    if (bitmap == null || bitmap.isRecycled()) {
                        continue;
                    }
                    //3.保存到sd卡上面
                    String cacheKey = NameGeneratorUtil.generateCacheKey(uri, mItemSize, mItemSize);
                    File imageFile = getFileByCacheKey(cacheKey);
                    if (imageFile.exists() && imageFile.length() > 1024) continue;
                    FileOutputStream os = new FileOutputStream(imageFile);
                    try {
                        savedSuccessfully = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    } finally {
                        IoUtils.closeSilently(os);
                        if (!savedSuccessfully) {
                            if (imageFile.exists()) {
                                imageFile.delete();
                            }
                        }
                        if (null != bitmap && !bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                    }
                } while ((imageId = mQueue.poll()) != null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mIsPoll = false;
            }
        }
    }

}
