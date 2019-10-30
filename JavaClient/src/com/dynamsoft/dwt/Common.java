package com.dynamsoft.dwt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public abstract class Common
{
    public static final int DWT_MainVer = 15; // DWT12.1 or DWT15.2 
    
    public static final String DWT_UserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36";
	public static final boolean bSSL = false;
    public static final String DWT_IP = "127.0.0.1";
    public static final String DCP_Port = "18625";
    public static final String DWT_Port = Common.getDWT_port();
    public static final String DWT_Version = "dwt_trial_15200924";
    public static final String DWT_WS_Command = "dwt_command";
    public static final String DWT_ProductKey = "LICENSE-KEY"; // <--- MUST CHANGE IT ----- (DWT Product Key) !!!
    
    public static final String getDWT_port() {
    	if(Common.DWT_MainVer<15)
    		return "18618";
    	else
    		return "18622";
    }
    
    public static final String getDWT_sync_path() {
    	if(Common.DWT_MainVer<15)
    		return "/f/";
    	else
    		return "/fa";
    }

    public static String getTempPDF() {
    	return System.getProperty("java.io.tmpdir");
    }
    
    public static byte[] readFileByBytes(String fileName) {
    	System.out.println("-> SaveAsPDF: fileName " + fileName);
    	
        File file = null;
    	InputStream fis = null;

		try {
			file = new File(fileName);
	        if (file.isFile() && file.exists()) {
	
    			fis = new FileInputStream(file);

    	        int len = fis.available();
    	        byte[] bytes = new byte[len];
    	        fis.read(bytes);
    	        

            	System.out.println("-> SaveAsPDF: read " + bytes.length);
    			return bytes;
	        } else {
	        	System.out.println("-> SaveAsPDF: file not exists.");
	        }
		} catch (Exception e1) {

        	System.out.println("-> SaveAsPDF: read file failed.");
        	System.out.println(e1.getMessage());
        	
		} finally {
			if(null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
	            	System.out.println("-> SaveAsPDF: close file failed.");
	            	System.out.println(e.getMessage());
				}
			}
		}
        return null;
    }
    
    public static String getServerRoot() {
    	StringBuffer strb = new StringBuffer();
    	if(Common.bSSL)
    		strb.append("https://");
    	else
    		strb.append("http://");
    	strb.append(Common.DWT_IP);
    	strb.append(":");
    	strb.append(Common.DWT_Port);
    	
    	if(Common.DWT_MainVer<15)
    	{
    		
    	}
    	else {
        	strb.append("/dwt/");
        	strb.append(DWT_Version);
    	}
    	
    	return strb.toString();
    }
    
    public static String getServerPathAsync() {
    	StringBuffer strb = new StringBuffer();
    	if(Common.bSSL)
    		strb.append("https://");
    	else
    		strb.append("http://");
    	strb.append(Common.DWT_IP);
    	strb.append(":");
    	strb.append(Common.DWT_Port);
    	strb.append(Common.getDWT_sync_path());
		strb.append("/");
    	return strb.toString();
    }

    public static String getWSUrl() {
    	StringBuffer strb = new StringBuffer();
    	if(Common.bSSL)
    		strb.append("wss://");
    	else
    		strb.append("ws://");
    	strb.append(Common.DWT_IP);
    	strb.append(":");
    	strb.append(Common.DWT_Port);
    	return strb.toString();
    }
    
    public static String getDCPServerPathAsync() {
    	StringBuffer strb = new StringBuffer();
    	if(Common.bSSL)
    		strb.append("https://");
    	else
    		strb.append("http://");
    	strb.append(Common.DWT_IP);
    	strb.append(":");
    	strb.append(Common.DCP_Port);
    	strb.append(Common.getDWT_sync_path());
    	return strb.toString();
    }
}
