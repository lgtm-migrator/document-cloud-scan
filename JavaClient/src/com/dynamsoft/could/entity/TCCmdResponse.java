package com.dynamsoft.could.entity;

public class TCCmdResponse {

	public TCCmdResponse() {
		this.kind = null;
		this.commandId = null;
		this.method = null;
		this.results = new TCCmdResResults();
	}
	
	public TCCmdResponse(TCCmdInput cmdInput) {
		this.kind = cmdInput.kind;
		this.commandId = cmdInput.commandId;
		this.method = cmdInput.method;
		this.results = new TCCmdResResults();
	}
	
	public String kind;
	public String commandId;
	public String method;
	public TCCmdResResults results;
}
