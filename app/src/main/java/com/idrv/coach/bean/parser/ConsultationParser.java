package com.idrv.coach.bean.parser;

import com.google.gson.Gson;
import com.idrv.coach.bean.Consultation;
import com.zjb.volley.bean.parser.BaseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/6/6
 * description:
 *
 * @author sunjianfei
 */
public class ConsultationParser extends BaseParser<List<Consultation>> {
    @Override
    public List<Consultation> parser(String json) {
        ArrayList<Consultation> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                Gson gson = new Gson();
                Consultation consultation = gson.fromJson(jsonString, Consultation.class);
                lists.add(consultation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
