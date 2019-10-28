package com.dynamsoft.twaindirect.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Image {
	
	public String compression;
	public String imageMerged;
	public String pixelFormat;
	public Integer pixelHeight;
	public Integer pixelOffsetX;
	public Integer pixelOffsetY;
	public Integer pixelWidth;
	public Integer resolution;
	public Integer size;
	
}