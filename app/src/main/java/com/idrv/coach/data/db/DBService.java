package com.idrv.coach.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * time: 15/6/16
 * description:数据库管理类
 *
 * @author sunjianfei
 */
public class DBService {

    private static DBOpenHelper sDBHelper;

    private static final ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();

    public static void init(Context context) {
        if (sDBHelper == null) {
            //1.初始化DBOpenHelper
            sDBHelper = DBOpenHelper.getInstance(context);
        }
    }

    public static SQLiteDatabase getReadableDatabase() {
        mLock.readLock().lock();
        mLock.writeLock().tryLock();
        return sDBHelper.getReadableDatabase();
    }

    public static SQLiteDatabase getWritableDatabase() {
        mLock.writeLock().lock();
        return sDBHelper.getWritableDatabase();
    }


    public static void closeDataBase(SQLiteDatabase db) {
        if (mLock.isWriteLocked()) {
            try {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLock.writeLock().unlock();
        } else {
            if (mLock.getReadLockCount() <= 1) {
                try {
                    if (db != null && db.isOpen()) {
                        db.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mLock.readLock().unlock();
        }
    }

    public static void putValues(ContentValues values, String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            values.put(key, value);
        }
    }


}
