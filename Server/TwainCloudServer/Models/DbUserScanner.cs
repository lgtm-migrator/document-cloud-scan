using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer.Models
{
	public class DbUserScanner
	{
		public int id { get; set; }
		public string name { get; set; }
		public string scannerId { get; set; }  // scanner uuid

	}
}