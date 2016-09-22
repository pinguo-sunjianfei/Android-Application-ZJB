package com.idrv.coach.bean.parser;

import com.google.gson.Gson;
import com.idrv.coach.bean.Rank;
import com.zjb.volley.bean.parser.BaseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/3/23
 * description:教练排行接口解析器
 *
 * @author sunjianfei
 */
public class CoachRankParser extends BaseParser<List<Rank>> {
    @Override
    public List<Rank> parser(String json) {
        ArrayList<Rank> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                Gson gson = new Gson();
                Rank rank = gson.fromJson(jsonString, Rank.class);
                lists.add(rank);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
