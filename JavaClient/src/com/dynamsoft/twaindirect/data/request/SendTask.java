package com.dynamsoft.twaindirect.data.request;

import com.dynamsoft.twaindirect.local.entity.BaseParams;
import com.dynamsoft.twaindirect.local.entity.BaseSession;
import com.dynamsoft.twaindirect.local.entity.Const;
import com.dynamsoft.twaindirect.local.entity.TLTask;

public class SendTask extends BaseSession {
	public Params params;

	public static class Params extends BaseParams {
		public TLTask task;
	}

	public SendTask(String commandId, String sessionId) {
		super(commandId, Const.method.sendTask);
		this.params = new Params();
		this.params.sessionId = sessionId;
	}
}
