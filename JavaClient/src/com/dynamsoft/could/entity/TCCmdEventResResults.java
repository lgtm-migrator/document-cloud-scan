package com.dynamsoft.could.entity;

import java.util.ArrayList;
import java.util.List;

import com.dynamsoft.twaindirect.local.entity.TDLEvent;

public class TCCmdEventResResults {
	public boolean success;
	public List<TDLEvent> events;
	
	public TCCmdEventResResults() {
		this.events = new ArrayList<TDLEvent>();
	}
}

