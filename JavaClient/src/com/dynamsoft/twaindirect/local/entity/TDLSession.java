package com.dynamsoft.twaindirect.local.entity;

public class TDLSession {
	public String sessionId;
	public int revision;
	public String state;
	
	public TDLSession()
	{
		gotoNoSession();
	}

	public TDLSession(TDLSession src)
	{
		CopyFrom(src);
	}
	
	public void gotoNoSession() {
		this.sessionId = "";
		this.state = "";
		this.revision = 0;
	}
	
	public void CopyFrom(TDLSession src)
	{
		this.sessionId = src.sessionId;
		if(this.sessionId.equals(""))
			this.state = "";
		else
			this.state = src.state;
		this.revision = src.revision;
	}
	
}
