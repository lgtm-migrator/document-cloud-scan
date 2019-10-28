package com.dynamsoft.could.entity;

import com.dynamsoft.twaindirect.local.entity.TDLSession;

public class TCCmdResResults {
	public boolean success;
	public TDLSession session;
	
	public TCCmdResResults() {
		this.session = new TDLSession();
	}
}
