package com.dynamsoft.could.entity;

import com.alibaba.fastjson.JSONObject;

public class TCLocalParameter {

	public String sessionId;
	public String sessionRevision;
	public Boolean withThumbnail;
	public Boolean withMetadata;
	public Integer imageBlockNum;
	public Integer lastImageBlockNum;

	// send Task params
	public JSONObject task;
}
