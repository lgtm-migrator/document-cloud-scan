
namespace TwainCloudServer.ApiModels
{

	/// <summary>
	/// tokens required to access TWAIN Cloud infrastructure.
	/// </summary>
	public class TCTokens
	{
		/// <summary>
		/// get authorization token which should be passed along with each TWAIN Cloud request.
		/// </summary>
		public string token;
		/// <summary>
		/// get refresh token which should be passed along with each TWAIN Cloud request.
		/// </summary>
		public string refreshToken;
	}
}