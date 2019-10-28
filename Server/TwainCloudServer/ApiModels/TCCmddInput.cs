using Newtonsoft.Json;

namespace TwainCloudServer.ApiModels
{
	public class TCCmdInput
	{
		public string kind;
		public string commandId;
		public string method;

		//[JsonProperty("params")]
		public TCParameter @params;
	}
}