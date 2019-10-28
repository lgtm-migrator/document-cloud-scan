package com.dynamsoft.twaindirect.local.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {
	public static String getString(JsonNode node, String fieldName, String defValue) {
		return node.has(fieldName) ? node.get(fieldName).asText() : defValue;
	}

	public static boolean getBoolean(JsonNode node, String fieldName, boolean defValue) {
		return node.has(fieldName) ? node.get(fieldName).asBoolean() : defValue;
	}

	public static int getInt(JsonNode node, String fieldName, int defValue) {
		return node.has(fieldName) ? node.get(fieldName).asInt() : defValue;
	}

	public static String writeValueAsString(Object value, boolean boINDENT_OUTPUT) {
		ObjectMapper mapper = new ObjectMapper();
		if (boINDENT_OUTPUT) {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
		}
		String str = "";
		try {
			str = mapper.writeValueAsString(value);
		} catch (JsonProcessingException jsonProcessingException) {
		}

		return str;
	}
}
