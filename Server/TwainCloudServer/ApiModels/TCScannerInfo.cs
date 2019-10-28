using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using TwainCloudServer.Models;

namespace TwainCloudServer.ApiModels
{
	public class TCScannerInfo : ScannerInfoBase
	{

		[JsonProperty("x-privet-token")]
		public string XPrivetToken;
		[JsonProperty("sessionid")]
		public string sessionid;
		public List<String> api;

	}
}