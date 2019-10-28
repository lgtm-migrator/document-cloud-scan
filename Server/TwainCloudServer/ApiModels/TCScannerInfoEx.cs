using System.Collections.Generic;

namespace TwainCloudServer.ApiModels
{
	public class TCScannerInfoEx : TCScannerInfo
	{
		public TCLocalMethod localMethods;
		public List<TCCloud> clouds;
		public string error;
	}
}