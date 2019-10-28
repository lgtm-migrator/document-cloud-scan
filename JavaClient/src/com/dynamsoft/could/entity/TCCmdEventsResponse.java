package com.dynamsoft.could.entity;

public class TCCmdEventsResponse {

	public TCCmdEventsResponse() {
		this.kind = null;
		this.commandId = null;
		this.method = null;
		this.results = new TCCmdEventResResults();
	}
	
	public TCCmdEventsResponse(TCCmdInput cmdInput) {
		this.kind = cmdInput.kind;
		this.commandId = cmdInput.commandId;
		this.method = cmdInput.method;
		this.results = new TCCmdEventResResults();
	}
	
	public String kind;
	public String commandId;
	public String method;
	public TCCmdEventResResults results;
}
