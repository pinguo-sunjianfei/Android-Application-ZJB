package com.idrv.coach.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.idrv.coach.data.manager.LoginManager;
import com.idrv.coach.utils.Logger;


/**
 * time: 15/7/29
 * description: 数据库帮助类
 *
 * @author sunjianfei
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "zjb.db";
    /**
     * 5 -----1.0.3
     * 6 -----1.5
     */
    private static final int VERSION = 2;

    private static DBOpenHelper mDBOpenHelper;


    private DBOpenHelper(Context context) {
        super(context, getDbName(), null, VERSION);
    }

    public static synchronized DBOpenHelper getInstance(Context context) {
        if (mDBOpenHelper == null) {
            mDBOpenHelper = new DBOpenHelper(context.getApplicationContext());
        }
        return mDBOpenHelper;
    }

    /*切换账号之后需要重新初始化该账号对应的数据库*/
    public static void resetInstance() {
        mDBOpenHelper = null;
    }

    private static String getDbName() {
        String uid = LoginManager.getInstance().getUid();
        if (!TextUtils.isEmpty(uid)) {
            return "zjb" + uid.trim() + ".db";
        }
        return DB_NAME;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            createTable(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.e("DBOpenHelper--->onUpgrade!--->oldVersion:" + oldVersion);
        try {
            //升级
            if (oldVersion < 2) {
                dropAllTable(db);
                createTable(db);
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL(TDReportLogin.getCreateSQL());
    }

    private void dropAllTable(SQLiteDatabase db) {
        db.execSQL(TDReportLogin.getDeleteSQL());
    }

}
