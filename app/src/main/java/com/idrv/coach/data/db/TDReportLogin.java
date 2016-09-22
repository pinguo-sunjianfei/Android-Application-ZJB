package com.idrv.coach.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.idrv.coach.bean.ReportLogin;
import com.idrv.coach.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * time:2016/3/29
 * description:
 *
 * @author sunjianfei
 */
public class TDReportLogin extends TDBase {
    private static final String TABLE_NAME = "report_login";
    public static final String ID = "_id";
    public static final String HOST = "host";
    public static final String LON = "longtitude";
    public static final String LAT = "latitude";
    public static final String ADDRESS = "address";
    public static final String CREATE = "created";
    public static final String COUNTRY = "country";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String DISTRICT = "district";
    public static final String STREET = "street";
    public static final String STREETNUM = "streetNum";
    public static final String CITYCODE = "cityCode";
    public static final String ADCODE = "adCode";
    public static final String AOINAME = "aoiName";

    public static String getCreateSQL() {
        return new StringBuilder()
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(TABLE_NAME).append("(")
                .append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(HOST).append(" VARCHAR(64),")
                .append(LON).append(" VARCHAR(32),")
                .append(LAT).append(" VARCHAR(32),")
                .append(ADDRESS).append(" VARCHAR(64),")
                .append(CREATE).append(" VARCHAR(32),")
                .append(COUNTRY).append(" VARCHAR(64),")
                .append(PROVINCE).append(" VARCHAR(128),")
                .append(CITY).append(" VARCHAR(128),")
                .append(DISTRICT).append(" VARCHAR(128),")
                .append(STREET).append(" VARCHAR(128),")
                .append(STREETNUM).append(" VARCHAR(128),")
                .append(CITYCODE).append(" VARCHAR(128),")
                .append(ADCODE).append(" VARCHAR(128),")
                .append(AOINAME).append(" VARCHAR(128)")
                .append(")")
                .toString();
    }

    public synchronized static String getDeleteSQL() {
        return "DROP TABLE " + TABLE_NAME;
    }

    public static Observable<List<ReportLogin>> getReportLogin() {
        return makeObservable(TDReportLogin::getReportLoginList);
    }

    private static List<ReportLogin> getReportLoginList() {
        SQLiteDatabase db = DBService.getReadableDatabase();
        List<ReportLogin> list = new ArrayList<>();
        try {
            db.beginTransaction();
            String sql = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                //1.构建基本信息
                ReportLogin login = new ReportLogin();
                buildReport(cursor, login);
                list.add(login);
            }
            Logger.e("开始查询历史提交记录");
            cursor.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DBService.closeDataBase(db);
        }
        return list;
    }

    public static boolean insertSync(ReportLogin login) {
        if (null == login || !login.isNewData()) return false;
        SQLiteDatabase db = DBService.getWritableDatabase();
        try {
            Logger.e("开始插入记录");
            db.beginTransaction();
            ContentValues values = new ContentValues();
            fillContentValue(values, login);
            //1.存放关系表
            db.insertWithOnConflict(TABLE_NAME, CREATE, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            DBService.closeDataBase(db);
        }
        return true;
    }

    public static void deleteSync(ReportLogin login) {
        if (null == login) return;
        SQLiteDatabase db = DBService.getWritableDatabase();
        try {
            Logger.e("开始删除记录");
            db.beginTransaction();
            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + CREATE + " = " + "'" + login.getCreated() + "'");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            DBService.closeDataBase(db);
        }
    }

    public static void fillContentValue(ContentValues values, ReportLogin login) {
        values.put(HOST, login.getHost());
        values.put(LON, login.getLongtitude());
        values.put(LAT, login.getLatitude());
        values.put(ADDRESS, login.getAddress());
        values.put(CREATE, login.getCreated());
        values.put(COUNTRY, login.getCountry());
        values.put(PROVINCE, login.getProvince());
        values.put(CITY, login.getCity());
        values.put(STREET, login.getStreet());
        values.put(STREETNUM, login.getStreetNum());
        values.put(DISTRICT, login.getDistrict());
        values.put(AOINAME, login.getAoiName());
        values.put(ADCODE, login.getAdCode());
        values.put(CITYCODE, login.getCityCode());
    }

    private static void buildReport(Cursor cursor, ReportLogin login) {
        login.setHost(cursor.getString(cursor.getColumnIndex(HOST)));
        login.setLongtitude(cursor.getString(cursor.getColumnIndex(LON)));
        login.setLatitude(cursor.getString(cursor.getColumnIndex(LAT)));
        login.setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS)));
        login.setCreated(cursor.getString(cursor.getColumnIndex(CREATE)));
        login.setCity(cursor.getString(cursor.getColumnIndex(CITY)));
        login.setStreet(cursor.getString(cursor.getColumnIndex(STREET)));
        login.setCityCode(cursor.getString(cursor.getColumnIndex(CITYCODE)));
        login.setAdCode(cursor.getString(cursor.getColumnIndex(ADCODE)));
        login.setAoiName(cursor.getString(cursor.getColumnIndex(AOINAME)));
        login.setCountry(cursor.getString(cursor.getColumnIndex(COUNTRY)));
        login.setDistrict(cursor.getString(cursor.getColumnIndex(DISTRICT)));
        login.setProvince(cursor.getString(cursor.getColumnIndex(PROVINCE)));
        login.setStreetNum(cursor.getString(cursor.getColumnIndex(STREETNUM)));
    }
}
