package com.idrv.coach.bean.parser;

import com.google.gson.Gson;
import com.idrv.coach.bean.WebSite;
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
public class WebSiteParser extends BaseParser<List<WebSite>> {
    @Override
    public List<WebSite> parser(String json) {
        ArrayList<WebSite> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                Gson gson = new Gson();
                WebSite webSite = gson.fromJson(jsonString, WebSite.class);
                lists.add(webSite);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
