package com.idrv.coach.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.City;
import com.idrv.coach.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * time:15-9-29
 * description: 驾校表的数据库辅助类
 *
 * @author sunjianfei
 */
public class TDSchool extends TDBase {
    public static final String CITY_DB_NAME = "school.db";
    private static final String CITY_TABLE_NAME = "school_city";
    private static final String SCHOOL_TABLE_NAME = "school";
    private static SQLiteDatabase db;

    public static void openSchoolDB(Context context) {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + "com.idrv.coach" + File.separator
                + CITY_DB_NAME;
        File file = new File(path);
        if (!file.exists()) {
            Logger.e("db is not exist!");
            try {
                InputStream is = context.getAssets().open(CITY_DB_NAME);
                FileOutputStream fos = new FileOutputStream(file);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
    }


    private static List<City> getAllCity() {
        openSchoolDB(ZjbApplication.gContext);
        List<City> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + CITY_TABLE_NAME, null);
        try {
            while (c.moveToNext()) {
                String province = c.getString(c.getColumnIndex("province"));
                String city = c.getString(c.getColumnIndex("city"));
                String number = c.getString(c.getColumnIndex("number"));
                String allPY = c.getString(c.getColumnIndex("allpy"));
                String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
                String firstPY = c.getString(c.getColumnIndex("firstpy"));
                City item = new City(province, city, number, firstPY, allPY,
                        allFirstPY);
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            db.close();
            db = null;
        }
        return list;
    }

    private static List<String> getSchool(String cid) {
        openSchoolDB(ZjbApplication.gContext);
        List<String> list = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + SCHOOL_TABLE_NAME + " WHERE cid=?", new String[]{cid});
        try {
            while (c.moveToNext()) {
                String school_name = c.getString(c.getColumnIndex("school_name"));
                list.add(school_name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            c.close();
            db.close();
            db = null;
        }
        return list;
    }

    /**
     * 获取数据库中所有城市
     *
     * @return
     */
    public static Observable<List<City>> getCities() {
        return makeObservable(TDSchool::getAllCity);
    }

    /**
     * 根据城市id，查询对应的驾校
     */
    public static Observable<List<String>> getSchools(String cid) {
        return makeObservable(() -> TDSchool.getSchool(cid));
    }

    /**
     * 去掉市或县搜索
     *
     * @param city
     * @return
     */
    public static String parseName(String city) {
        if (city.contains("市")) {// 如果为空就去掉市字再试试
            String subStr[] = city.split("市");
            city = subStr[0];
        } else if (city.contains("县")) {// 或者去掉县字再试试
            String subStr[] = city.split("县");
            city = subStr[0];
        }
        return city;
    }

}
