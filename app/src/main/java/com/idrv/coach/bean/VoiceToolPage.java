package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/8/16
 * description:
 *
 * @author sunjianfei
 */
public class VoiceToolPage {
    List<VoiceTool> light;
    List<VoiceTool> subject_3;

    public List<VoiceTool> getLight() {
        return light;
    }

    public void setLight(List<VoiceTool> light) {
        this.light = light;
    }

    public List<VoiceTool> getSubject_3() {
        return subject_3;
    }

    public void setSubject_3(List<VoiceTool> subject_3) {
        this.subject_3 = subject_3;
    }
}
