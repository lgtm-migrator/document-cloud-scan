package com.dynamsoft.dwt;

public interface WSHandler {

	void onWSOpen();
	void onWSMessage(String msg);
	void onWSClose();
	void onWSError(Exception error);
	
}
