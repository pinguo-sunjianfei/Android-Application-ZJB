package com.idrv.coach.bean.parser;

import com.google.gson.Gson;
import com.idrv.coach.bean.AdvBean;
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
public class AdsParser extends BaseParser<List<AdvBean>> {
    @Override
    public List<AdvBean> parser(String json) {
        ArrayList<AdvBean> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                Gson gson = new Gson();
                AdvBean advBean = gson.fromJson(jsonString, AdvBean.class);
                lists.add(advBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
