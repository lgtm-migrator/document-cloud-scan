package com.dynamsoft.twaindirect.local.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseSession {
	
	public String commandId;
	public String kind = "twainlocalscanner";
	public String method;

	public BaseSession(String commandId, String method) {
		this.commandId = commandId;
		this.method = method;
	}
	
	public String toString() {
		return toString(false);
	}

	public String toString(boolean boINDENT_OUTPUT) {
		ObjectMapper mapper = new ObjectMapper();
		if (boINDENT_OUTPUT) {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
		}
		String str = "";
		try {
			str = mapper.writeValueAsString(this);
		} catch (JsonProcessingException jsonProcessingException) {
		}

		return str;
	}
}
