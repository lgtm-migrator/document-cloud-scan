package com.dynamsoft.twaindirect.rest;

import com.dynamsoft.IniConfig;
import com.dynamsoft.twaindirect.data.response.Root;
import com.dynamsoft.twaindirect.local.entity.BaseSession;
import com.dynamsoft.twaindirect.local.entity.Const;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TwainLocalHttpClient extends AsyncRestClient {
	private static Logger logger = LoggerFactory.getLogger(TwainLocalHttpClient.class.getName());
	public final MediaType contentType;
	
	@SuppressWarnings("unused")
	private final long TIMER_COMMAND = 16000L;
	@SuppressWarnings("unused")
	private final long TIMER_EVENT = 31000L;
	@SuppressWarnings("unused")
	private final String GET_INFOX = "/privet/infoex";
	@SuppressWarnings("unused")
	private final String POST_SESSION = "/privet/twaindirect/session";

	private String restURL;
	private String xPrivetToken;
	private TwainLocalHttpCallback tdRestCallback;
	private String precommandId;
	private int cmdCount;

	public void init(String strTwainLocalUrl) {

		this.restURL = strTwainLocalUrl;
		logger.info(this.restURL);
	}

	public String getxPrivetToken() {
		return this.xPrivetToken;
	}

	public TwainLocalHttpClient(TwainLocalHttpCallback responseCallback) {
		this.contentType = MediaType.parse("application/json; charset=utf-8");
		this.xPrivetToken = "";

		this.precommandId = "";
		this.cmdCount = 0;
		this.tdRestCallback = responseCallback;
		long timeout = IniConfig.getInstance().getLong("timeout", "TIMER_COMMAND", 16000L);
		setTimeoutMillisec(Long.valueOf(timeout));
	}

	public void setxPrivetToken(String xPrivetToken) {
		this.xPrivetToken = xPrivetToken;
	}

	public String getCommandId() {
		if (this.precommandId.isEmpty()) {

			try {
				Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
				while (nics.hasMoreElements()) {
					NetworkInterface nic = (NetworkInterface) nics.nextElement();
					StringBuilder sb = new StringBuilder();
					byte[] hardwareAddress = nic.getHardwareAddress();
					if (hardwareAddress != null) {
						boolean first = true;
						int b1;
						byte[] arrayOfByte = hardwareAddress;
						for (b1 = 0; b1 < arrayOfByte.length; b1++) {
							byte b = arrayOfByte[b1];
							if (first) {
								first = false;
							} else {
								sb.append(":");
							}
							sb.append(String.format("%02x", new Object[] { Byte.valueOf(b) }));

						}

						this.precommandId = sb.toString();
					}
				}
			} catch (Exception exception) {
			}

			if (this.precommandId.isEmpty()) {
				this.precommandId = UUID.randomUUID().toString();
			}
		}
		return String.valueOf(this.precommandId)
				+ String.format("-%04d", new Object[] { Integer.valueOf(this.cmdCount++) });
	}

	public void Infoex() {
		this.xPrivetToken = "";
		Request request = (new Request.Builder()).url(String.valueOf(this.restURL) + "/privet/infoex")
				.addHeader("x-privet-token", this.xPrivetToken).build();
		get(request);
	}

	public void post(BaseSession session) {
		logger.info(String.valueOf(session.getClass().getSimpleName()) + "\n" + session.toString(true));

		RequestBody body = RequestBody.create(session.toString(), this.contentType);
		
		Request request = (new Request.Builder())
				.url(String.valueOf(this.restURL) + "/privet/twaindirect/session")
				.addHeader("x-privet-token", this.xPrivetToken)
				.post(body)
				.tag(session)
				.build();
		
		Long timeout = null;
		if ("waitForEvents".equals(session.method))
			timeout = Long.valueOf(IniConfig.getInstance().getLong("timeout", "TIMER_EVENT", 31000L));
		post(request, timeout);
	}

	public void Session(BaseSession session) {
		post(session);
	}

	public void onFailure(Call call, IOException e) {
		this.tdRestCallback.onFailure(call, e);
	}

	public void onResponse(Call call, Response response) {
		logger.info(response.toString());

		try {
			JsonNode rootNode = null;
			String strBody = "";
			byte[] byteBody = null;

			if ("multipart".equals(response.body().contentType().type())) {

				List<Object> bodys = new ArrayList<Object>();
				splitMultiPart(response, bodys);
				if (bodys.size() >= 2) {
					strBody = (String) bodys.get(0);
					byteBody = (byte[]) bodys.get(1);
				}

			} else if ("application".equals(response.body().contentType().type())
					&& "json".equals(response.body().contentType().subtype())) {
				strBody = response.body().string();
			} else {

				logger.error("unknown contentType :" + response.body().contentType());
				response.close();

				return;
			}
			ObjectMapper mapper = new ObjectMapper();
			rootNode = mapper.readTree(strBody);
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			logger.info("raw data\n" + mapper.writeValueAsString(rootNode));

			if ("GET".equalsIgnoreCase(call.request().method())) {

				this.tdRestCallback.onInfoex(rootNode);

				return;
			}
			Root responseRoot = (Root) mapper.readValue(strBody, Root.class);
			logger.debug("parse Root.class\n" + mapper.writeValueAsString(responseRoot));

			this.tdRestCallback.onSession(responseRoot);
			{
				switch(responseRoot.method) {
						
					case Const.method.createSession:
						this.tdRestCallback.onCreateSession(responseRoot);
						break;
					case Const.method.waitForEvents:
						this.tdRestCallback.onWaitForEvents(responseRoot);
						break;
					case Const.method.getSession:
						this.tdRestCallback.onGetSession(responseRoot);
						break;
					case Const.method.sendTask:
						this.tdRestCallback.onSendTask(responseRoot);
						break;
					case Const.method.startCapturing:
						this.tdRestCallback.onStartCapturing(responseRoot);
						break;
					case Const.method.readImageBlockMetadata:
						this.tdRestCallback.onReadImageBlockMetadata(responseRoot);
						break;
					case Const.method.readImageBlock:
						this.tdRestCallback.onReadImageBlock(responseRoot, byteBody);
						break;
					case Const.method.releaseImageBlocks:
						this.tdRestCallback.onReleaseImageBlocks(responseRoot);
						break;
					case Const.method.stopCapturing:
						this.tdRestCallback.onStopCapturing(responseRoot);
						break;
					case Const.method.closeSession:
						this.tdRestCallback.onCloseSession(responseRoot);
						break;
					default:
						// not support
						break;
				}

				//cmdName = "on" + cmdName.substring(0, 1).toUpperCase() + cmdName.substring(1);
				//Method cmdMethod = TDRestCallback.class.getMethod(cmdName, new Class[] { Root.class });
				//cmdMethod.invoke(this.tdRestCallback, new Object[] { responseRoot });
			}

		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	private void splitMultiPart(Response response, List<Object> bodys) {
		try {
			InputStream is = response.body().byteStream();

			String boundary = readLine(is);
			MediaType mtype = null;
			String strBody = "";
			String wkStr = "";
			int length = 0;

			for (String line = readLine(is); line != null; line = readLine(is)) {
				if (line.isEmpty()) {
					if (mtype != null && "pdf".equals(mtype.subtype())) {
						break;
					}
				} else if (boundary.equals(line)) {
					if (mtype != null && "json".equals(mtype.subtype())) {
						strBody = wkStr;
					} else if (mtype != null && "pdf".equals(mtype.subtype())) {

						System.out.println(wkStr);
					} else {

						logger.error("unknown contentType :" + mtype);
					}
					wkStr = "";
				} else if (line.startsWith("Content-")) {
					if (line.startsWith("Content-Type")) {
						mtype = MediaType.parse(line.substring(line.indexOf(":") + 1).trim());
					} else if (line.startsWith("Content-Length") && mtype != null && "pdf".equals(mtype.subtype())) {
						length = Integer.parseInt(line.substring(line.indexOf(":") + 1).trim());
					}
				} else {

					wkStr = String.valueOf(wkStr) + line + "\n";
				}
			}

			logger.info("PDF Read START");

			byte[] byteBody = new byte[length];

			readBytes(is, byteBody, length);

			logger.info("PDF Read END");

			is.close();
			bodys.add(strBody);
			bodys.add(byteBody);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String readLine(InputStream in) {
		byte[] buf = new byte[8192];
		int length = 0;
		for (int i = 0; i < buf.length; i++) {
			try {
				int ret = in.read(buf, i, 1);
				if (ret < 0) {
					logger.info("readLine:closed. ret=" + ret + " length=" + length);
					return null;
				}
				if (ret > 0 && buf[i] != 13) {

					if (buf[i] == 10) {
						break;
					}
					length++;
				}
			} catch (Exception e) {
				logger.error("ConnectionThread:readLine:IOException:" + e.getMessage() + " length=" + length);
				return null;
			}
		}
		if (length == 0) {
			return "";
		}
		byte[] bytes = Arrays.copyOfRange(buf, 0, length);
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("ConnectionThread:readLine:UnsupportedEncodingException");
			return null;
		}
	}

	private int readBytes(InputStream in, byte[] buf, int contentLength) throws IOException {
		int cnt;
		if (contentLength == 0) {
			cnt = in.read(buf);
		} else {
			int length;

			if (buf.length < contentLength) {
				length = buf.length;
			} else {
				length = contentLength;
			}

			cnt = 0;
			while (true) {
				int ret = in.read(buf, cnt, length - cnt);
				if (ret < 0) {
					logger.info("readBytes:closed. ret=" + ret + " cnt=" + cnt);
					return cnt;
				}
				if (ret > 0) {
					cnt += ret;
				}
				if (cnt < length) {
					try {
						Thread.sleep(1L);
						continue;
					} catch (InterruptedException interruptedException) {
						continue;
					}
				}

				break;
			}
			if (length < contentLength) {
				while (true) {
					long ret = in.skip((contentLength - cnt));
					if (ret < 0L) {
						logger.info("readBytes:closed. ret=" + ret + " cnt=" + cnt);
						return cnt;
					}
					if (ret > 0L) {
						cnt = (int) (cnt + ret);
					}
					if (cnt < contentLength) {
						try {
							Thread.sleep(1L);
							continue;
						} catch (InterruptedException interruptedException) {
							continue;
						}
					}
					break;
				}
			}
		}
		return cnt;
	}
}
