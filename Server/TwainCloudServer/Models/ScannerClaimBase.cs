namespace TwainCloudServer.Models
{
	public class ScannerClaimBase : ScannerInfoBase
	{
		public string clientId { get; set; }
		public string registerToken { get; set; }
	}
}