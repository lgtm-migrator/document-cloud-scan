package com.dynamsoft.twaindirect.local;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.dynamsoft.DLogger;
import com.dynamsoft.could.entity.TCBlockCmdResponse;
import com.dynamsoft.could.entity.TCCmdEventsResponse;
import com.dynamsoft.could.entity.TCCmdInput;
import com.dynamsoft.could.entity.TCCmdResponse;
import com.dynamsoft.could.entity.TCLocalParameter;
import com.dynamsoft.dwt.DWTClient;
import com.dynamsoft.dwt.ICmdCallback;
import com.dynamsoft.dwt.common.EnumDWT_PDFCompressionType;
import com.dynamsoft.dwt.common.EnumDWT_PixelType;
import com.dynamsoft.dwt.evt.DWTEventHandler;
import com.dynamsoft.dwt.evt.EventType;
import com.dynamsoft.twaindirect.local.entity.TDLEvent;
import com.dynamsoft.twaindirect.local.entity.TDLEventSession;
import com.dynamsoft.twaindirect.local.entity.TDLSession;
import com.dynamsoft.twaindirect.local.entity.TLTask;
import com.dynamsoft.twaindirect.local.entity.TLTask.TLAction;
import com.dynamsoft.twaindirect.local.entity.TLTask.TLAttribute;
import com.dynamsoft.twaindirect.local.entity.TLTask.TLPixelFormat;
import com.dynamsoft.twaindirect.local.entity.TLTask.TLSource;
import com.dynamsoft.twaindirect.local.entity.TLTask.TLStream;

public class TDLDoActions extends TDLSessionActions {
	
	private static int errCapabilityNotSupported = -1034;
	
	protected DWTClient dwtClient;
	private String scanner;
	private boolean bCaptured = false;
	
	public TDLDoActions(DWTClient dwtClient, String scanner)
	{
		super(TDLSessionState.noSession);
		this.dwtClient = dwtClient;
		this.scanner = scanner;
	}
	
	public boolean isCaptured()
	{
		return this.bCaptured;
	}

	public void setCaptured(boolean b) {

		this.bCaptured = b;
	}
	
	protected TCCmdResponse cmdCreateSession(TCCmdInput cmdInput) {

		TCCmdResponse response = new TCCmdResponse(cmdInput);

		boolean handled = false;
		
		List<String> listSource = this.dwtClient.GetSourceNames();

		DLogger.GetLogger().LogInfo("Get Source Names: ");
		int sourceIndex = -1;

		if(this.scanner.isEmpty()) {
			if(listSource.size()>0) {
				sourceIndex = 0;
				DLogger.GetLogger().LogInfo("Select first Source:" + listSource.get(0));
			} else {
				DLogger.GetLogger().LogInfo("No Sources");
			}
		} else {
			
			for(int i=0; i<listSource.size(); i++) {
				DLogger.GetLogger().LogInfo(listSource.get(i));
				if(listSource.get(i).equals(this.scanner)) {
					sourceIndex = i;
					DLogger.GetLogger().LogInfo("Select Source:" + this.scanner);
					break;
				}
			}
		}
		
		this.dwtClient.RemoveAllImages();
		if(sourceIndex>=0) {

			super.releaseImageBlocks();
			this.dwtClient.closeSource();
			this.dwtClient.selectSourceByIndex(sourceIndex);
			this.dwtClient.openSource();
			DLogger.GetLogger().LogInfo("Open Source ok");

			handled = super.createSession();
		}
		
		if(handled) {
		
			response.results.success = true;
			setResponse(true, response);

			DLogger.GetLogger().LogInfo("Create Session: true");
		} else {

			setResponse(false, response);

			DLogger.GetLogger().LogInfo("Create Session: false");
		}
		
		return response;
	}

	protected void setResponse(boolean bSuccess, TCBlockCmdResponse response)
	{
		response.results.success = bSuccess;
		if(bSuccess) {
			super.session.state = super.state.toString();
			if(null != super.session)
				response.results.session.CopyFrom(super.session);
			else
				response.results.session = new TDLSession(super.session);
		} else {
			response.results.session = null;
		}
	}
	
	protected void setResponse(boolean bSuccess, TCCmdResponse response)
	{
		response.results.success = bSuccess;
		if(bSuccess) {
			super.session.state = super.state.toString();
			if(null != super.session)
				response.results.session.CopyFrom(super.session);
			else
				response.results.session = new TDLSession(super.session);
		} else {
			response.results.session = null;
		}
	}
	
	protected void setEventResponse(boolean bSuccess, TCCmdEventsResponse response)
	{
		response.results.success = bSuccess;
		if(bSuccess) {
			super.session.state = super.state.toString();
			
			TDLEventSession evtSession = new TDLEventSession(super.session);
			
			TDLEvent event = new TDLEvent(evtSession);
			
			if(this.bCaptured) {
				event.event = "imageBlocks";
				List<Integer> list = event.session.imageBlocks;
				for(int i=0; i< dwtClient.HowManyImagesInBuffer(); i++) {
					list.add(i+1);
				}

			} else {
				event.event = "";
				// TODO: send more events
				// event.event = "commandComplete";
				// event.event = "commandUpdate";";
				// event.event = "sessionTimeout";
			}
			
			
			response.results.events.add(event);
		} else {
			
		}
	}
	
	protected TCCmdResponse cmdCloseSession(TCCmdInput cmdInput) {

		TCCmdResponse response = new TCCmdResponse(cmdInput);
		
		this.dwtClient.closeSource();
		this.dwtClient.closeSourceManager();
		
		
		boolean handled = super.closeSession(this.dwtClient.hasBlocks());
		
		DLogger.GetLogger().LogInfo("DeviceScannerCloseSession");

		if(handled) {
		
			setResponse(true, response);
		} else {

			setResponse(false, response);
			
		}
		return response;
	}

	protected TCCmdResponse cmdGetSession(TCCmdInput cmdInput) {

		TCCmdResponse response = new TCCmdResponse(cmdInput);
		DLogger.GetLogger().LogInfo("DeviceScannerGetSession");
		
		setResponse(true, response);
		
		return response;
	}

	protected TCCmdResponse cmdStopCapturing(TCCmdInput cmdInput) {

		this.dwtClient.cancelAllPendingTransfers();

		TCCmdResponse response = new TCCmdResponse(cmdInput);
		boolean handled = super.stopCapturing(this.dwtClient.hasBlocks());
		
		DLogger.GetLogger().LogInfo("DeviceScannerStopCapturing");
		if(handled) {
		
			setResponse(true, response);
		} else {

			setResponse(false, response);
			
		}
		return response;
	}

	protected TCCmdResponse cmdStartCapturing(TCCmdInput cmdInput) {

		TCCmdResponse response = new TCCmdResponse(cmdInput);

		this.bCaptured = false;
		
		this.dwtClient.addEvent(EventType.OnPostAllTransfers, new DWTEventHandler() {
			
			@Override
			public void callback(List<String> params) {

				
				DLogger.GetLogger().LogInfo("OnPostAllTransfers event");

				// send event : capture succes
				int imageCount = dwtClient.HowManyImagesInBuffer();
				if(imageCount>0) {

					DLogger.GetLogger().LogInfo("change state: capturing => draining");
					boolean bHandled = transfer(TDLSessionState.capturing, TDLSessionState.draining);
				} else {
					DLogger.GetLogger().LogInfo("change state: capturing => ready");

					boolean bHandled = transfer(TDLSessionState.capturing, TDLSessionState.ready);
				}
				
				bCaptured = true;
			}
		});
		
		// do capture
		this.dwtClient.Acquire(new ICmdCallback() {
			
			@Override
			public void sFun(List<String> ret) {
				
				
				DLogger.GetLogger().LogInfo("Acquire OK");

			}
			
			@Override
			public boolean fFun(String errString) {
				// send event : capture failed
				DLogger.GetLogger().LogInfo("Acquire Failed");
				boolean bHandled = transfer(TDLSessionState.capturing, TDLSessionState.ready);
				return false;
			}
		});
		
		
		boolean handled = super.startCapturing();
		
		DLogger.GetLogger().LogInfo("DeviceScannerStartCapturing");
		if(handled) {
			setResponse(true, response);
		} else {
			setResponse(false, response);
			
		}
		return response;
	}

	protected TCCmdResponse cmdSendTask(TCCmdInput cmdInput) {

		TCCmdResponse response = new TCCmdResponse(cmdInput);
		DLogger.GetLogger().LogInfo("DeviceScannerSendTask");
		
		
		
		boolean handled = false;

		TCLocalParameter params = cmdInput.params;
		
		do {
		if (null != params && null != params.task) {

			TLTask task = JSONObject.toJavaObject(params.task, TLTask.class);
			
			if (null != task) {
				List<TLAction> actions = task.actions;
				if (null != actions && actions.size() > 0) {
					TLAction action = actions.get(0);
					List<TLStream> streams = action.streams;
					if (null != streams && streams.size() > 0) {
						TLStream stream = streams.get(0);
						List<TLSource> sources = stream.sources;
						if (null != sources && sources.size() > 0) {
							TLSource source = sources.get(0);
							
							boolean ifSetSourceSuccess = true;
							if(source.source.equals("any")) {
								
							} else if(source.source.equals("feederRear")) {
								ifSetSourceSuccess = this.dwtClient.IfAutoFeed(true);
								if(!ifSetSourceSuccess)
									break;
								ifSetSourceSuccess = this.dwtClient.IfDuplexEnabled(true);
								if(!ifSetSourceSuccess)
									break;
								
								// for test, scan 3 pages
								this.dwtClient.XferCount(3);
								
							} else if(source.source.equals("feederFront")) {
								ifSetSourceSuccess = this.dwtClient.IfAutoFeed(true);
								if(!ifSetSourceSuccess)
									break;

								// for test, scan 3 pages
								this.dwtClient.XferCount(3);
								
							}

							boolean ifSetFormatSuccess = true;
							List<TLPixelFormat> formats = source.pixelFormats;
							if (null != formats && formats.size() > 0) {
								TLPixelFormat pixFormat = formats.get(0);
								String pixel = pixFormat.pixelFormat;
								if (pixel.equals("bw1")) {
									ifSetFormatSuccess = this.dwtClient.SetPixelType(EnumDWT_PixelType.TWPT_BW);
									if(!ifSetFormatSuccess)
										break;
								} else if (pixel.equals("gray8") || pixel.equals("gray16")) {
									ifSetFormatSuccess = this.dwtClient.SetPixelType(EnumDWT_PixelType.TWPT_GRAY);
									if(!ifSetFormatSuccess)
										break;
								} else if (pixel.equals("rgb24") || pixel.equals("rgb48")) {
									ifSetFormatSuccess = this.dwtClient.SetPixelType(EnumDWT_PixelType.TWPT_RGB);
									if(!ifSetFormatSuccess)
										break;
								} else if (pixel.equals("raw")) {
									ifSetFormatSuccess = this.dwtClient.SetPixelType(EnumDWT_PixelType.TWPT_RGB);
									if(!ifSetFormatSuccess)
										break;
								}
								
								
								boolean ifSetAttributeSuccess = true;
								List<TLAttribute> attributes = pixFormat.attributes;
								// {"attribute":"resolution","values":[{"value":"100"}]}
								// {"attribute":"automaticDeskew","values":[{"value":"off"}]}
								// {"attribute":"discardBlankImages","values":[{"value":"off"}]}
								// {"attribute":"compression","values":[{"value":"group4"}]}
								if (null != attributes && attributes.size() > 0) {
									boolean breakWhile = false;
									
									for(int i=0; i<attributes.size(); i++) {
										TLAttribute attribute = attributes.get(i);
										if(null != attribute.values && attribute.values.size()>0) {
											Object value = attribute.values.get(0).value;
											switch(attribute.attribute) {
											case "resolution":
												Integer iResolution = Integer.valueOf(value.toString());
												ifSetAttributeSuccess = this.dwtClient.SetResolution(iResolution);
												if(!ifSetAttributeSuccess)
													breakWhile = true;
												break;
											case "automaticDeskew":
												if(value.equals("off")) {
													ifSetAttributeSuccess = this.dwtClient.IfAutomaticDeskew(false);
												} else if(value.equals("on")) {
													ifSetAttributeSuccess = this.dwtClient.IfAutomaticDeskew(true);
												}
												if(!ifSetAttributeSuccess && this.dwtClient.getErrorCode() != errCapabilityNotSupported)
													breakWhile = true;
												break;
											case "discardBlankImages":
												if(value.equals("off")) {
													ifSetAttributeSuccess = this.dwtClient.IfAutoDiscardBlankpages(false);
												} else if(value.equals("on")) {
													ifSetAttributeSuccess = this.dwtClient.IfAutoDiscardBlankpages(false);
												}
												
												if(!ifSetAttributeSuccess && this.dwtClient.getErrorCode() != errCapabilityNotSupported)
													breakWhile = true;
												break;
											case "compression":
												if(value.equals("autoVersion1")) {
													ifSetAttributeSuccess = this.dwtClient.SetPDFCompressionType(EnumDWT_PDFCompressionType.PDF_AUTO);
												} else if(value.equals("group4")) {
													if (pixel.equals("bw1")) {
														ifSetAttributeSuccess = this.dwtClient.SetPDFCompressionType(EnumDWT_PDFCompressionType.PDF_FAX4);	
													} else {
														ifSetAttributeSuccess = this.dwtClient.SetPDFCompressionType(EnumDWT_PDFCompressionType.PDF_LZW);
													}
												} else if(value.equals("jpeg")) {
													ifSetAttributeSuccess = this.dwtClient.SetPDFCompressionType(EnumDWT_PDFCompressionType.PDF_JPEG);
												} else if(value.equals("none")) {
													ifSetAttributeSuccess = this.dwtClient.SetPDFCompressionType(EnumDWT_PDFCompressionType.PDF_LZW);
												} else {
													ifSetAttributeSuccess = this.dwtClient.SetPDFCompressionType(EnumDWT_PDFCompressionType.PDF_AUTO);
												}
												if(!ifSetAttributeSuccess)
													breakWhile = true;
												break;
											}
										}
										if(breakWhile)	// break for
											break;
									}
									if(breakWhile) // break while
										break;
								}
							}
							
							handled = true;
						}
					}
				}
			}
		}
		} while(false);
		
		if(handled) {
			setResponse(true, response);
		} else {
			setResponse(false, response);
		}
		return response;
	}
	
	public TCCmdEventsResponse cmdWaitForEvents(TCCmdInput cmdInput) {

		TCCmdEventsResponse response = new TCCmdEventsResponse(cmdInput);
		DLogger.GetLogger().LogInfo("DeviceScannerSendTask");
		boolean handled = true;
		if(handled) {
			setEventResponse(true, response);
		} else {

			setEventResponse(false, response);
			
		}
		return response;
	}
	

	protected TCCmdResponse readImageBlock(TCCmdInput cmdInput) {

		TCCmdResponse response = new TCCmdResponse(cmdInput);
		DLogger.GetLogger().LogInfo("readImageBlock");
		
		boolean handled = true;
		if(handled) {
			setResponse(true, response);
		} else {
			setResponse(false, response);
			
		}
		return response;
	}

	protected TCCmdResponse readImageBlockMetadata(TCCmdInput cmdInput) {

		TCCmdResponse response = new TCCmdResponse(cmdInput);
		DLogger.GetLogger().LogInfo("readImageBlockMetadata");
		boolean handled = super.releaseImageBlocks();
		if(handled) {
			setResponse(true, response);
		} else {
			setResponse(false, response);
			
		}
		return response;
	}
	protected TCCmdResponse releaseImageBlocks(TCCmdInput cmdInput) {

		TCCmdResponse response = new TCCmdResponse(cmdInput);
		DLogger.GetLogger().LogInfo("releaseImageBlocks");
		
		this.dwtClient.RemoveAllImages();
		
		boolean handled = super.releaseImageBlocks();
		if(handled) {
			setResponse(true, response);
		} else {

			setResponse(false, response);
			
		}
		return response;
	}
}
