package com.dynamsoft.could.entity;

import java.util.HashMap;

import com.dynamsoft.DLogger;

public class TCHeaders {

	private HashMap<String,String> map;
	
	public TCHeaders(String headers)
	{
		map = new HashMap<String, String>();
		if(headers != null) {

			String[] ary = headers.split(";");
			for(String s : ary)
			{
				int first = s.indexOf(':');
				if(first >= 0) {
					String k = s.substring(0, first);
					String v = s.substring(first+1);
					
					if(DLogger.debug)
					{
			            DLogger.println("put key/value into map:");
						DLogger.print(k);
						DLogger.print(" / ");
						DLogger.println(v);
					}
					
					map.put(k.toLowerCase(),v);
				}
			}
		}
	}
	
	public String get(String key)
	{
		return map.get(key);
	}
}
