using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer.ApiModels
{
	public class TCParameter
	{
		public string sessionId;
		public string sessionRevision;
		public object task;
		public Boolean withThumbnail;
		public Boolean withMetadata;
		public Int32 imageBlockNum;
		public Int32 lastImageBlockNum;
	}
}