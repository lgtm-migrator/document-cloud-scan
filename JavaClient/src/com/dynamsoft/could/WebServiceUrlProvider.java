package com.dynamsoft.could;

public class WebServiceUrlProvider {

	private static WebServiceUrlProvider instance;
	public static WebServiceUrlProvider getInstance(String s)
	{
		if(null == instance) {
			instance = new WebServiceUrlProvider(s);
		}
		
		return instance;
	}
	
	private String webServiceRoot;
	private WebServiceUrlProvider(String webServiceRoot)
	{
		this.webServiceRoot = webServiceRoot;
	}
	
    public String userInfo()
    {
		return String.format("%s/api/user", this.webServiceRoot);
    }

    // v2.0 POST Body:0
    public String refreshToken(String refreshToken)
    {
		return String.format("%s/api/refresh/%s", this.webServiceRoot, refreshToken);
    }

    // v2.0 POST
    public String registerScanner()
    {
		return String.format("%s/api/register", this.webServiceRoot);
    }
    
    public String registerScannerBody(
    		String name,
    		String description,
    		String manufacturer,
    		String model,
    		String serial_number)
    {   	
    	// json
		return String.format("{\"name\":\"%s\",\"description\":\"%s\",\"type\":\"twaindirect\",\"manufacturer\":\"%s\",\"model\":\"%s\",\"serial_number\":\"%s\"}", 
				name,
	    		description,
	    		manufacturer,
	    		model,
	    		serial_number	
				);
    }

    // v2.0 POST
    // Content-Type: application/x-www-form-urlencoded; charset=UTF-8 
    public String claimScanner()
    {
		return String.format("%s/api/claim", this.webServiceRoot);
    }
    
    public String claimScannerBody(String scannerId, String registrationToken)
    {
		return String.format("scannerId=%s&registrationToken=%s", scannerId, registrationToken);
    }

    // GET -- scanners Token
    public String scanners()
    {
		return String.format("%s/api/scanners", this.webServiceRoot);
    }

    // GET -- scanner Token
    public String scannerToken(String scannerId)
    {
		return String.format("%s/api/scanners/%s", this.webServiceRoot, scannerId);
    }

    // DELETE scanner
    public String scannerDel(String scannerId)
    {
		return String.format("%s/api/scanners/%s", this.webServiceRoot, scannerId);
    }
    
    // GET -- get scanner info
    // x-privet-token:""
    public String scannerInfo(String scannerId)
    {
		return String.format("%s/api/scanners/%s/info", this.webServiceRoot, scannerId);
    }

    // GET -- get scanner info ex
    // x-privet-token:""
    public String scannerInfoEx(String scannerId)
    {
		return String.format("%s/api/scanners/%s/infoex", this.webServiceRoot, scannerId);
    }
    
    // POST  using WebServiceUrlProvider to get post body
    // x-privet-token: <from info | infoex command>
    public String session(String scannerId)
    {
		return String.format("%s/api/scanners/%s/twaindirect/session", this.webServiceRoot, scannerId);
    }
    
    // upload image blocks OR thumbnail blocks
    // POST
    // Content-Type: application/octet-stream
    public String blocks(String scannerId)
    {
		return String.format("%s/api/scanners/%s/blocks", this.webServiceRoot, scannerId);
    }
    
    // GET
    public String block(String scannerId, String blockId)
    {
		return String.format("%s/api/scanners/%s/blocks/%s", this.webServiceRoot, scannerId, blockId);
    }
}
