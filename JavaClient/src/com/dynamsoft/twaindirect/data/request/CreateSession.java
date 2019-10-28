package com.dynamsoft.twaindirect.data.request;

import com.dynamsoft.twaindirect.local.entity.BaseSession;
import com.dynamsoft.twaindirect.local.entity.Const;

public class CreateSession extends BaseSession {
	public CreateSession(String commandId) {
		super(commandId, Const.method.createSession);
	}
}
