package com.idrv.coach.bean.parser;

import com.google.gson.Gson;
import com.idrv.coach.bean.Comment;
import com.zjb.volley.bean.parser.BaseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/6/6
 * description:评论解析器
 *
 * @author sunjianfei
 */
public class CommentParser extends BaseParser<List<Comment>> {
    @Override
    public List<Comment> parser(String json) {
        ArrayList<Comment> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                Gson gson = new Gson();
                Comment comment = gson.fromJson(jsonString, Comment.class);
                lists.add(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
