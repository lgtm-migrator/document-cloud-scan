package com.dynamsoft.twaindirect.data.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
	public Integer imageNumber = Integer.valueOf(1);
	public Integer imagePart = Integer.valueOf(1);
	public String moreParts = "lastPartInFile";
	public String pixelFormatName;
	public Integer sheetNumber = Integer.valueOf(1);
	public String source = "feederFront";

	public String sourceName;

	public String streamName;

	@JsonIgnore
	public boolean isLastPart() {
		return (this.moreParts != null && this.moreParts.startsWith("lastPartInFile"));
	}

	@JsonIgnore
	public String getSaveName() {
		String men = "feederRear".equals(this.source) ? "R" : "F";
		return String.format("%04d-%04d-%02d-%s.pdf",
				new Object[] { this.sheetNumber, this.imageNumber, this.imagePart, men });
	}
}
