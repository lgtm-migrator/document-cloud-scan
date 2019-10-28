using System;

namespace TwainCloudServer.ApiModels
{
	public class TCMethodBase
	{
		public object closeSession;
		public object createSession;
		public object getSession;
		public object readImageBlock;
		public object readImageBlockMetadata;
		public object releaseImageBlocks;
		public object sendTask;
		public object startCapturing;
		public object stopCapturing;
		public object waitForEvents;
	}
}