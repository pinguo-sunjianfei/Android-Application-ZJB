package com.idrv.coach.data.model;

import com.idrv.coach.R;
import com.idrv.coach.ZjbApplication;
import com.idrv.coach.bean.VoiceTool;
import com.idrv.coach.bean.VoiceToolPage;
import com.idrv.coach.utils.FileUtil;
import com.idrv.coach.utils.helper.ResHelper;
import com.zjb.volley.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * time:2016/8/16
 * description:
 *
 * @author sunjianfei
 */
public class VoiceToolModel {
    int[] lightArray = {R.raw.light1, R.raw.light2, R.raw.light3, R.raw.light4, R.raw.light5, R.raw.light6, R.raw.light7, R.raw.light8};
    int[] subjectArray = {R.raw.subject_3_1, R.raw.subject_3_2, R.raw.subject_3_7, R.raw.subject_3_5, R.raw.subject_3_13, R.raw.subject_3_12,
            R.raw.subject_3_3, R.raw.subject_3_8, R.raw.subject_3_9, R.raw.subject_3_4, R.raw.subject_3_18, R.raw.subject_3_17, R.raw.subject_3_14,
            R.raw.subject_3_16, R.raw.subject_3_10, R.raw.subject_3_11, R.raw.subject_3_15, R.raw.subject_3_19, R.raw.subject_3_6};

    int[] subjectIconArray = {R.drawable.icon_subject_3_1, R.drawable.icon_subject_3_2, R.drawable.icon_subject_3_7, R.drawable.icon_subject_3_5,
            R.drawable.icon_subject_3_13, R.drawable.icon_subject_3_12, R.drawable.icon_subject_3_3, R.drawable.icon_subject_3_8, R.drawable.icon_subject_3_9,
            R.drawable.icon_subject_3_4, R.drawable.icon_subject_3_18, R.drawable.icon_subject_3_17, R.drawable.icon_subject_3_14, R.drawable.icon_subject_3_16,
            R.drawable.icon_subject_3_10, R.drawable.icon_subject_3_11, R.drawable.icon_subject_3_15, R.drawable.icon_subject_3_19, R.drawable.icon_subject_3_6};

    /**
     * 获取语音播报数据
     *
     * @return
     */
    public Observable<List<VoiceTool>> getVoiceToolList() {
        Observable<List<VoiceTool>> observable = Observable.<List<VoiceTool>>create(subscriber -> {
            try {
                String json = FileUtil.getTextFromAssets(ZjbApplication.gContext, "voice.json");
                VoiceToolPage page = GsonUtil.fromJson(json, VoiceToolPage.class);
                subscriber.onNext(createVoiceList(page));
                subscriber.onCompleted();
            } catch (Exception e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
        return observable;
    }

    private List<VoiceTool> createVoiceList(VoiceToolPage page) {
        List<VoiceTool> result = new ArrayList<>();
        List<VoiceTool> lights = page.getLight();
        List<VoiceTool> subject_3 = page.getSubject_3();
        int lightSize = lights.size();
        int subject_3_Size = subject_3.size();

        for (int i = 0; i < lightSize; i++) {
            VoiceTool tool = lights.get(i);
            tool.setResId(lightArray[i]);
            tool.setIconRes(R.drawable.icon_light);
        }

        for (int i = 0; i < subject_3_Size; i++) {
            VoiceTool tool = subject_3.get(i);
            tool.setResId(subjectArray[i]);
            tool.setIconRes(subjectIconArray[i]);
        }

        VoiceTool light = new VoiceTool();
        light.setGroup(true);
        light.setGroupName(ResHelper.getString(R.string.light));

        VoiceTool subject = new VoiceTool();
        subject.setGroup(true);
        subject.setGroupName(ResHelper.getString(R.string.subject_3));

        result.add(light);
        result.addAll(lights);
        result.add(subject);
        result.addAll(subject_3);
        return result;
    }
}
