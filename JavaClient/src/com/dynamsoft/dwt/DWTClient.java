package com.dynamsoft.dwt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.dynamsoft.DLogger;
import com.dynamsoft.IDProvider;
import com.dynamsoft.dwt.common.EnumDWT_ImageType;
import com.dynamsoft.dwt.evt.DWTEventHandler;
import com.dynamsoft.dwt.evt.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;


public class DWTClient implements WSHandler
{
	private final Base64.Decoder base64Decoder = Base64.getDecoder();
	
	private boolean bReady;
	private boolean bDisposed;
	private DWTWebsocketClient wsClient;
    private DWTDataProvider dataProvider;
	private ArrayList<DWTCmdCallback> curCommand = new ArrayList<DWTCmdCallback>();
	private long _errorCode = 0;
	private String _errorString = "";
	private List<String> ids;
	
	public Map<EventType, DWTEventHandler> handlerMap = null;
	
    public DWTClient()
    {
		try {
			this.bReady = false;
			this.bDisposed = false;
			this.ids = new ArrayList<String>();
			handlerMap = new HashMap<EventType, DWTEventHandler>();
			
	    	long id = IDProvider.getInstance().getRamdomIDAsLong();
	    	dataProvider = new DWTDataProvider(id);
            
        	Map<String, String> headers = new HashMap<String, String>();
        	headers.put("User-Agent", Common.DWT_UserAgent);
        	headers.put("Origin", "http://localhost");

        	wsClient = new DWTWebsocketClient(this, Common.getWSUrl(), headers);
        	wsClient.connect();
        	
            
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
    }
    
    public boolean isDisposed()
    {
    	return this.bDisposed;
    }
    
    public long getErrorCode()
    {
    	return this._errorCode;
    }
    
    public String getErrorString()
    {
    	return this._errorString;
    }

    private DWTResult GetResult(String strJson)
    {
        System.out.print(strJson);
        
        JSONObject objJson = JSONObject.fromObject(strJson);
        
        DWTResult p1 = new DWTResult();
  
        p1.id = objJson.getString("id");  
        p1.method = objJson.getString("method");  
        p1.cmdId = objJson.getString("cmdId");  
        p1.result = objJson.getJSONArray("result");  

        return p1;
    }
    
    public List<String> GetSourceNames()
    {
    	List<String> ret = new ArrayList<String>();
		if(this.isDisposed())
			return ret;
		
        String strData = dataProvider.getData("GetSourceNames", "");
        DWTResult p1 = this.post(Common.getServerPathAsync() + "GetSourceNames", strData);
        if(p1 == null)
        	return ret;
        
        int len = p1.result.size(); 
        if (len > 0) {
        	if(len > 1)
        		len--;
        	
        	for(int i=0; i<len; i++)
        	{
            	Object json = p1.result.get(i);
            	if(json instanceof String) {
            		ret.add((String) json);
            	} else {
            		ret.add(json.toString());	
            	}
        	}
        }
            
        return ret;
    }
    
    public boolean SetProductKey(String strProductKey)
    {
    	return this.innerInvokeReturnBool("ProductKey", String.format("\"%s\"", strProductKey));
    }

    public boolean Rotate(int sImageIndex, double fAngle)
    {
    	return this.innerInvokeReturnBool("Rotate", String.format("%d,%f,true", sImageIndex, fAngle));
    }
    
    public boolean Crop(int sImageIndex, double left, double top, double width, double height)
    {
    	return this.innerInvokeReturnBool("Crop", String.format("%d,%f,%f,%f,%f", sImageIndex, left, top, left + width, top + height));
    }
    

    public boolean XferCount(int count)
    {
    	return this.innerInvokeReturnBool("XferCount", String.valueOf(count));
    }
    
    
    public int GetImageWidth(int sImageIndex)
    {
		if(this.isDisposed())
			return 0;
		
        String strData = dataProvider.getData("GetImageWidth", String.format("%d", sImageIndex));
        DWTResult p1 = this.post(Common.getServerPathAsync() + "GetImageWidth", strData);
        if(p1 == null)
        	return 0;
        
        if (p1.result.size() > 0)
            return Integer.parseInt(p1.result.get(0).toString());
        else
            return 0;

    }
    public int GetImageHeight(int sImageIndex)
    {

		if(this.isDisposed())
			return 0;
		
        String strData = dataProvider.getData("GetImageHeight", String.format("%d", sImageIndex));
        DWTResult p1 = this.post(Common.getServerPathAsync() + "GetImageHeight", strData);
        if(p1 == null)
        	return 0;
        
        if (p1.result.size() > 0)
            return Integer.parseInt(p1.result.get(0).toString());
        else
            return 0;

    }
    public boolean SetPDFConvertMode(int mode)
    {
    	return this.invokeReturnBool("PDFConvertMode", mode);
    }
    public String VersionInfo()
    {

		if(this.isDisposed())
			return "";
		
    	String strData = dataProvider.getData("VersionInfo", "");
        DWTResult p1 = this.post(Common.getServerPathAsync() + "VersionInfo", strData);
        if(p1 == null)
        	return "";
        
        if (p1.result.size() > 0)
            return p1.result.get(0).toString();
        else
            return "";
    }
    

    public boolean SetPixelType(int pixelType)
    {
    	return this.invokeReturnBool("PixelType", pixelType);
    }

    public int GetPixelType()
    {
		if(this.isDisposed())
			return -1;

    	String strData = dataProvider.getData("PixelType", "");
        DWTResult p1 = this.post(Common.getServerPathAsync() + "PixelType", strData);
        if(p1 == null)
			return -1;

        if (p1.result.size() > 0)
            return p1.result.getInt(0);
        else
			return -1;
    }

    public boolean SetResolution(int resolution)
    {
    	return this.invokeReturnBool("Resolution", resolution);
    }

    public int GetResolution()
    {
		if(this.isDisposed())
			return -1;

    	String strData = dataProvider.getData("Resolution", "");
        DWTResult p1 = this.post(Common.getServerPathAsync() + "Resolution", strData);
        if(p1 == null)
			return -1;

        if (p1.result.size() > 0)
            return p1.result.getInt(0);
        else
			return -1;
    }
    
    public boolean LoadImage(String strPath)
    {
    	return this.innerInvokeReturnBool("LoadImage", String.format("\"%s\"", strPath.replace("\\", "\\\\")));
    }
    
    public boolean LoadImageEx(String strPath, int ImageType)
    {
    	return this.innerInvokeReturnBool("LoadImageEx", String.format("\"%s\",%d", strPath.replace("\\", "\\\\"), ImageType));
    }
    public boolean IfDisableSourceAfterAcquire(boolean boolVal)
    {
		return this.setBoolean("IfDisableSourceAfterAcquire", boolVal);
    }
    public boolean IfDuplexEnabled(boolean boolVal)
    {
		return this.setBoolean("IfDuplexEnabled", boolVal);
    }
    public boolean IfShowFileDialog(boolean bShow)
    {
		return this.setBoolean("IfShowFileDialog", bShow);
    }

    public boolean IfAutomaticDeskew(boolean bAutomaticDeskew)
    {
		return this.setBoolean("IfAutomaticDeskew", bAutomaticDeskew);
    }

    public boolean IfAutoDiscardBlankpages(boolean bAutoDiscardBlankpages)
    {
		return this.setBoolean("IfAutoDiscardBlankpages", bAutoDiscardBlankpages);
    }

    public boolean IfAutoFeed(boolean bAutoFeed)
    {
		return this.setBoolean("IfAutoFeed", bAutoFeed);
    }

	public boolean IfShowUI(boolean bShow) {
		return this.setBoolean("IfShowUI", bShow);
	}

    public void openSource()
    {
    	this.invokeReturnVoid("OpenSource");
    }
    public void openSourceManager()
    {
    	this.invokeReturnVoid("OpenSourceManager");
    }
    public void closeSource()
    {
    	this.invokeReturnVoid("CloseSource");
    }
    public void closeSourceManager()
    {
    	this.invokeReturnVoid("CloseSourceManager");
    }
    public void closeWorkingProcess()
    {
    	this.invokeReturnVoid("CloseWorkingProcess");
    }
    
    public void cancelAllPendingTransfers()
    {
    	this.invokeReturnVoid("CancelAllPendingTransfers");
    }
    public void feedPage()
    {
    	this.invokeReturnVoid("FeedPage");
    }
    public void rewindPage()
    {
    	this.invokeReturnVoid("RewindPage");
    }
    
    public boolean selectSourceByIndex(int index)
    {
    	return this.invokeReturnBool("SelectSourceByIndex", index);
    }
    
	private boolean setBoolean(String method, boolean boolVal)
	{
		return this.innerInvokeReturnBool(method, String.format("%s", boolVal ? "true" : "false"));
	}
	private boolean invokeReturnBool(String method, int val)
	{
		return this.innerInvokeReturnBool(method, String.valueOf(val));
	}
	private boolean innerInvokeReturnBool(String method, String val)
	{
		if(this.isDisposed())
			return false;
		
        String strData = dataProvider.getData(method, val);
        DWTResult p1 = this.post(Common.getServerPathAsync() + method, strData);
        if(p1 == null)
        	return false;
        
        if (null == p1.exception && p1.result.size() > 0) {
            return (p1.result.get(0).equals("ok") || p1.result.get(0).equals("true") || p1.result.get(0).equals("1")
            		 || p1.result.get(0).equals(true) || p1.result.get(0).equals(1) || p1.result.get(0).equals(1.0));
        }
        else
            return false;
	}
	
	
    public int HowManyImagesInBuffer()
    {

		if(this.isDisposed())
			return 0;
		
        int iRet = 0;
        String strData = dataProvider.getData("HowManyImagesInBuffer", "");
        DWTResult p1 = this.post(Common.getServerPathAsync() + "HowManyImagesInBuffer", strData);
        if(p1 == null)
        	return iRet;
        
        if (p1.result.size() > 0)
        {
            return Integer.parseInt(p1.result.get(0).toString());
        }

        return iRet;
    }

    

    private void invokeReturnVoid(String method)
    {
		if(this.isDisposed())
			return;
		
        this.post(Common.getServerPathAsync() + method, dataProvider.getData(method, ""));
    }
    
    public boolean RemoveAllImages()
    {
    	this.ids.clear();
    	return this.innerInvokeReturnBool("RemoveAllImages", "");
    }


	public boolean SetPDFCompressionType(int pdfCompressionType) {

    	return this.innerInvokeReturnBool("PDFCompressionType", String.valueOf(pdfCompressionType));
	}

    public boolean Save(String path, int index, int ImageType)
    {

		if(this.isDisposed())
			return false;
		
        String method;
        if (ImageType == EnumDWT_ImageType.IT_BMP)
        {
            method = "SaveAsBMP";
        }
        else if (ImageType == EnumDWT_ImageType.IT_JPG)
        {
            method = "SaveAsJPEG";
        }
        else if (ImageType == EnumDWT_ImageType.IT_TIF)
        {
            method = "SaveAsTIFF";
        }
        else if (ImageType == EnumDWT_ImageType.IT_PNG)
        {
            method = "SaveAsPNG";
        }
        else if (ImageType == EnumDWT_ImageType.IT_PDF)
        {
            method = "SaveAsPDF";
        }
        else
        {
            return false;
        }

    	return this.innerInvokeReturnBool(method, String.format("\"%s\",%d", path.replace("\\", "\\\\"), index));

    }
    public boolean SaveAllAsMultiPageTIFF(String path)
    {
    	return this.innerInvokeReturnBool("SaveAllAsMultiPageTIFF", String.format("\"%s\"", path.replace("\\", "\\\\")));
    }
    
    public boolean SaveAllAsPDF(String path)
    {
    	return this.innerInvokeReturnBool("SaveAllAsPDF", String.format("\"%s\"", path.replace("\\", "\\\\")));
    }

    public byte[] GetImageAsPDF(int index)
    {
        System.out.println("Get Image by index: " + String.valueOf(index));
		return this.GetImageAsPDF_v12(index);
    	//	return this.GetImageAsPDF_v15(index);
    }

    public byte[] GetImageAsPDF_v15(int index)
    {
		String serverId = this.ids.get(index);
        System.out.println("-> the serverId: " + String.valueOf(serverId));

		StringBuffer params = new StringBuffer();
		params.append(EnumDWT_ImageType.IT_PDF);
		params.append(',');
		params.append("\"" + String.valueOf(serverId) + "\"");
		
        String strData = dataProvider.getData("ConvertToBlob", params.toString());
        byte[] ret = this.postRecieveBinary(Common.getServerPathAsync() + "ConvertToBlob", strData);
        if(ret == null)
        	return null;

        return ret;
    }
    
    public byte[] GetImageAsPDF_v12(int index) {

        System.out.println("start GetImageAsPDF_v12:");
        
        String serverId = null; 
        if(Common.DWT_MainVer<15)
    	{
        	serverId = String.valueOf(index);
    	} else {
    		serverId = this.ids.get(index);
    	}
        
        long ticks = IDProvider.getInstance().getRamdomIDAsLong();
    	
        String url = String.format("%s/img?id=%s&index=%s&ticks=%d", Common.getServerRoot(), this.dataProvider.getClientId(), serverId, ticks);

    	System.out.println(url);
    	
    	byte[] binaryData = null;
		try {
			binaryData = PDFUtil.createPDFFromURL(new URL(url));
		} catch (IOException e) {
			e.printStackTrace();
	    	System.out.println(e.getMessage());
		}
        
        System.out.println("--> post PDF Data Real Length: " + binaryData!=null?binaryData.length:"(null)");
        return binaryData;
    }
    
    public byte[] GetAllImagesAsPDF()
    {

    	if(Common.DWT_MainVer<15)
    	{
    		return this.GetImageAsPDF_v12(0);
    	} else {
            return GetImageAsPDF(0);
    	}
    }

    private byte[] getRecieveBinary(String url) {
    	int SUCCESS_CODE = 200;
    	
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            client = HttpClients.createDefault();
            HttpGet get = new HttpGet(url);
            
            get.setProtocolVersion(HttpVersion.HTTP_1_1);
            get.setHeader(new BasicHeader("Content-Type", "text/plain; charset=UTF-8"));
            get.setHeader(new BasicHeader("Accept", "text/plain, */*; q=0.01"));
            get.setHeader(new BasicHeader("User-Agent", Common.DWT_UserAgent));
            get.setHeader(new BasicHeader("Referer", "http://localhost/test.html"));
            get.setHeader(new BasicHeader("Origin", "http://localhost"));
            
            response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode){
            	HttpEntity retEntity = response.getEntity();
    	        InputStream stream = retEntity.getContent();
            	
    	        int len = stream.available();
    	        byte[] ary = new byte[len];
    	        stream.read(ary);
    	        
    	        return ary;
            }
            response.close();
            client.close();
        } catch (ClientProtocolException e) {
        	System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
        return null;
	}
    
    private byte[] postRecieveBinary(String url, String strPostData) {
    	int SUCCESS_CODE = 200;
    	
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(strPostData, "utf-8");
            post.setEntity(entity);
            
            post.setProtocolVersion(HttpVersion.HTTP_1_1);
			post.setHeader(new BasicHeader("Content-Type", "text/plain; charset=UTF-8"));
            post.setHeader(new BasicHeader("Accept", "text/plain, */*; q=0.01"));
            post.setHeader(new BasicHeader("User-Agent", Common.DWT_UserAgent));
            post.setHeader(new BasicHeader("Referer", "http://localhost/test.html"));
            post.setHeader(new BasicHeader("Origin", "http://localhost"));
            
            
            response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode){
            	HttpEntity retEntity = response.getEntity();
    	        InputStream stream = retEntity.getContent();
            	
    	        int len = stream.available();
    	        byte[] ary = new byte[len];
    	        stream.read(ary);
    	        
    	        return ary;
            }
            response.close();
            client.close();
        } catch (ClientProtocolException e) {
        	System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
        return null;
	}

	private DWTResult post(String url, String strPostData)
    {
    	int SUCCESS_CODE = 200;
    	
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(strPostData, "utf-8");
            post.setEntity(entity);
            
            post.setProtocolVersion(HttpVersion.HTTP_1_1);
			post.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
            post.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));
            post.setHeader(new BasicHeader("User-Agent", Common.DWT_UserAgent));
            
            response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode){
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
    	        DWTResult p1 = GetResult(result);
    	        
    	        return p1;
            }
            response.close();
            client.close();
        } catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
        return null;
    }
    
    private DWTResult postFile(String url, byte[] binData)
    {
		int SUCCESS_CODE = 200;
    	
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        try{
            client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.addBinaryBody("f1", binData);
            
            post.setEntity(entityBuilder.build());
            
            post.setProtocolVersion(HttpVersion.HTTP_1_1);
            post.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));
            post.setHeader(new BasicHeader("User-Agent", Common.DWT_UserAgent));
            
            response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (SUCCESS_CODE == statusCode){
                String result = EntityUtils.toString(response.getEntity(),"UTF-8");
    	        DWTResult p1 = GetResult(result);
    	        
    	        return p1;
            }
            response.close();
            client.close();
        } catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
        return null;
    }
    
    public void dispose() {
    	this.wsClient.close();
    	this.wsClient = null;
		this.bDisposed = true;
		this.bReady = false;
    }

	@Override
	public void onWSOpen() {
        System.out.println("Client onOpen");

		if(this.isDisposed())
			return;
		
		this.bReady = true;
        DWTCmdCallback callback = new DWTCmdCallback();
        callback.cmdId = cmdId++;
        callback.callback = new ICmdCallback() {
			
			@Override
			public void sFun(List<String> ret) {
				
				bReady = true;
				DWTEventHandler dwtEventHandler = handlerMap.get(EventType.OnReady);
				
				if(null != dwtEventHandler)
				{
					dwtEventHandler.callback(ret);
				}
			}
			
			@Override
			public boolean fFun(String errString) {
				DLogger.GetLogger().LogError(errString);
				return false;
			}
		};
		this.curCommand.add(callback);
		
        
    	String activeUI = this.dataProvider.getData("ActiveUI", String.format("\"12.0.0\",%d,1", new Date().getTime()), callback.cmdId);

        System.out.println(activeUI);
   		this.wsClient.send(activeUI);
	}

	@Override
	public void onWSMessage(String rawData) {

        System.out.println("onMessage: " + rawData);

		if(this.isDisposed())
			return;
		
		
		String tmpData = rawData; //.replace('\0', ' ');

		if (tmpData.indexOf("Exception:") >= 0 || tmpData.indexOf("Error") == 0) {// not be here
			
			if(this.curCommand.size()>0) {
				DWTCmdCallback curCmd = this.curCommand.remove(0);
				if (curCmd.callback != null)
					curCmd.callback.fFun(rawData);
			}

			return;
		}

		DWTCmdCallback curCmd = null;
		DWTCmdResponse r = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
	        r = objectMapper.readValue(tmpData, DWTCmdResponse.class);

		} catch (Exception exp) {
			DLogger.GetLogger().LogError(exp.getMessage());
			return;
		}

		if (r.event != null) {
			this.handleEvent(this, r);
			return;
		}


		if (r.cmdId != null) {
			int _cmdcount = this.curCommand.size(), i, findIndex = -1;
			
			for (i = _cmdcount - 1; i >= 0; i--) {
				DWTCmdCallback cmd = this.curCommand.get(i);
				if (cmd.cmdId == r.cmdId) {
					findIndex = i;
					curCmd = this.curCommand.remove(findIndex);
					break;
				}
			}

			if (findIndex < 0) {
				return;
			}

		} else {
			if(this.curCommand.size()>0)
				curCmd = this.curCommand.remove(0);
		}

		if (r.exception != null && r.description != null) {
			this._errorCode = r.exception;
			this._errorString = r.description;
		} else {
			
			if (r.method == null) {
			 	if (curCmd.callback != null) {
			 		String msg = "";
			 		if(r.result.size()>1)
			 			msg = r.result.get(1);
			 		else if(r.result.size()>0)
			 			msg = r.result.get(0);
			 		
			 		curCmd.callback.fFun(msg);
			 	}
			 	return;
			}
			
			if (!r.method.equals("StartScan")) {
				this._errorCode = 0;
				this._errorString = "";
			}
		}

		if (r.method.equals("ReadBarcode") || r.method.equals("OCRRecognize")) {
		 	if (curCmd.callback != null)
		 		curCmd.callback.sFun(r.result);
		 	return;
		 } else if (r.method.equals("ActiveUI") 
				|| r.method.equals("VersionInfo") 
				|| r.method.equals("ConvertToBase64") 
				|| r.method.equals("SaveSelectedImagesToBase64Binary") 
				|| r.method.equals("ConvertToBlob")) {
			if (curCmd.callback != null) {
				curCmd.callback.sFun(r.result);
				return;
			}
		} else if (r.method.equals("ShowFileDialog") 
				|| r.method.equals("SplitTiff") 
				|| r.method.equals("SplitPDF") 
				|| r.method.equals("EncodeAsBase64")) {

			if (curCmd.callback != null) {
				curCmd.callback.sFun(r.result);
				return;
			}

		}

		if (r.result!=null && r.result.size()>0 && (r.result.get(0).equals("1") || r.result.get(0).equals("true"))) {

			if (curCmd.callback != null) {
				curCmd.callback.sFun(null);
			}
			
		} else {
			if (r.exception != null && r.exception != 0) {
				
				// Error
				if (curCmd.callback != null) {
					curCmd.callback.fFun(this._errorString);
				}

			} else {
				
				if (r.method.equals("LoadImage") || r.method.equals("LoadImageEx") || 
					r.method.equals("LoadImageFromBytes") || r.method.equals("LoadImageFromBase64Binary") || r.method.equals("LoadDibFromClipboard") || 
					r.method.equals("FTPDownload") || r.method.equals("FTPDownloadEx")) {

					if (curCmd.callback != null) {
						curCmd.callback.sFun(r.result);
					}
					
					return;
				}

				if (curCmd.callback != null) {
					curCmd.callback.fFun(this._errorString);
				}
			}
		}
	}
	
	@Override
	public void onWSClose() {

        System.out.println("onClose ...");

		this.bReady = false;
		if(this.isDisposed())
			return;
	}

	@Override
	public void onWSError(Exception error) {
        System.out.println("onError: " + error);

		this.bReady = false;
		if(this.isDisposed())
			return;
		
	}
    

    // type: 1-Append(after index), 2-Insert(before index), 3-Remove, 4-Edit(Replace), 5-Index Change
	private static interface OpType {
		String Append = "1";
		String Insert = "2";
		String Remove = "3";
		String Edit = "4";
		String IndexChange = "5";
	}

	private void handleEvent(DWTClient dwtClient, DWTCmdResponse r) {
		System.out.println("-->handleEvent: " + r.event);
		System.out.println("-->cmdId: " + r.cmdId);
		
		//   "result" : [ "1392", "46", 1, 46, 1, 850 ],
			
		if(r.event.toLowerCase().equals("onbitmapchanged")) {
			
			// _OpType
			if(r.result.get(2).equals(OpType.Append)) {
				System.out.println("-->handleEvent onbitmapchanged add");
				this.ids.add(r.result.get(1));
			}
			else if(r.result.get(2).equals(OpType.Remove)) {
				System.out.println("-->handleEvent onbitmapchanged remove");
				this.ids.remove(r.result.get(1));
			}
			else {
				System.out.println("-->handleEvent onbitmapchanged no actions");
			}
		} else if(r.event.toLowerCase().equals("onpostalltransfers")) {
			DWTEventHandler dwtEventHandler = handlerMap.get(EventType.OnPostAllTransfers);

			if(null != dwtEventHandler)
			{
				System.out.println("-->handleEvent onpostalltransfers callback");
				dwtEventHandler.callback(r.result);
			}
		} else {
			System.out.println("-->handleEvent no callback: " + r.event);
		}

		//if(this.isDisposed())
	}

	private static int cmdId = 1;
	public void Acquire(ICmdCallback callback) {

        System.out.println("AcquireImage");

		if(this.isDisposed())
			return;
		
        DWTCmdCallback cmdCallback = new DWTCmdCallback();
        cmdCallback.cmdId = cmdId++;
        cmdCallback.callback = callback;

        String cmd = this.dataProvider.getData("AcquireImage", "{\"EnableEvents\":0}", cmdCallback.cmdId);
        System.out.println(cmd);
        this.curCommand.add(cmdCallback);
        
        this.wsClient.sendBinary(cmd);
	}

	public void addEvent(EventType evtName, DWTEventHandler dwtEventHandler) {

		if(this.isDisposed())
			return;
		
		if(dwtEventHandler != null)
			handlerMap.put(evtName, dwtEventHandler);
	}

	public boolean hasBlocks() {
		return this.HowManyImagesInBuffer()>0;
	}

}