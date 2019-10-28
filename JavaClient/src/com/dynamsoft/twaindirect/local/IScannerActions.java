package com.dynamsoft.twaindirect.local;

import com.dynamsoft.could.entity.TCCmdInput;
import com.dynamsoft.could.entity.TCCmdResponse;


public interface IScannerActions {
	TCCmdResponse CloseSession(TCCmdInput cmdInput);
	TCCmdResponse GetSession(TCCmdInput cmdInput);
	TCCmdResponse StopCapturing(TCCmdInput cmdInput);
	TCCmdResponse StartCapturing(TCCmdInput cmdInput);

	TCCmdResponse CreateSession(TCCmdInput cmdInput);
	TCCmdResponse SendTask(TCCmdInput cmdInput);
	
	String getSessionId();
}
