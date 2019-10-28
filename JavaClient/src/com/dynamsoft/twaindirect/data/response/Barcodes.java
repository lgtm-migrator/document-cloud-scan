package com.dynamsoft.twaindirect.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Barcodes {
	
	public String base64Data;
	public Integer pixelOffsetX;
	public Integer pixelOffsetY;
	public String type;
	
}
