package com.buihha.audiorecorder;

import android.util.Log;

public class RingBuffer {
	private byte[] buffer;
	
	private int size;
	
	private int rp;
	
	private int wp;
	
	/**
	 * Initialize a ring buffer given number of bytes
	 * @param size
	 */
	public RingBuffer(int size) {
		this.size = size;
		buffer = new byte[size];
		wp = rp = 0;
	}
	
	/**
	 * Check number of bytes left
	 * 
	 * @param writeCheck
	 * @return
	 */
	private int checkSpace(boolean writeCheck) {
		int s;
		if (writeCheck) {
			if (wp > rp) {
				s = rp - wp + size - 1;
			} else if (wp < rp) {
				s = rp - wp - 1;
			} else s = size - 1;
		} else {
			if (wp > rp) {
				s = wp - rp;				
			} else if (wp < rp) {
				s = wp - rp + size;
			} else {
				s = 0;
			}
		}
		return s;
	}
	
	/**
	 * Read a number of bytes from ring buffer
	 * 
	 * @param buffer
	 * @param bytes
	 * @return
	 */
	public int read(byte[] buffer, final int bytes) {
		int remaining;
		if ((remaining = checkSpace(false)) == 0) {
			Log.d(RingBuffer.class.getSimpleName(), "No data");
			return 0;
		}
		final int bytesRead = bytes > remaining ? remaining : bytes;
		// copy from ring buffer to buffer
		for (int i = 0; i < bytesRead; ++i) {
			buffer[i] = this.buffer[rp++];
			if (rp == size) rp = 0;
		}
		return bytesRead;
	}
	
	/**
	 * Write a number of bytes to ring buffer;
	 * 
	 * @param buffer
	 * @param bytes
	 * @return
	 */
	public int write(byte[] buffer, final int bytes) {
		int remaining;
		if ((remaining = checkSpace(true)) == 0) {
			Log.e(RingBuffer.class.getSimpleName(), "Buffer overrun. Data will not be written");
			return 0;
		}
		final int bytesWrite = bytes > remaining ? remaining : bytes;
		for (int i = 0; i < bytesWrite; ++i) {
			this.buffer[wp++] = buffer[i];
			if (wp == size) wp = 0;
		}
		return bytesWrite;
	}
}
