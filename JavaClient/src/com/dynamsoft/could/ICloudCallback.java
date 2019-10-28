package com.dynamsoft.could;

public interface ICloudCallback {
	void onSuccess(Object obj);
	void onError(String err);
}
