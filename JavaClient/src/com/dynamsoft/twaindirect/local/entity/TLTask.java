package com.dynamsoft.twaindirect.local.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TLTask extends BaseTask {
	public List<TLAction> actions;
	private static Logger logger = LoggerFactory.getLogger(TLTask.class.getName());

	public static class value extends BaseTask {
		public value(Object value) {
			this.value = value;
		}

		public Object value;

		public value() {
		}
	}

	public static class TLAttribute extends BaseTask {
		public String attribute;
		public List<TLTask.value> values;

		public TLAttribute() {
		}

		public TLAttribute(String attribute) {
			this.attribute = attribute;
			this.values = new ArrayList<TLTask.value>();
		}
	}

	public static class TLPixelFormat extends BaseTask {
		public String pixelFormat;
		public List<TLTask.TLAttribute> attributes;

		public TLPixelFormat() {
		}

		public TLPixelFormat(String pixelFormat) {
			this.pixelFormat = pixelFormat;
			this.attributes = new ArrayList<TLTask.TLAttribute>();
		}
	}

	public static class TLSource extends BaseTask {
		public String source;
		public List<TLTask.TLPixelFormat> pixelFormats;

		public TLSource() {
		}

		public TLSource(String source) {
			this.source = source;
			this.pixelFormats = new ArrayList<TLTask.TLPixelFormat>();
		}
	}

	public static class TLStream extends BaseTask {
		public List<TLTask.TLSource> sources;

		public TLStream() {
		}

		public TLStream(String name) {
			this.name = name;
			this.sources = new ArrayList<TLTask.TLSource>();
		}
	}

	public static class TLAction extends BaseTask {
		public String action;
		public List<TLTask.TLStream> streams;

		public TLAction() {
		}

		public TLAction(String action) {
			this.action = action;
			this.streams = new ArrayList<TLTask.TLStream>();
		}
	}

	public void setActionName(String action) {
		((TLAction) this.actions.get(0)).action = action;
	}

	@JsonIgnore
	public String getActionName() {
		return ((TLAction) this.actions.get(0)).action;
	}

	public void setSourceName(String source) {
		((TLSource) ((TLStream) ((TLAction) this.actions.get(0)).streams.get(0)).sources.get(0)).source = source;
	}

	@JsonIgnore
	public String getSourceName() {
		return ((TLSource) ((TLStream) ((TLAction) this.actions.get(0)).streams.get(0)).sources.get(0)).source;
	}

	public void setPixelFormatName(String pixelFormat) {
		((TLPixelFormat) ((TLSource) ((TLStream) ((TLAction) this.actions.get(0)).streams.get(0)).sources.get(0)).pixelFormats
				.get(0)).pixelFormat = pixelFormat;
	}

	@JsonIgnore
	public String getPixelFormatName() {
		return ((TLPixelFormat) ((TLSource) ((TLStream) ((TLAction) this.actions.get(0)).streams.get(0)).sources
				.get(0)).pixelFormats.get(0)).pixelFormat;
	}

	public void setAttValue(String attribute, Object objVal) {
		value value = new value(objVal);
		boolean boHit = false;
		for (TLAttribute att : ((TLPixelFormat) ((TLSource) ((TLStream) ((TLAction) this.actions.get(0)).streams.get(0)).sources
				.get(0)).pixelFormats.get(0)).attributes) {
			if (att.attribute.equals(attribute)) {
				boHit = true;
				att.values.set(0, value);
				break;
			}
		}
		if (!boHit) {
			TLAttribute att = new TLAttribute(attribute);
			att.values.set(0, value);
			((TLPixelFormat) ((TLSource) ((TLStream) ((TLAction) this.actions.get(0)).streams.get(0)).sources
					.get(0)).pixelFormats.get(0)).attributes.add(att);
		}
	}

	@JsonIgnore
	public Object getAttValue(String attribute) {
		Object objValue = "";
		for (TLAttribute att : ((TLPixelFormat) ((TLSource) ((TLStream) ((TLAction) this.actions.get(0)).streams.get(0)).sources
				.get(0)).pixelFormats.get(0)).attributes) {
			if (att.attribute.equals(attribute)) {
				objValue = ((value) att.values.get(0)).value;
				break;
			}
		}
		return objValue;
	}

	@JsonIgnore
	public static String getDefaultTaskString() {
		String strTask = "";
		String fileName = "template/defaultTask.json";
		File file = new File("./" + fileName);
		String encoding = "UTF-8";
		BufferedReader br = null;

		String text = "";

		try {
			if (file.exists()) {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(encoding)));
			} else {

				br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(fileName),
						Charset.forName(encoding)));
			}
			while ((text = br.readLine()) != null) {
				strTask = String.valueOf(strTask) + text + "\n";
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), text);
		}
		return strTask;
	}
}
