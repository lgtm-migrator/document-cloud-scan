package com.dynamsoft;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

public class IDProvider {

	private static IDProvider _instance = null;
	public static IDProvider getInstance() {
		if(null == _instance) {
			_instance = new IDProvider();
		}
		return _instance;
	}

    private final String m_szDeviceSecret = "SkBt07NZmTppKVTjllBclvJd";
    
	private IDProvider() {
	}
	
	public long getRamdomIDAsLong()
	{
    	long cmdId = UUID.randomUUID().getLeastSignificantBits();
    	if(cmdId<0)
    		cmdId = cmdId & 0x7FFFFFFFL;

    	return cmdId;
	}
	
    public String createXPrivetToken(long a_lTicks)
    {
        long lTicks;

        if (a_lTicks > 0)
        {
            lTicks = a_lTicks;
        }

        else
        {
            lTicks = new Date().getTime();
        }

        String szTmp = m_szDeviceSecret + ":" + lTicks;
       	return String.format("%s:%d", DigestUtils.sha256Hex(szTmp.getBytes()), lTicks);
    }

	public boolean checkToken(String tokenIn)
	{
		boolean bValid = false;

		String[] arr = tokenIn.split(":");
		if (arr.length == 2)
		{
			long lTicksFromToken = Long.parseLong(arr[1]);
			if (!arr[0].isEmpty() && lTicksFromToken > 0)
			{
				String mustBe = this.m_szDeviceSecret + ":" + arr[1];
				
				mustBe = DigestUtils.sha256Hex(mustBe.getBytes());
				
				if (mustBe.equals(arr[0]))
				{
					long lTicksNow = new Date().getTime();
					// 12 hours
					if ((lTicksNow - lTicksFromToken) < 432000000000L)
					{
						bValid = true;
					}
				}
			}
		}

		return bValid;
	}
	
    public boolean checkXPrivetToken(String token) {
    	
    	boolean ret = false;
    	
    	return ret;
    }
}
