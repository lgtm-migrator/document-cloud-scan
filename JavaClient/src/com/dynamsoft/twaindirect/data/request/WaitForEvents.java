package com.dynamsoft.twaindirect.data.request;

import com.dynamsoft.twaindirect.local.entity.BaseParams;
import com.dynamsoft.twaindirect.local.entity.BaseSession;
import com.dynamsoft.twaindirect.local.entity.Const;

public class WaitForEvents extends BaseSession {
	public Params params;

	public static class Params extends BaseParams {
		public Integer sessionRevision;
	}

	public WaitForEvents(String commandId, String sessionId) {
		super(commandId, Const.method.waitForEvents);
		this.params = new Params();
		this.params.sessionId = sessionId;
	}
}
