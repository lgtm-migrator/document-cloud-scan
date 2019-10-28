
namespace TwainCloudServer.ApiModels
{

	/// <summary>
	/// Scanner status response payload.
	/// </summary>
	public class TCScannerStatus
	{
		/// <summary>
		/// Gets or sets the URL to use to connect to TWAIN Cloud MQTT broker.
		/// </summary>
		public string url;

		/// <summary>
		/// Gets or sets the name of MQTT topic to use for incoming messages.
		/// </summary>
		public string requestTopic;

		/// <summary>
		/// Gets or sets the name of MQTT topic to use for outgoing messages.
		/// </summary>
		public string responseTopic;
	}
}