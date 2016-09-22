package com.buihha.audiorecorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Mp3Recorder {

    private static final String TAG = Mp3Recorder.class.getSimpleName();
    String audioOutputPath;

    static {
        System.loadLibrary("mp3lame");
    }

    private static final int DEFAULT_SAMPLING_RATE = 22050;

    private static final int FRAME_COUNT = 160;

    /* Encoded bit rate. MP3 file will be encoded with bit rate 32kbps */
    private static final int BIT_RATE = 32;

    private AudioRecord audioRecord = null;

    private int bufferSize;

    private File mp3File;

    private RingBuffer ringBuffer;

    private byte[] buffer;

    private FileOutputStream os = null;

    private DataEncodeThread encodeThread;

    private int samplingRate;

    private int channelConfig;

    private PCMFormat audioFormat;

    private boolean isRecording = false;
    private VolumeChangeListener mVolumeChangeListener;

    private static final int LEVEL_1 = 1;
    private static final int LEVEL_2 = 2;
    private static final int LEVEL_3 = 3;
    private static final int LEVEL_4 = 4;
    private static final int LEVEL_5 = 5;

    public Mp3Recorder(int samplingRate, int channelConfig,
                       PCMFormat audioFormat) {
        this.samplingRate = samplingRate;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
    }

    /**
     * Default constructor. Setup recorder with default sampling rate 1 channel,
     * 16 bits pcm
     */
    public Mp3Recorder() {
        this(DEFAULT_SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO,
                PCMFormat.PCM_16BIT);
    }

    public void setAudioOutputPath(String audioOutputPath) {
        this.audioOutputPath = audioOutputPath;
    }

    public String getAudioOutputPath() {
        return audioOutputPath;
    }

    public void setVolumeChangeListener(VolumeChangeListener listener) {
        this.mVolumeChangeListener = listener;
    }

    /**
     * Start recording. Create an encoding thread. Start record from this
     * thread.
     *
     * @throws IOException
     */
    public void startRecording() throws IOException {
        if (isRecording) return;
        Log.d(TAG, "Start recording");
        Log.d(TAG, "BufferSize = " + bufferSize);
        // Initialize audioRecord if it's null.
        if (audioRecord == null) {
            initAudioRecorder();
        }
        audioRecord.startRecording();
        new Thread() {

            @Override
            public void run() {
                isRecording = true;
                long time1 = System.currentTimeMillis();
                while (isRecording) {
                    int bytes = audioRecord.read(buffer, 0, bufferSize);
                    if (bytes > 0) {
                        ringBuffer.write(buffer, bytes);
                    }

                    int v = 0;
                    for (int i = 0; i < bytes; i++) {
                        v += buffer[i] * buffer[i];
                    }
                    long time2 = System.currentTimeMillis();
                    if (null != mVolumeChangeListener) {
                        if (time2 - time1 > 100) {
                            // 平方和除以数据总长度，得到音量大小。
                            double mean = v / (float) bytes;
                            double f = 10 * Math.log10(mean);
                            Log.e("音量 = ", f + "");
                            if (f <= 30) {
                                mVolumeChangeListener.onVolumeChange(LEVEL_1);
                                Log.e("音量", "音量 >> 1");
                            } else if (f <= 80 && f > 30) {
                                mVolumeChangeListener.onVolumeChange(LEVEL_2);
                                Log.e("音量", "音量 >> 2");
                            } else if (f <= 120 && f > 80) {
                                mVolumeChangeListener.onVolumeChange(LEVEL_3);
                                Log.e("音量", "音量 >> 3");
                            } else if (f <= 160 && f > 120) {
                                mVolumeChangeListener.onVolumeChange(LEVEL_4);
                                Log.e("音量", "音量 >> 4");
                            } else if (f > 160) {
                                mVolumeChangeListener.onVolumeChange(LEVEL_5);
                                Log.e("音量", "音量 >> 5");
                            }
                            time1 = time2;
                        }
                    }
                }

                // release and finalize audioRecord
                try {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;

                    // stop the encoding thread and try to wait
                    // until the thread finishes its job
                    Message msg = Message.obtain(encodeThread.getHandler(),
                            DataEncodeThread.PROCESS_STOP);
                    msg.sendToTarget();

                    Log.d(TAG, "waiting for encoding thread");
                    encodeThread.join();
                    Log.d(TAG, "done encoding thread");
                } catch (InterruptedException e) {
                    Log.d(TAG, "Faile to join encode thread");
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }.start();
    }

    /**
     * @throws IOException
     */
    public void stopRecording() throws IOException {
        Log.d(TAG, "stop recording");
        isRecording = false;
    }

    /**
     * Initialize audio recorder
     */
    private void initAudioRecorder() throws IOException {
        int bytesPerFrame = audioFormat.getBytesPerFrame();
        /* Get number of samples. Calculate the buffer size (round up to the
           factor of given frame size) */
        int frameSize = AudioRecord.getMinBufferSize(samplingRate,
                channelConfig, audioFormat.getAudioFormat()) / bytesPerFrame;
        if (frameSize % FRAME_COUNT != 0) {
            frameSize = frameSize + (FRAME_COUNT - frameSize % FRAME_COUNT);
            Log.d(TAG, "Frame size: " + frameSize);
        }

        bufferSize = frameSize * bytesPerFrame;

		/* Setup audio recorder */
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                samplingRate, channelConfig, audioFormat.getAudioFormat(),
                bufferSize);

        // Setup RingBuffer. Currently is 10 times size of hardware buffer
        // Initialize buffer to hold data
        ringBuffer = new RingBuffer(10 * bufferSize);
        buffer = new byte[bufferSize];

        // Initialize lame buffer
        // mp3 sampling rate is the same as the recorded pcm sampling rate
        // The bit rate is 32kbps
        SimpleLame.init(samplingRate, 1, samplingRate, BIT_RATE);

        if (TextUtils.isEmpty(audioOutputPath)) {
            throw new NullPointerException("please set the audio output path!");
        }
        mp3File = new File(audioOutputPath);
        os = new FileOutputStream(mp3File);

        // Create and run thread used to encode data
        // The thread will
        encodeThread = new DataEncodeThread(ringBuffer, os, bufferSize);
        encodeThread.start();
        audioRecord.setRecordPositionUpdateListener(encodeThread, encodeThread.getHandler());
        audioRecord.setPositionNotificationPeriod(FRAME_COUNT);
    }

    public interface VolumeChangeListener {
        void onVolumeChange(int level);
    }
}