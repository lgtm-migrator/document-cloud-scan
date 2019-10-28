package com.dynamsoft.dwt;

import com.dynamsoft.IDProvider;

public class DWTDataProvider {
	
	private String id;
	
	
	public DWTDataProvider(long id)
	{
		this.id = String.valueOf(id);
	}
	
	public String getClientId() {
		return id;
	}

	public String getData(String strMethod, String params) {

    	long cmdId = IDProvider.getInstance().getRamdomIDAsLong();
    	return getData(strMethod, params, cmdId);
    }

    public String getData (String strMethod, String params, long a_cmdId) {

    	long cmdId = a_cmdId;
    	
		StringBuilder cmd = new StringBuilder();
		
		cmd.append("{");

		// id
		cmd.append("\"id\":\"");
		cmd.append(id);
		cmd.append("\"");

		if (cmdId > 0) {
	    	if(cmdId <= 0)
	    		cmdId = IDProvider.getInstance().getRamdomIDAsLong();
	    	
			cmd.append(",\"cmdId\":\"");
			cmd.append(String.valueOf(cmdId));
			cmd.append("\"");
		}

		// method
		cmd.append(",\"method\":\"");
		cmd.append(strMethod);
		cmd.append("\"");

		cmd.append(",\"module\":\"dwt\",\"version\":\"");
		cmd.append(Common.DWT_Version);
		cmd.append("\"");

		if (params != null) {
			cmd.append(",\"parameter\":[");
			cmd.append(params);
			cmd.append("]");
		}

		cmd.append("}");

		return cmd.toString();
	}
}
