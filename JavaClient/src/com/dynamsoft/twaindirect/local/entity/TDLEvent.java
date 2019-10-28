package com.dynamsoft.twaindirect.local.entity;

public class TDLEvent {

	public String event;
	public TDLEventSession session;
	
	public TDLEvent(TDLEventSession src)
	{
		CopyFrom(src);
	}
	
	public void CopyFrom(TDLEventSession src)
	{
		this.session = new TDLEventSession(src);
	}
}
