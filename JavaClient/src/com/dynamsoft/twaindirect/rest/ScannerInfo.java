package com.dynamsoft.twaindirect.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.jmdns.ServiceInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScannerInfo {
	public String name;
	public int port;
	public String type;
	public String subType;
	public String[] hostAddress;
	public Boolean https;

	public ScannerInfo() {
	}

	public ScannerInfo(ServiceInfo info) {
		this.name = info.getName();
		this.port = info.getPort();
		this.type = info.getType();
		this.subType = info.getSubtype();
		this.hostAddress = info.getHostAddresses();
		this.https = Boolean.valueOf("1".equals(info.getPropertyString("https")));
	}
}
