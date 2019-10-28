using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer.Models
{
	public class DbImageBlock
	{
		public int id { get; set; }
		public String blockId { get; set; }
		public String imageType { get; set; }
		public byte[] data { get; set; }
		public String scannerId { get; set; }
	}
}