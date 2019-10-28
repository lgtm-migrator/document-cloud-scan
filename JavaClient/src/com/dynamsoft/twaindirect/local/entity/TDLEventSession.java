package com.dynamsoft.twaindirect.local.entity;

import java.util.ArrayList;
import java.util.List;

public class TDLEventSession {

	public String sessionId;
	public int revision;
	public String state;
	public List<Integer> imageBlocks;
	
	public TDLEventSession()
	{
		gotoNoSession();
	}

	public TDLEventSession(TDLSession src)
	{
		CopyFromTDLSession(src);
	}
	
	public TDLEventSession(TDLEventSession src)
	{
		CopyFrom(src);
	}
	
	public void gotoNoSession() {
		this.sessionId = "";
		this.state = "";
		this.revision = 0;
		imageBlocks = new ArrayList<Integer>();
	}
	
	public void CopyFromTDLSession(TDLSession src)
	{
		this.sessionId = src.sessionId;
		this.state = src.state;
		this.revision = src.revision;
		imageBlocks = new ArrayList<Integer>();
	}

	public void CopyFrom(TDLEventSession src)
	{
		this.sessionId = src.sessionId;
		this.state = src.state;
		this.revision = src.revision;

		this.imageBlocks = new ArrayList<Integer>();
		if(null != src.imageBlocks) {
			this.imageBlocks.addAll(src.imageBlocks);
		}
	}
}
