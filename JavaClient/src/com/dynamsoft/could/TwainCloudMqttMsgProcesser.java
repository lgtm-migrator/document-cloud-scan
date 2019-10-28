package com.dynamsoft.could;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.alibaba.fastjson.JSON;
import com.dynamsoft.DLogger;
import com.dynamsoft.IDProvider;
import com.dynamsoft.could.entity.TCCmdEventsResponse;
import com.dynamsoft.could.entity.TCCmdInput;
import com.dynamsoft.could.entity.TCHeaders;
import com.dynamsoft.could.entity.TCSessionMessage;
import com.dynamsoft.could.json.JsonToEntity;
import com.dynamsoft.twaindirect.local.TDLSessionState;

public class TwainCloudMqttMsgProcesser implements IMqttMessageListener {

	
	private String sendTopic;
	private String scannerId;
	private TwainCloudMqttDevice mqtt;
	private TwainLocalHttpServer localHttpServer;
	private TwainCloudClient tcClient; // for upload blocks
	
	public TwainCloudMqttMsgProcesser(
			TwainCloudMqttDevice mqtt,
			TwainLocalHttpServer localHttpServer,
			String sendTopic, String scannerId,
			TwainCloudClient tcClient)
	{
		this.mqtt = mqtt;
		this.localHttpServer = localHttpServer;
		this.sendTopic = sendTopic;
		this.scannerId = scannerId;
		
		this.tcClient = tcClient;
	}
	
	@Override
	public void messageArrived(String _arrivedTopic, MqttMessage _arrivedMsg) throws Exception {
		
		String jsonMsg = new String(_arrivedMsg.getPayload());
		if(DLogger.debug)
		{
            DLogger.println("recieve message:");
			DLogger.println("    ---->" + jsonMsg);
		}
		
		//
		String localToken = this.localHttpServer.getPrivetToken();
		TCSessionMessage msg = JsonToEntity.getParser().parseSessionMsg(jsonMsg);

		if(msg.url.equals("https://twaincloud.dynamsoft.com/" + this.scannerId) 
				|| msg.url.equals("https://cloud.dynamsoft.com/" + this.scannerId)) {
			// ok
			TCHeaders headers = new TCHeaders(msg.headers);
			String token = headers.get("x-privet-token");

			if(DLogger.debug)
			{
	            DLogger.print("get x-privet-token: ");
				DLogger.println(token);
			}
			
			if(token != null && token.equals("\"\"")) {

				// request Token
				MqttMessage newMsg = new MqttMessage();
				

            	String curSessionId = null;
            	if(null != localHttpServer)
            		curSessionId = localHttpServer.getSession().sessionId;
            	
				String returnInfoEx = String.format("{\"token\":\"%s\",\"sessionid\":\"%s\"}", localToken, curSessionId);
				
				// send back
				newMsg.setPayload(returnInfoEx.getBytes());
				
				this.mqtt.send(this.sendTopic, newMsg);
				return;
				
			} else if(null != token && null != localToken && token.equals(localToken)) {
				
				if(IDProvider.getInstance().checkToken(token)) {
					
					TCCmdInput cmdInput = JsonToEntity.getParser().parseSessionCommand(msg.body);
					if(cmdInput.kind.equals("twainlocalscanner")) {

						if(cmdInput.method.equals("waitForEvents")) {

							new Thread(new Runnable() {
					            public void run() {
					            	
					            	while(localHttpServer.getSessionState() == TDLSessionState.capturing) {
					            		try {
					            			if(localHttpServer.isCaptured())
					            				break;
					            			
											Thread.sleep(300);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
					            	}
					            	
									TCCmdEventsResponse responseEvt = localHttpServer.cmdWaitForEvents(cmdInput);
									String _ret = JSON.toJSONString(responseEvt);
									MqttMessage _newMsg = new MqttMessage();
									_newMsg.setPayload(_ret.getBytes());
									mqtt.send(sendTopic, _newMsg);
					            }
					        }).start();
							
							return;
						}
						else {

							String response = null;
							//	case "releaseImageBlocks":
							if (cmdInput.method.equals("readImageBlock")
									|| cmdInput.method.equals("readImageBlockMetadata")) {

								Integer imageBlockNum = cmdInput.params.imageBlockNum;
								
								String imageBlockUrl = null;
								byte[] aryPDF = null;
								if(imageBlockNum == null) {
									aryPDF = this.localHttpServer.GetImagesAsPDF();
								} else {
									// imageBlockNum is start from 1
									aryPDF = this.localHttpServer.GetImageAsPDF(imageBlockNum-1);
								}
								
								if(null != aryPDF) {
									// upload
									System.out.print("start upload block to server (length): " + aryPDF.length);
									imageBlockUrl = this.tcClient.uploadImageBlock(this.scannerId, aryPDF);
								} 
								
								System.out.print("-> upload block to server: " + imageBlockUrl);
								
								response = this.localHttpServer.ProcessBlock(cmdInput, imageBlockUrl);
								
							} else {
								response = this.localHttpServer.ProcessCmd(cmdInput);
							}
							
							MqttMessage newMsg = new MqttMessage();
							newMsg.setPayload(response.getBytes());
							this.mqtt.send(this.sendTopic, newMsg);
							return;
						}
					}
				}
			}
		}
		
		
		// default send back: ERROR
		MqttMessage newMsg = new MqttMessage();
		newMsg.setPayload(("{\"test\":0}").getBytes());
		this.mqtt.send(this.sendTopic, newMsg);
	}
}
