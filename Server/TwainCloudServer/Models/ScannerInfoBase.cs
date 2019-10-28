using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer.Models
{
	public class ScannerInfoBase
	{
		public string version { get; set; }
		public string name { get; set; }
		public string description { get; set; }
		public string url { get; set; }
		public string type { get; set; }
		public string id { get; set; }  // scanner uuid
		public string device_state { get; set; }
		public string connection_state { get; set; }
		public string manufacturer { get; set; }
		public string model { get; set; }
		public string serial_number { get; set; }
		public string firmware { get; set; }
		public string uptime { get; set; }
		public string setup_url { get; set; }
		public string support_url { get; set; }
		public string update_url { get; set; }
		public string semantic_state { get; set; }
	}
}