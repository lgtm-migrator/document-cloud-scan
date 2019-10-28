package com.dynamsoft.twaindirect.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Results {
	
	public Integer characterOffset;
	public String jsonKey;
	public String reason;
	public Boolean success;
	public Integer timeRemaining;
	public String code;
	public List<Event> events;
	public Session session;
	public Metadata metadata;
}
