using Newtonsoft.Json;
using System;

namespace TwainCloudServer.ApiModels
{
	public class TCCmdResponse
	{
		public String kind;
		public String commandId;
		public String method;
		public TCCmdResResults results;
	}
}