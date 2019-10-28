package com.dynamsoft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IniConfig {
	private static Logger logger = LoggerFactory.getLogger(IniConfig.class.getName());
	private static Map<String, Map<String, String>> iniMap = new HashMap<String, Map<String, String>>();
	
	private IniConfig() {
		load();
	}
	private static IniConfig _instance = null;
	public static IniConfig getInstance()
	{
		if(null == _instance)
			_instance = new IniConfig();
		return _instance;
	}

	public Set<String> getSections() {
		return iniMap.keySet();
	}

	public Map<String, String> getSection(String section) {
		return iniMap.get(section);
	}

	public String getString(String section, String key) {
		return getString(section, key, "");
	}

	public String getString(String section, String key, String defValue) {
		Map<String, String> keyValue = getSection(section);
		String value = null;
		if (keyValue != null) {
			value = (String) keyValue.get(key);
		}
		return (value == null) ? defValue : value;
	}

	public int getInt(String section, String key, int defValue) {
		String strValue = getString(section, key, Integer.toString(defValue));
		int nValue = defValue;
		try {
			nValue = Integer.parseInt(strValue);
		} catch (NumberFormatException numberFormatException) {
		}

		return nValue;
	}

	public long getLong(String section, String key, long defValue) {
		String strValue = getString(section, key, Long.toString(defValue));
		long nValue = defValue;
		try {
			nValue = Long.parseLong(strValue);
		} catch (NumberFormatException numberFormatException) {
		}

		return nValue;
	}

	private void load() {
		InputStreamReader stream;
		String confFilePath = "conf/config.ini";
		String encoding = "UTF-8";

		File file = new File("./" + confFilePath);

		try {
			if (file.exists()) {
				stream = new InputStreamReader(new FileInputStream(file), encoding);
			} else {

				stream = new InputStreamReader(ClassLoader.getSystemResourceAsStream(confFilePath),
						Charset.forName(encoding));
			}
		} catch (FileNotFoundException | java.io.UnsupportedEncodingException e) {
			logger.error("fail open file " + confFilePath, e);
			return;
		}
		INIConfiguration configRoot = new INIConfiguration();
		try {
			configRoot.read(new BufferedReader(stream));
		} catch (ConfigurationException | IOException e) {
			logger.error("fail read file", e);

			return;
		}
		
		for (String section : configRoot.getSections()) {
			iniMap.put(section, new HashMap<String, String>());
			SubnodeConfiguration subnodeConfiguration = configRoot.getSection(section);

			Iterator<String> atriKeys = subnodeConfiguration.getKeys();

			while (atriKeys.hasNext()) {
				String key = (String) atriKeys.next();
				String key2 = key.replaceAll("\\.\\.", "\\.");

				(iniMap.get(section)).put(key2, subnodeConfiguration.getString(key));
			}
		}
	}
}
