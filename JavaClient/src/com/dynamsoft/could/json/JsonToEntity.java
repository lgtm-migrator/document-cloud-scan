package com.dynamsoft.could.json;

import com.alibaba.fastjson.JSONObject;
import com.dynamsoft.DLogger;
import com.dynamsoft.could.entity.*;
import com.dynamsoft.twaindirect.local.entity.TLTask;

public class JsonToEntity {

	private static JsonToEntity objJsonToEntity;
	public static JsonToEntity getParser()
	{
		if(null == objJsonToEntity)
		{
			objJsonToEntity = new JsonToEntity();
		}
		return objJsonToEntity;
	}
	
	private JsonToEntity()
	{}
	
	private boolean isJsonNull(String json)
	{
		return null == json || json.isEmpty() || json.equals("null");
	}
	
	public TCTokens parseTokens(String json)
	{
		if(DLogger.debug) {
			DLogger.print("Get Tokens Response: "); DLogger.println(json);
		}
		
		if(this.isJsonNull(json)) {
			return null;
		}
		
		JSONObject object = JSONObject.parseObject(json);

		TCTokens tokens = new TCTokens();
		tokens.token = object.getString("token");
		tokens.refreshToken = object.getString("refreshToken");
		return tokens;
	}
	

	public TCSessionMessage parseSessionMsg(String json)
	{
		if(DLogger.debug) {
			DLogger.print("Get Session Message: "); DLogger.println(json);
		}
		
		if(this.isJsonNull(json)) {
			return null;
		}
		
		JSONObject object = JSONObject.parseObject(json);

		TCSessionMessage msg = new TCSessionMessage();
		msg.url = object.getString("url");
		msg.headers = object.getString("headers");
		msg.body = object.getString("body");
		return msg;
	}
	
	public TCCmdInput parseSessionCommand(String json)
	{

		if(DLogger.debug) {
			DLogger.print("Get Session Message: "); DLogger.println(json);
		}
		
		if(this.isJsonNull(json)) {
			return null;
		}
		
		JSONObject object = JSONObject.parseObject(json);

		TCCmdInput msg = new TCCmdInput();
		msg.kind = object.getString("kind");
		msg.commandId = object.getString("commandId");
		msg.method = object.getString("method");
		
		try {
			msg.params = object.getObject("params", TCLocalParameter.class);
		}catch(Exception e1) {
			e1.printStackTrace();
		}
		return msg;
	}
	
	public TCScannerInfo parseScannerInfo(String json, TCTokens tokens)
	{
		if(DLogger.debug) {
			DLogger.print("Scanner Info Response: "); DLogger.println(json);
		}

		if(this.isJsonNull(json)) {
			return null;
		}
		
		JSONObject object = JSONObject.parseObject(json);
		
		TCScannerInfo scannerInfo = new TCScannerInfo();
		
		scannerInfo.id					=object.getString("id");
		scannerInfo.model               =object.getString("model");
		scannerInfo.support_url         =object.getString("support_url");
		scannerInfo.version             =object.getString("version");
		scannerInfo.setup_url           =object.getString("setup_url");
		scannerInfo.uptime              =object.getString("uptime");
		scannerInfo.name                =object.getString("name");
		scannerInfo.clientId            =object.getString("clientId");
		scannerInfo.semantic_state      =object.getString("semantic_state");
		scannerInfo.serial_number       =object.getString("serial_number");
		scannerInfo.manufacturer        =object.getString("manufacturer");
		scannerInfo.firmware            =object.getString("firmware");
		scannerInfo.connection_state    =object.getString("connection_state");
		scannerInfo.device_state        =object.getString("device_state");
		scannerInfo.description         =object.getString("description");
		scannerInfo.update_url          =object.getString("update_url");
		scannerInfo.type                =object.getString("type");
		
		tokens.token = object.getString("token");
		tokens.refreshToken = object.getString("refreshToken");
		

		if(DLogger.debug) {

			DLogger.print("id:");                DLogger.println(scannerInfo.id);
			DLogger.print("model:");             DLogger.println(scannerInfo.model);
			DLogger.print("support_url:");       DLogger.println(scannerInfo.support_url);
			DLogger.print("version:");           DLogger.println(scannerInfo.version);
			DLogger.print("setup_url:");         DLogger.println(scannerInfo.setup_url);
			DLogger.print("uptime:");            DLogger.println(scannerInfo.uptime);
			DLogger.print("name:");              DLogger.println(scannerInfo.name);
			DLogger.print("clientId:");          DLogger.println(scannerInfo.clientId);
			DLogger.print("semantic_state:");    DLogger.println(scannerInfo.semantic_state);
			DLogger.print("serial_number:");     DLogger.println(scannerInfo.serial_number);
			DLogger.print("manufacturer:");      DLogger.println(scannerInfo.manufacturer);
			DLogger.print("firmware:");          DLogger.println(scannerInfo.firmware);
			DLogger.print("connection_state:");  DLogger.println(scannerInfo.connection_state);
			DLogger.print("device_state:");      DLogger.println(scannerInfo.device_state);
			DLogger.print("description:");       DLogger.println(scannerInfo.description);
			DLogger.print("update_url:");        DLogger.println(scannerInfo.update_url);
			DLogger.print("type:");              DLogger.println(scannerInfo.type);

			DLogger.print("token:");             DLogger.println(tokens.token);
			DLogger.print("refreshToken:");      DLogger.println(tokens.refreshToken);
			
		}
		return scannerInfo;
	}
	
	public TCScannerRegister parseRegister(String json)
	{
		if(DLogger.debug) {
			DLogger.print("Scanner Register Response: "); DLogger.println(json);
		}

		if(this.isJsonNull(json)) {
			return null;
		}
		
		TCScannerRegister register = new TCScannerRegister();

		JSONObject object = JSONObject.parseObject(json);

		register.scannerId		= object.getString("scannerId");
		register.pollingUrl     = object.getString("pollingUrl");
		register.inviteUrl      = object.getString("inviteUrl");
		
		return register;
	}

	public TCUserInfo parseUserInfo(String json) {


		if(DLogger.debug) {
			DLogger.print("UserInfo Response: "); DLogger.println(json);
		}

		if(this.isJsonNull(json)) {
			return null;
		}
		
		JSONObject object = JSONObject.parseObject(json);

		TCUserInfo userInfo = new TCUserInfo();
		userInfo.url		= object.getString("url");
		userInfo.type     = object.getString("type");
		userInfo.topic      = object.getString("topic");
		
		return userInfo;
	}
}
