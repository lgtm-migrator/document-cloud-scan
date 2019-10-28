package com.dynamsoft.could.json;


public class EntityToJson {
	private static EntityToJson objEntityToJson;
	public static EntityToJson getInstance()
	{
		if(null == objEntityToJson)
		{
			objEntityToJson = new EntityToJson();
		}
		return objEntityToJson;
	}
	
	private EntityToJson()
	{}

}
