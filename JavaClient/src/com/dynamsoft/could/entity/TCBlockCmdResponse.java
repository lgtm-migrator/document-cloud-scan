package com.dynamsoft.could.entity;

public class TCBlockCmdResponse {

	public TCBlockCmdResponse() {
		this.kind = null;
		this.commandId = null;
		this.method = null;
		this.results = new TCBlockCmdResResults();
	}
	
	public TCBlockCmdResponse(TCCmdInput cmdInput) {
		this.kind = cmdInput.kind;
		this.commandId = cmdInput.commandId;
		this.method = cmdInput.method;
		this.results = new TCBlockCmdResResults();
	}
	
	public String kind;
	public String commandId;
	public String method;
	public TCBlockCmdResResults results;
}
