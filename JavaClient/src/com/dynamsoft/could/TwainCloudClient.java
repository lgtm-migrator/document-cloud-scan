package com.dynamsoft.could;

import java.util.Date;

import com.dynamsoft.DLogger;
import com.dynamsoft.could.entity.TCScannerInfo;
import com.dynamsoft.could.entity.TCScannerRegister;
import com.dynamsoft.could.entity.TCTokens;
import com.dynamsoft.could.entity.TCUserInfo;
import com.dynamsoft.could.json.JsonToEntity;

 
public class TwainCloudClient extends TwainCloudClientBase {

	public boolean bOut = false;
	
	public TwainCloudClient(String httpServerUrl, TCTokens authorizationToken, ICloudCallback cloudCallback) {
		super(httpServerUrl, cloudCallback);
		super.authorizationToken = authorizationToken; 
    }

	public TCUserInfo GetUserInfo() {

		// get topic from $/user
        String jsonUserInfo = httpGet(urlProvider.userInfo());
        
        // parse jsonUserInfo
        return JsonToEntity.getParser().parseUserInfo(jsonUserInfo);
	}
	
	public String uploadImageBlock(String scannerId, byte[] data)
	{
		// post to $/scanners/{scan_id}/blocks
		String postBlockUrl = urlProvider.blocks(scannerId);
		
        String imageBlockUrl = httpPostFile(postBlockUrl, data);
        return imageBlockUrl;
	}

	
	public String downloadBlock(String scannerId, String blockId)
	{
		// post to $/scanners/{scan_id}/blocks
        String jsonBlockInfo = httpGet(urlProvider.block(scannerId, blockId));
        
        return jsonBlockInfo;
	}
	

	public TCScannerRegister register(String scannerName)
	{
		Date date = new Date();
		String random = String.valueOf(date.getTime());
		String name = scannerName;
		
		String description = ""; //"TC desc" + random;
		String manufacturer = ""; //"TC manufacturer" + random;
		String model = ""; //"TC model" + random;
		String serial_number = ""; //"TC serial" + random;
		
		String strPostData = urlProvider.registerScannerBody(name,
				 description,
				 manufacturer,
				 model,
				 serial_number);
		
		String jsonRegisterResponse = httpPostJson(urlProvider.registerScanner(), strPostData);

		TCScannerRegister register = JsonToEntity.getParser().parseRegister(jsonRegisterResponse);
		
		if(DLogger.debug) {
			DLogger.println("jsonRegisterResponse is:" + jsonRegisterResponse);
			DLogger.print("scannerId:");       DLogger.println(register.scannerId);
			DLogger.print("pollingUrl:");      DLogger.println(register.pollingUrl);
			DLogger.print("inviteUrl:");       DLogger.println(register.inviteUrl);
		}
		
		return register;
	}
    

	public void claim(String claimUrl) {

		String[] aryUrl = claimUrl.split("\\?");
		if(aryUrl.length == 2) {
			String jsonRegisterResponse = httpPost(aryUrl[0], aryUrl[1]);

			TCTokens tokens = new TCTokens();
			TCScannerInfo scannerInfo = JsonToEntity.getParser().parseScannerInfo(jsonRegisterResponse, tokens);
		}				
	}
    


	public void deleteScanner(String scannerId) {

		String strUrl = urlProvider.scannerDel(scannerId);
		String jsonDeleteScannerResponse = httpDelete(strUrl, null);

		if(DLogger.debug) {
			DLogger.println("jsonDeleteScannerResponse is:" + jsonDeleteScannerResponse);
		}
			
	}
    
    private void UpdateTokens(TCTokens tokens)
    {
        this.authorizationToken = tokens;
        // OnTokensRefreshed(new TokensRefreshedEventArgs { Tokens = _tokens });
    }
    
}