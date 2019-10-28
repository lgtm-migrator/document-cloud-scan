package com.dynamsoft.twaindirect.app;

import com.dynamsoft.twaindirect.data.request.CloseSession;
import com.dynamsoft.twaindirect.data.request.CreateSession;
import com.dynamsoft.twaindirect.data.request.GetSession;
import com.dynamsoft.twaindirect.data.request.ReadImageBlock;
import com.dynamsoft.twaindirect.data.request.ReadImageBlockMetadata;
import com.dynamsoft.twaindirect.data.request.ReleaseImageBlocks;
import com.dynamsoft.twaindirect.data.request.SendTask;
import com.dynamsoft.twaindirect.data.request.StartCapturing;
import com.dynamsoft.twaindirect.data.request.StopCapturing;
import com.dynamsoft.twaindirect.data.request.WaitForEvents;
import com.dynamsoft.twaindirect.data.response.Address;
import com.dynamsoft.twaindirect.data.response.Event;
import com.dynamsoft.twaindirect.data.response.Root;
import com.dynamsoft.twaindirect.data.response.Session;
import com.dynamsoft.twaindirect.local.entity.BaseSession;
import com.dynamsoft.twaindirect.local.entity.JsonUtil;
import com.dynamsoft.twaindirect.local.entity.TLTask;
import com.dynamsoft.twaindirect.rest.TwainLocalHttpCallback;
import com.dynamsoft.twaindirect.rest.TwainLocalHttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwainBroker implements TwainLocalHttpCallback {

	private static final Logger logger = LoggerFactory.getLogger(TwainBroker.class.getName());
	public static TwainBroker instance = null;
	
	private ScanProgress dlgScan;
	private final TwainLocalHttpClient tdRest;

	public TwainBroker() {
		this.tdRest = new TwainLocalHttpClient(this);
		this.dlgScan = new ScanProgress();
		instance = this;


		this.boDebugMode = false;
		this.state = "";
		this.sessionId = "";
		this.revision = 0;
		this.boReadImageBlock = false;
		this.currentReadImageBlock = 0;
		this.imageBlockList = new ArrayList<Integer>();
		this.lockObj = new Object();

		this.boCancel = false;
		
		/*
		btnScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScannerExecute.this.cmdCreateSession();
			}
		});
		btnInfoex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScannerExecute.this.tdRest.Infoex();
			}
		});
		btnCreateSession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScannerExecute.this.cmdCreateSession();
			}
		});
		btnWaitforevent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScannerExecute.this.cmdWaitForEvents();
			}
		});
		btnGetsession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScannerExecute.this.cmdGetSession();
			}
		});
		btnSendtask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {x
				ScannerExecute.this.cmdSendTask();
			}
		});
		btnStartcapturing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScannerExecute.this.cmdStartCapturing();
			}
		});
		btnStopcapturing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScannerExecute.this.cmdStopCapturing();
			}
		});
		btnClosesession.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScannerExecute.this.cmdCloseSession();
			}
		});
		btnReadimageblock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int imageBlockNum = ScannerExecute.this.txtImageBlockNum.getInt();
				ScannerExecute.this.cmdReadImageBlock(imageBlockNum);
			}
		});
		btnReadimageblockmetadata.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int imageBlockNum = ScannerExecute.this.txtImageBlockNum2.getInt();
				ScannerExecute.this.cmdReadImageBlockMetadata(imageBlockNum);
			}
		});x
		btnReleaseimageblocks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int imageBlockNum = ScannerExecute.this.txtNum.getInt();
				int lastImageBlockNum = ScannerExecute.this.txtLastNum.getInt();
				ScannerExecute.this.cmdReleaseImageBlocks(imageBlockNum, lastImageBlockNum);
			}
		});
*/
		logger.debug("End");
	}

	private boolean boDebugMode;
	private String state;
	private String sessionId;
	private int revision;
	private boolean boReadImageBlock;
	private int currentReadImageBlock;
	private List<Integer> imageBlockList;
	private Object lockObj;
	private boolean boCancel;

	public void scanCancel() {
		logger.info("state[" + this.state + "]");
		this.boCancel = true;

		String str;
		switch ((str = this.state).hashCode()) {
		case -1718402667:
			if (!str.equals("noSession"))
				;
			break;
		case -1541723517:
			if (!str.equals("capturing")) {
				break;
			}

			cmdStopCapturing();
			break;
		case -1357520532:
			if (!str.equals("closed")) {
				break;
			}
			cmdReleaseImageBlocks(1, 2147483647);
			break;
		case -839042326:
			if (!str.equals("draining"))
				break;
			cmdReleaseImageBlocks(1, 2147483647);
			break;
		case 108386723:
			if (!str.equals("ready"))
				break;
			cmdCloseSession();
			break;
		}
	}

	@SuppressWarnings("unused")
	private void cmdCreateSession() {
		CreateSession cmd = new CreateSession(this.tdRest.getCommandId());
		this.tdRest.Session(cmd);
	}

	private void cmdWaitForEvents() {
		if (this.state.isEmpty() || "noSession".equals(this.state)) {
			logger.warn("state[" + this.state + "]");
			return;
		}
		WaitForEvents cmd = new WaitForEvents(this.tdRest.getCommandId(), this.sessionId);
		cmd.params.sessionRevision = Integer.valueOf(this.revision);
		this.tdRest.Session(cmd);
	}

	@SuppressWarnings("unused")
	private void cmdSendTask(String strJson) {
		SendTask cmd = new SendTask(this.tdRest.getCommandId(), this.sessionId);
		ObjectMapper mapper = new ObjectMapper();
		try {
			cmd.params.task = (TLTask) mapper.readValue(strJson, TLTask.class);
			this.tdRest.Session(cmd);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void cmdStartCapturing() {
		this.boReadImageBlock = false;
		StartCapturing cmd = new StartCapturing(this.tdRest.getCommandId(), this.sessionId);
		this.tdRest.Session(cmd);
	}

	private void cmdStopCapturing() {
		StopCapturing cmd = new StopCapturing(this.tdRest.getCommandId(), this.sessionId);
		this.tdRest.Session(cmd);
	}

	@SuppressWarnings("unused")
	private void cmdReadImageBlockMetadata(int imageBlockNum) {
		cmdReadImageBlockMetadata(imageBlockNum, false);
	}

	private void cmdReadImageBlockMetadata(int imageBlockNum, boolean withThumbnail) {
		ReadImageBlockMetadata cmd = new ReadImageBlockMetadata(this.tdRest.getCommandId(), this.sessionId);
		cmd.params.withThumbnail = Boolean.valueOf(withThumbnail);
		cmd.params.imageBlockNum = Integer.valueOf(imageBlockNum);
		this.tdRest.Session(cmd);
	}

	private void cmdReadImageBlock(int imageBlockNum) {
		cmdReadImageBlock(imageBlockNum, true);
	}

	private void cmdReadImageBlock(int imageBlockNum, boolean withMetadata) {
		ReadImageBlock cmd = new ReadImageBlock(this.tdRest.getCommandId(), this.sessionId);
		cmd.params.withMetadata = Boolean.valueOf(withMetadata);
		cmd.params.imageBlockNum = Integer.valueOf(imageBlockNum);
		this.currentReadImageBlock = imageBlockNum;
		this.tdRest.Session(cmd);
	}

	private void cmdReleaseImageBlocks(int imageBlockNum, int lastImageBlockNum) {
		ReleaseImageBlocks cmd = new ReleaseImageBlocks(this.tdRest.getCommandId(), this.sessionId);
		cmd.params.imageBlockNum = Integer.valueOf(imageBlockNum);
		cmd.params.lastImageBlockNum = Integer.valueOf(lastImageBlockNum);
		this.tdRest.Session(cmd);
	}

	private void cmdReleaseImageBlocks(int imageBlockNum) {
		cmdReleaseImageBlocks(imageBlockNum, imageBlockNum);
	}

	private void cmdGetSession() {
		GetSession cmd = new GetSession(this.tdRest.getCommandId(), this.sessionId);
		this.tdRest.Session(cmd);
	}

	private void cmdCloseSession() {
		CloseSession cmd = new CloseSession(this.tdRest.getCommandId(), this.sessionId);
		this.tdRest.Session(cmd);
	}

	/// String url = info.https.booleanValue() ? "https" : "http" + "://" + info.hostAddress[0] + ":" + info.port;
	public void init(String url) {
		logger.info("Start");
		
		this.tdRest.init(url);

		this.tdRest.Infoex();

		logger.info("End");
	}

	public void onFailure(Call call, IOException e) {
		logger.error(call.request().toString(), e);
			String msg = "method=" + call.request().method() + ", url=" + call.request().url();
			try {
				BaseSession session = (BaseSession) call.request().tag();
				if (session != null) {
					msg = String.valueOf(msg) + "\n" + session.toString(true);
				}
			} catch (Exception exception) {
			}

	}

	private void showResponseJson(final Object rootNode) {
		System.out.println(JsonUtil.writeValueAsString(rootNode, true));
	}

	public void onInfoex(JsonNode rootNode) {
		String xPrivetToken = rootNode.get("x-privet-token").asText();

		this.tdRest.setxPrivetToken(xPrivetToken);
		if (this.boDebugMode) {
			showResponseJson(rootNode);
		}
	}

	private void readSession(Session session) {
		if (session == null)
			return;
		String detected = "";
		this.sessionId = session.sessionId;
		this.revision = session.revision.intValue();
		this.state = session.state;
		if (session.status != null && !session.status.success.booleanValue()) {
			detected = session.status.detected;
			this.dlgScan.onDetected(detected);
		}


		if (session.imageBlocks != null) {

			synchronized (this.lockObj) {
				this.imageBlockList = session.imageBlocks;
			}
			if (this.boDebugMode) {
				int size = this.imageBlockList.size();
				if (size > 0) {
					System.out.println(String.format("ImageBlockNum:%d", this.imageBlockList.get(0)));
					System.out.println(String.format("ImageBlockNum2:%d", this.imageBlockList.get(0)));
					System.out.println(String.format("Num:%d", this.imageBlockList.get(0)));
					System.out.println(String.format("LastNum:%d", this.imageBlockList.get(0)));
				}
			}
		}
	}

	public void onSession(Root responseRoot) {
		try {
			String code = "";
			boolean success = responseRoot.results.success.booleanValue();
			if (!success) {
				code = responseRoot.results.code;

				if ("critical".equals(code)) {
					this.dlgScan.onError(responseRoot.results.reason);
				} else {

					this.dlgScan.onError(code);
				}
			} else {
				readSession(responseRoot.results.session);
			}
			if (this.boDebugMode) {
				showResponseJson(responseRoot);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void onCreateSession(Root responseRoot) {
		if (this.boDebugMode)
			return;
		logger.info("Start");
		boolean success = responseRoot.results.success.booleanValue();
		if (success) {

			cmdWaitForEvents();

			//String strJson = this.tabbedPane.getJsonString();
			//cmdSendTask(strJson);
		}
		logger.info("End");
	}

	public void onWaitForEvents(Root responseRoot) {
		logger.info("Start");
		try {
			boolean success = responseRoot.results.success.booleanValue();
			if (!success) {
				if (!this.boDebugMode && !this.boCancel) {
					String code = responseRoot.results.code;
					if ("timeout".equals(code)) {

						cmdWaitForEvents();
					} else if ("invalidState".equals(code)) {
						cmdGetSession();
					}
				}
			} else {
				if (!this.boDebugMode && !this.boCancel) {
					cmdWaitForEvents();
				}
				for (Event event : responseRoot.results.events) {
					if ("imageBlocks".equals(event.event)) {
						if (event.session != null) {
							readSession(event.session);

							if (!this.boDebugMode && !this.boCancel) {
								synchronized (this.lockObj) {

									if (!this.imageBlockList.isEmpty() && !this.boReadImageBlock) {
										this.boReadImageBlock = true;

										cmdReadImageBlock(((Integer) this.imageBlockList.get(0)).intValue());
									}
									continue;
								}
							}
							continue;
						}
						logger.error("no session node");
						continue;
					}
					if (!"commandComplete".equals(event.event)) {
						if (!"commandUpdate".equals(event.event)) {

							logger.error("unknown event : " + event.event);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("End");
	}

	public void onGetSession(Root responseRoot) {
		if (this.boDebugMode)
			return;
		logger.info("Start");
		logger.info("End");
	}

	public void onSendTask(Root responseRoot) {
		if (this.boDebugMode)
			return;
		logger.info("Start");
		boolean success = responseRoot.results.success.booleanValue();
		if (success) {
			cmdStartCapturing();
		}
		logger.info("End");
	}

	public void onStartCapturing(Root responseRoot) {
		if (this.boDebugMode) {
			boolean success = responseRoot.results.success.booleanValue();
			if (success) {

				System.out.println(String.format("ImageBlockNum:%d", 1));
				System.out.println(String.format("ImageBlockNum2:%d", 1));
				System.out.println(String.format("Num:%d", 1));
				System.out.println(String.format("LastNum:%d", 1));
				
			}
			return;
		}
		logger.info("Start");
		logger.info("End");
	}

	public void onReadImageBlockMetadata(Root responseRoot) {
		if (this.boDebugMode)
			return;
		logger.info("Start");
		logger.info("End");
	}

	public void onReadImageBlock(Root responseRoot, byte[] data) {
		logger.info("Start");
		try {
			boolean success = responseRoot.results.success.booleanValue();
			if (!success) {
				return;
			}

			Address address = responseRoot.results.metadata.address;

			ScanProgress.WriteData writeData = new ScanProgress.WriteData(this.currentReadImageBlock,
					address, data);
			this.dlgScan.push(writeData);

			if (this.boDebugMode || this.boCancel) {
				return;
			}
			synchronized (this.lockObj) {

				if (!this.imageBlockList.isEmpty()) {
					if (this.currentReadImageBlock == ((Integer) this.imageBlockList.get(0)).intValue()) {
						cmdReleaseImageBlocks(((Integer) this.imageBlockList.get(0)).intValue());
					} else {
						cmdReadImageBlock(((Integer) this.imageBlockList.get(0)).intValue());
					}

				} else {

					StopCapturingJudgment(responseRoot.results.session);
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("End");
	}

	public void onReleaseImageBlocks(Root responseRoot) {
		if (this.boDebugMode)
			return;
		logger.info("Start");
		try {
			boolean success = responseRoot.results.success.booleanValue();
			if (!success) {
				return;
			}
			if (this.boCancel) {
				cmdCloseSession();
			} else {

				synchronized (this.lockObj) {

					if (!this.imageBlockList.isEmpty()) {
						cmdReadImageBlock(((Integer) this.imageBlockList.get(0)).intValue());
					} else {
						StopCapturingJudgment(responseRoot.results.session);
					}

				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("End");
	}

	private void StopCapturingJudgment(Session session) {
		if (session == null)
			return;
		boolean boDoneCapturing = session.doneCapturing.booleanValue();
		boolean boImageBlocksDrained = session.imageBlocksDrained.booleanValue();

		if (boDoneCapturing && boImageBlocksDrained) {

			cmdStopCapturing();

		} else {

			this.boReadImageBlock = false;
		}
	}

	public void onStopCapturing(Root responseRoot) {
		if (this.boDebugMode)
			return;
		logger.info("Start");
		try {
			this.dlgScan.scanEnd();
			boolean success = responseRoot.results.success.booleanValue();
			if (!success) {
				return;
			}
			if (this.boCancel) {
				cmdReleaseImageBlocks(1, 2147483647);
			} else {

				cmdCloseSession();
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("End");
	}

	public void onCloseSession(Root responseRoot) {
		if (this.boDebugMode)
			return;
		logger.info("Start");
		logger.info("End");
	}
}
