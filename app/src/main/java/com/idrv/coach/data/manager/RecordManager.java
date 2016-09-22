package com.idrv.coach.data.manager;

import android.media.AudioManager;
import android.media.MediaRecorder;

import com.idrv.coach.data.model.RecordModel;
import com.idrv.coach.utils.FileUtil;
import com.idrv.coach.utils.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by 明明大美女 on 2016/2/19.
 */
public class RecordManager {
    public AudioStateListener mListener;

    private MediaRecorder mMediaRecorder;
    // 总路径（包含文件名）
    private String mCurrentFilePath;

    private boolean isPrepare;

    private static RecordManager mInstance;

    private RecordManager() {

    }

    public static RecordManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new RecordManager();
                }
            }
        }
        return mInstance;
    }

    public void prepareAudio() {
        try {
            isPrepare = false;

            mCurrentFilePath = FileUtil.createPath(FileUtil.DIR_TYPE_TEMP, generateFileName());
            mMediaRecorder = new MediaRecorder();
            // 设置输出文件
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            // 设置MediaRecorder的音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            // 设置音频的编码方式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            // 准备结束
            isPrepare = true;

            if (mListener != null) {
                mListener.wellPrepared();
            }

        } catch (IllegalStateException e) {
            Logger.e("录音准备失败", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Logger.e("录音准备失败2", e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Logger.e("录音准备失败-总的", e.toString());
        }
    }


    private String generateFileName() {
        return RecordModel.tempRecrodName;
//         return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if (isPrepare) {
            try {
                // mMediaRecorder.getMaxAmplitude() 1-32767
                return (maxLevel * mMediaRecorder.getMaxAmplitude()) / 32768;
            } catch (Exception e) {
                Logger.e("getVoiceLevel", e.toString());
                // TODO: handle exception
            }
        }
        return 1;
    }

    public void stop() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
        }
    }

    public void release() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }


    public void setOnAudioStateListener(AudioStateListener listener) {
        mListener = listener;
    }

    public interface AudioStateListener {
        void wellPrepared();
    }
}
