package com.dynamsoft.could;

import com.dynamsoft.DLogger;
import com.dynamsoft.could.entity.TCTokens;
import com.dynamsoft.could.json.JsonToEntity;

public class TwainCloudLoginClient extends TwainCloudClientBase {

	public TwainCloudLoginClient(String httpServerUrl, ICloudCallback cloudCallback) {
		super(httpServerUrl, cloudCallback);
	}

	public TCTokens login(String user, String password)
	{
		String strPostData = String.format("name=%s&password=%s", user, password); 
		String jsonTokens = httpPost(urlProvider.userInfo(), strPostData);
		
		if(null == jsonTokens) {
			return null;
		}
		
		TCTokens tokens = JsonToEntity.getParser().parseTokens(jsonTokens);

		if(DLogger.debug) {
			DLogger.println("jsonTokens is:" + jsonTokens);
			if(null != tokens) {
				DLogger.println(tokens.token);
				DLogger.println(tokens.refreshToken);
			}
		}
		return tokens;
	}

}
