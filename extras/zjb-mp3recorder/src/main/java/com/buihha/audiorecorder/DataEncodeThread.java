package com.buihha.audiorecorder;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.CountDownLatch;

public class DataEncodeThread extends Thread implements AudioRecord.OnRecordPositionUpdateListener {

	private static final String TAG = DataEncodeThread.class.getSimpleName();
	
	public static final int PROCESS_STOP = 1;

	private StopHandler handler;
	
	private byte[] buffer;
	
	private byte[] mp3Buffer;
	
	private RingBuffer ringBuffer;
	
	private FileOutputStream os;

	private int bufferSize;

	private CountDownLatch handlerInitLatch = new CountDownLatch(1);
	
	/**
	 * @see https://groups.google.com/forum/?fromgroups=#!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ
	 * @author buihong_ha
	 */
	static class StopHandler extends Handler {
		
		WeakReference<DataEncodeThread> encodeThread;
		
		public StopHandler(DataEncodeThread encodeThread) {
			this.encodeThread = new WeakReference<DataEncodeThread>(encodeThread);
		}
		
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == PROCESS_STOP) {
				DataEncodeThread threadRef = encodeThread.get();
				// Process all data in ring buffer and flush
				// left data to file
				while (threadRef.processData() > 0);
				// Cancel any event left in the queue
				removeCallbacksAndMessages(null);					
				threadRef.flushAndRelease();
				getLooper().quit();
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * Constructor
	 * @param ringBuffer
	 * @param os
	 * @param bufferSize
	 */
	public DataEncodeThread(RingBuffer ringBuffer, FileOutputStream os,
			int bufferSize) {
		this.os = os;
		this.ringBuffer = ringBuffer;
		this.bufferSize = bufferSize;
		buffer = new byte[bufferSize]; 
		mp3Buffer = new byte[(int) (7200 + (buffer.length * 2 * 1.25))];
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new StopHandler(this);
		handlerInitLatch.countDown();
		Looper.loop();
	}

	/**
	 * Return the handler attach to this thread
	 * @return
	 */
	public Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.e(TAG, "Error when waiting handle to init");
		}
		return handler;
	}

	@Override
	public void onMarkerReached(AudioRecord recorder) {
		// Do nothing		
	}

	@Override
	public void onPeriodicNotification(AudioRecord recorder) {
		processData();
	}
	
	/**
	 * Get data from ring buffer
	 * Encode it to mp3 frames using lame encoder
	 * @return  Number of bytes read from ring buffer
	 * 			0 in case there is no data left 
	 */
	private int processData() {		
		int bytes = ringBuffer.read(buffer, bufferSize);
		Log.d(TAG, "Read size: " + bytes);
		if (bytes > 0) {
			short[] innerBuf = new short[bytes / 2];
			ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(innerBuf);
			int encodedSize = SimpleLame.encode(innerBuf, innerBuf, bytes / 2, mp3Buffer);
			
			if (encodedSize < 0) {
				Log.e(TAG, "Lame encoded size: " + encodedSize);				
			}

			try {
				os.write(mp3Buffer, 0, encodedSize);
			} catch (IOException e) {
				Log.e(TAG, "Unable to write to file");
			}
			
			return bytes;
		}
		return 0;
	}
	
	/**
	 * Flush all data left in lame buffer to file
	 */
	private void flushAndRelease() {
		final int flushResult = SimpleLame.flush(mp3Buffer);

		if (flushResult > 0) {
			try {
				os.write(mp3Buffer, 0, flushResult);
			} catch (final IOException e) {
				// TODO: Handle error when flush
				Log.e(TAG, "Lame flush error");				
			}
		}
	}
}
