package com.idrv.coach.bean.parser;

import com.google.gson.Gson;
import com.idrv.coach.bean.DrivingTestInsDetail;
import com.zjb.volley.bean.parser.BaseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/3/23
 * description:保单接口解析器
 *
 * @author sunjianfei
 */
public class DrivingTestInsDetailParser extends BaseParser<List<DrivingTestInsDetail>> {
    @Override
    public List<DrivingTestInsDetail> parser(String json) {
        ArrayList<DrivingTestInsDetail> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                Gson gson = new Gson();
                DrivingTestInsDetail detail = gson.fromJson(jsonString, DrivingTestInsDetail.class);
                lists.add(detail);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
