package com.dynamsoft.could;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.dynamsoft.DLogger;
import com.dynamsoft.IDProvider;
import com.dynamsoft.could.entity.TCBlockCmdResponse;
import com.dynamsoft.could.entity.TCCmdInput;
import com.dynamsoft.could.entity.TCCmdResponse;
import com.dynamsoft.dwt.DWTClient;
import com.dynamsoft.twaindirect.local.TDLDoActions;

public class TwainLocalHttpServer extends TDLDoActions {

	private String localToken;
	private boolean blRunning;

	public String GetStatus(TCCmdResponse response) {
		if(null != response)
			return response.results.session.state;
		else
			return "";
	}

	public boolean isRunning() {
		return blRunning;
	}
	
	public TwainLocalHttpServer(DWTClient dwtClient, String scanner)
	{
		super(dwtClient, scanner);
		this.localToken = null;
		this.blRunning = true;
	}
	
	public String getPrivetToken() {

		if(null == this.localToken) {
			// new token
			this.localToken = IDProvider.getInstance().createXPrivetToken(new Date().getTime());
		}
		
		return this.localToken;
	}
	
    public String ProcessCmd(TCCmdInput cmdInput)
    {
		TCCmdResponse response = null;
		boolean bNeedAppendDefaultReturn = false;
		
		try {
		if(cmdInput.kind.equals("twainlocalscanner")) {
			switch(cmdInput.method) {
			case "createSession": 
				this.setCaptured(false);
				response = this.cmdCreateSession(cmdInput);
				break;

			case "closeSession": {
				this.setCaptured(false);
				response = this.cmdCloseSession(cmdInput);
				bNeedAppendDefaultReturn = true;
			}
				break;


			case "getSession": {
				response = this.cmdGetSession(cmdInput);
				bNeedAppendDefaultReturn = true;
			}
				break;

			case "startCapturing": {
				this.setCaptured(false);
				response = this.cmdStartCapturing(cmdInput);
				bNeedAppendDefaultReturn = true;
			}
				break;

			case "stopCapturing": {
				this.setCaptured(false);
				response = this.cmdStopCapturing(cmdInput);
				bNeedAppendDefaultReturn = true;
			}
				break;

			case "sendTask": {
				response = this.cmdSendTask(cmdInput); // refblSetAppCapabilities
			}
				break;
				
			// ==> process waitForEvents out of this function
			//case "waitForEvents": {
			//	TCCmdEventsResponse responseEvt = this.cmdWaitForEvents(cmdInput);
			//	return JSON.toJSONString(responseEvt);
			//}
				 
			case "exit":
				blRunning = false;
				break;

			// Stuff we don't recognize. Some commands never make it this
			// far...
			case "readImageBlock":
				response = this.readImageBlock(cmdInput);
			case "readImageBlockMetadata":
				response = this.readImageBlockMetadata(cmdInput);
			case "releaseImageBlocks":
				this.setCaptured(false);
				response = this.releaseImageBlocks(cmdInput);
			default:
				break;
			}		
		}
		}catch(Exception e) {

			response = new TCCmdResponse(cmdInput);
			super.setResponse(false, response);
			
		}

		return JSON.toJSONString(response);
    }


	public byte[] GetImagesAsPDF() {

		int imageCount = this.dwtClient.HowManyImagesInBuffer();
		if(imageCount>0) {
			return this.dwtClient.GetAllImagesAsPDF();
		}
		
		return null;
	}

	public byte[] GetImageAsPDF(int index) {

		int imageCount = this.dwtClient.HowManyImagesInBuffer();
		if(imageCount>0 && index<imageCount) {
			return this.dwtClient.GetImageAsPDF(index);
		}
		
		return null;
	}
	
	public String ProcessBlock(TCCmdInput cmdInput, String imageBlockUrl) {

		boolean bSuscess = (null != imageBlockUrl);
		TCBlockCmdResponse response = new TCBlockCmdResponse(cmdInput);
		DLogger.GetLogger().LogInfo("readImageBlock");
		
		setResponse(bSuscess, response);
		if(bSuscess) {
			response.results.imageBlockUrl = imageBlockUrl;
		}
		
		return JSON.toJSONString(response);
	}
    
}
