package com.dynamsoft.twaindirect.local.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseTask {
	
	public String name;
	public String comment;
	public String exception;
	public String vendor;

}
