package com.dynamsoft;

import java.util.Map;

public class DLogger
{
	private DLogLevel level = DLogLevel.Info;
	public static void setLogLevel(DLogLevel level)
	{
		GetLogger().level = level;
	}
	
	public static DLogger GetLogger()
	{
		return new DLogger();
	}
	
	private DLogger() 
	{
		
	}

	public void LogCritical(String message)
	{
		LogMessage(DLogLevel.Critical, message, null);
	}

	public void LogError(String message)
	{
		LogMessage(DLogLevel.Error, message, null);
	}

	public void LogWarning(String message)
	{
		LogMessage(DLogLevel.Warning, message, null);
	}

	public void LogInfo(String message)
	{
		LogMessage(DLogLevel.Info, message, null);
	}

	public void LogDebug(String message)
	{
		LogMessage(DLogLevel.Debug, message, null);
	}

	public void LogException(DLogLevel level, Exception ex, String message)
	{
		LogMessage(DLogLevel.Debug, message, null);
		//if(level == this.level)
		{
			System.out.println(ex.getMessage());
		}
	}

	public void LogMessage(DLogLevel level, String message, Map<String, String> props)
	{
		//if(level == this.level)
		{
			System.out.println(message);
		}
	}
	
	public static boolean debug = true;

	public static void print(String string) {
		if(debug)
			System.out.print(string);
	}

	public static void println(String string) {
		if(debug)
			System.out.println(string);
	}
}