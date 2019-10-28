using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer.ApiModels
{
	public class TCImageBlock
	{
		public String blockId;
		public String scannerId;
		public String imageType;
		public byte[] data;
	}
}