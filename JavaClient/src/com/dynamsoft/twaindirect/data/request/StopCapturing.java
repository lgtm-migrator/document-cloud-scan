package com.dynamsoft.twaindirect.data.request;

import com.dynamsoft.twaindirect.local.entity.BaseParams;
import com.dynamsoft.twaindirect.local.entity.BaseSession;
import com.dynamsoft.twaindirect.local.entity.Const;

public class StopCapturing extends BaseSession {
	public Params params;

	public static class Params extends BaseParams {
	}

	public StopCapturing(String commandId, String sessionId) {
		super(commandId, Const.method.stopCapturing);
		this.params = new Params();
		this.params.sessionId = sessionId;
	}
}
