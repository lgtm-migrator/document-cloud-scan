package com.dynamsoft.twaindirect.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
	
	public Address address;
	public Image image;
	public Status status;
	public Barcodes barcodes;
	public Micr micr;
	public PatchCode patchCode;
	public List<Vendor> vendors;
}
