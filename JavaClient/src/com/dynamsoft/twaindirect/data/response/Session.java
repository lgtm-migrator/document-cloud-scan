package com.dynamsoft.twaindirect.data.response;

import com.dynamsoft.twaindirect.local.entity.TLTask;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Session {
	
	public Boolean doneCapturing;
	public List<Integer> imageBlocks;
	public Boolean imageBlocksDrained;
	public Integer revision;
	public String sessionId;
	public Status status;
	public String state;
	public TLTask task;
}
