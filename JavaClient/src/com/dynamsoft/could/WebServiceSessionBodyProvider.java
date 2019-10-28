package com.dynamsoft.could;

public class WebServiceSessionBodyProvider {

    // commandId: client generated id
    public String createSession(String commandId)
    {   	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"createSession\"}", 
				commandId);
    }

    // commandId: client generated id
    // sessionId: <from createSession>
    // sessionRevision: revision number of last session object processed by the application
    public String waitForEvents(String commandId,
    		String sessionId,
    		String sessionRevision)
    {   	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"waitForEvents\",\"params\":{\"sessionId\":\"%s\",\"sessionRevision\":\"%s\"}}", 
				commandId,
				sessionId,
				sessionRevision);
    }

    // commandId: client generated id
    // sessionId: <from createSession>
    public String sessionGet(String commandId,
    		String sessionId)
    {   	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"getSession\",\"params\":{\"sessionId\":\"%s\"}}", 
				commandId,
				sessionId);
    }

    // commandId: client generated id
    // sessionId: <from createSession>
    // twainDirectTaskJson: TWAIN Direct task same as TWAIN local
    public String sendTask(String commandId,
    		String sessionId,
    		String twainDirectTaskJson)
    {   	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"sendTask\",\"params\":{\"sessionId\":\"%s\",\"task\":%s}}", 
				commandId,
				sessionId,
				twainDirectTaskJson);
    }

    // commandId: client generated id
    // sessionId: <from createSession>
    public String startCapturing(String commandId,
    		String sessionId)
    {   	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"startCapturing\",\"params\":{\"sessionId\":\"%s\"}}", 
				commandId,
				sessionId);
    }

    // commandId: client generated id
    // sessionId: <from createSession>
    // withThumbnail: boolean
    public String readImageBlockMetadata(String commandId,
    		String sessionId,
    		boolean withThumbnail)
    {
    	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"readImageBlockMetadata\",\"params\":{\"sessionId\":\"%s\",\"withThumbnail\":%s}}", 
				commandId,
				sessionId,
				withThumbnail? "true" : "false");
    }
    

    // commandId: client generated id
    // sessionId: <from createSession>
    // withMetadata: boolean
    public String readImageBlock(String commandId,
    		String sessionId,
    		boolean withMetadata)
    {
    	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"readImageBlock\",\"params\":{\"sessionId\":\"%s\",\"withMetadata\":%s}}", 
				commandId,
				sessionId,
				withMetadata? "true" : "false");
    }

    // commandId: client generated id
    // sessionId: <from createSession>	
    // imageBlockNum: number of first block to remove
	// lastImageBlockNum: number of last block to remove
    public String releaseImageBlocks(String commandId,
    		String sessionId,
    		int imageBlockNum,
    		int lastImageBlockNum)
    {
    	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"releaseImageBlocks\",\"params\":{\"sessionId\":\"%s\",\"imageBlockNum\":%d,\"lastImageBlockNum\":%d}}", 
				commandId,
				sessionId,
				imageBlockNum,
				lastImageBlockNum);
    }
    

    // commandId: client generated id
    // sessionId: <from createSession>
    public String stopCapturing(String commandId,
    		String sessionId)
    {
    	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"stopCapturing\",\"params\":{\"sessionId\":\"%s\"}}", 
				commandId,
				sessionId);
    }
    

    // commandId: client generated id
    // sessionId: <from createSession>
    public String closeSession(String commandId,
    		String sessionId)
    {
    	
    	// json
		return String.format("{\"kind\":\"twainlocalscanner\",\"commandId\":\"%s\",\"method\":\"closeSession\",\"params\":{\"sessionId\":\"%s\"}}", 
				commandId,
				sessionId);
    }
    
}
