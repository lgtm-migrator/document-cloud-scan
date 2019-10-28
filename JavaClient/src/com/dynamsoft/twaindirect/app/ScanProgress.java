package com.dynamsoft.twaindirect.app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;

import okhttp3.Call;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dynamsoft.twaindirect.data.response.Address;

public class ScanProgress {

	public static String strOutPut = "C:\\temp";
	private static Logger logger = LoggerFactory.getLogger(ScanProgress.class.getName());

	static class WriteData {
		int imageBlockNum;

		public WriteData(int imageBlockNum, Address address, byte[] data) {
			this.imageBlockNum = imageBlockNum;
			this.address = address;
			this.data = data;
		}

		Address address;
		byte[] data;
	}


	public ScanProgress() {
	}

	public void scanStart() {
	}

	public void scanEnd() {
	}

	public void updateScanProgress(List<Integer> imageBlockList) {
		if (imageBlockList == null)
			return;
		int size = imageBlockList.size();
		if (size == 0) {
			return;
		}
		int now = ((Integer) imageBlockList.get(0)).intValue();
		int max = ((Integer) imageBlockList.get(size - 1)).intValue();
		setScan(now, max);
	}

	public void push(WriteData data) {
	}

	private int now = 0;
	private int max = 0;
	
	private void setScan(int now, int max) {
		this.now = now;
		this.max = max;
		if (now == 0 && max == 0) {
			System.out.println("Scanning...");
		} else {
			System.out.println(String.format("Scanning... %d/%d", this.now, this.max));
		}
	}

	private void setWrite(int now) {

		this.now = now;
		if (now == 0) {
			System.out.println("File Writing...");
		} else {
			System.out.println(String.format("File Writing... %d/%d", this.now, this.max));
		}
	}

	public void onFailure(Call call, IOException e) {
	}

	public void onError(String code) {
		System.out.println("An error has occurred.(" + code + ")");
	}

	public void onDetected(String detected) {
		System.out.println("A condition has been detected.(" + detected + ")");
	}

	class WriteThread extends Thread {
		private byte[] m_data;

		private String outputPath;

		private boolean boEnd = false;

		private Queue<ScanProgress.WriteData> writeQueue = new ArrayDeque<ScanProgress.WriteData>();
		private Object lockObj = new Object();

		public void end() {
			this.boEnd = true;
			synchronized (this.lockObj) {
				this.lockObj.notifyAll();
			}
			try {
				join();
			} catch (InterruptedException interruptedException) {
			}
		}

		public void push(ScanProgress.WriteData data) {
			synchronized (this.lockObj) {
				this.writeQueue.add(data);
				this.lockObj.notifyAll();
			}
		}

		public void run() {
			logger.info("Start");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
			Calendar cl = Calendar.getInstance();

			this.outputPath = (new File(strOutPut, sdf.format(cl.getTime()))).getPath();
			this.boEnd = false;

			while (!this.boEnd) {
				synchronized (this.lockObj) {
					if (this.writeQueue.isEmpty()) {
						try {
							this.lockObj.wait();
						} catch (InterruptedException interruptedException) {
						}
					}
				}

				if (this.writeQueue.isEmpty() && this.boEnd)
					break;
				File writeDir = new File(this.outputPath);
				if (!writeDir.exists()) {
					writeDir.mkdirs();
				}
				while (true) {
					ScanProgress.WriteData data;
					synchronized (this.lockObj) {
						data = (ScanProgress.WriteData) this.writeQueue.poll();
					}
					if (data == null)
						break;
					this.m_data = ArrayUtils.addAll(this.m_data, data.data);
					if (data.address.isLastPart()) {
						String fileName = (new File(this.outputPath, data.address.getSaveName())).getPath();
						logger.info("output:" + fileName);
						try {
							BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(fileName));
							bo.write(this.m_data);
							bo.flush();
							bo.close();
						} catch (Exception ex) {
							logger.error(ex.getMessage(), ex);
						}
						this.m_data = null;
					}
					ScanProgress.this.setWrite(data.imageBlockNum);
				}
			}
			logger.info("End");
		}
	}
}
