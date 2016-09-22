package com.idrv.coach.bean.parser;

import com.google.gson.Gson;
import com.idrv.coach.bean.Picture;
import com.zjb.volley.bean.parser.BaseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/3/23
 * description:照片墙接口解析器
 *
 * @author sunjianfei
 */
public class PhotoWallParser extends BaseParser<List<Picture>> {
    @Override
    public List<Picture> parser(String json) {
        ArrayList<Picture> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                Gson gson = new Gson();
                Picture picture = gson.fromJson(jsonString, Picture.class);
                lists.add(picture);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
