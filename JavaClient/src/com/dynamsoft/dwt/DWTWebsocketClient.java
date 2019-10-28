package com.dynamsoft.dwt;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;


public class DWTWebsocketClient extends WebSocketClient {

    private WSHandler handler;
    
    public DWTWebsocketClient(WSHandler handler, String url, Map<String, String> headers) throws URISyntaxException {
        super(new URI(url), headers);
    	this.handler = handler;
    }

    @Override
    public void onOpen(ServerHandshake shake) {
    	
        System.out.println("onOpen...");
        for(Iterator<String> it=shake.iterateHttpFields();it.hasNext();) {
            String key = it.next();
            System.out.println(key+":"+shake.getFieldValue(key));
        }

    	handler.onWSOpen();
    	
    }
 
    @Override
    public void onMessage(String msg) {
    	handler.onWSMessage(msg);
    }
 
    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
    	handler.onWSClose();
    }
 
    @Override
    public void onError(Exception e) {
    	handler.onWSError(e);
    }
    
    @Override
    public void send(String strData) {
    	super.send(strData);
    }

    public void sendBinary(String strData) {
    	this.sendBinary(strData, null);
    }
    
    public void sendBinary(String strData, byte[] binData) {
    	
    	if(null == strData || strData.isEmpty())
    		return;
    	
		byte[] _strData = strData.getBytes();
		
		int json_len = _strData.length;
		int bin_len = (null == binData) ? 0 : binData.length;
		int total_len = json_len + bin_len;
		
		byte[] newBlob = new byte[_strData.length+12];
		int i;

		// total length
		for (i = 0; i < 8; i++) {
			newBlob[i] = (byte) (total_len & 0xFF);
			total_len = total_len >> 8;
		}

		// bin length
		for (i = 8; i < 12; i++) {
			newBlob[i] = (byte) (json_len & 0xFF);
			json_len = json_len >> 8;
		}
		
		System.arraycopy(_strData, 0, newBlob, 12, _strData.length);

		super.send(newBlob);
		
		if(bin_len>0)
			super.send(binData);
		
    }
    
}

