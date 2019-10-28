package com.dynamsoft.twaindirect.data.request;

import com.dynamsoft.twaindirect.local.entity.BaseParams;
import com.dynamsoft.twaindirect.local.entity.BaseSession;
import com.dynamsoft.twaindirect.local.entity.Const;

public class GetSession extends BaseSession {
	public Params params;

	public static class Params extends BaseParams {
	}

	public GetSession(String commandId, String sessionId) {
		super(commandId, Const.method.getSession);
		this.params = new Params();
		this.params.sessionId = sessionId;
	}
}
