package com.dynamsoft.twaindirect.local;

import com.dynamsoft.IDProvider;

public class TDLResponse {

	public String GetJson(String cmdId, String state) {

		String sessionId = String.valueOf(IDProvider.getInstance().getRamdomIDAsLong());
		String revision = String.valueOf(1);
		
		String msg = String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"createSession\",\"results\":{\"success\":true,\"events\":[{\"session\":{\"sessionId\":\"%s\",\"revision\": %s,\"state\":\"%s\"}}}}} ",
				cmdId,
				sessionId,
				revision,
				state);

		return msg;
	}

}
