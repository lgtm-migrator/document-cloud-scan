using System;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;
using System.Web.Http;
using TwainCloudServer.Models;
using TwainCloudServer.ApiModels;


namespace TwainCloudServer.Controllers
{
	public class UserController : ControllerBasecs
	{
		public TCUserInfo Get()
		{
			String username;
			String XPrivetToken;	// token for scanner, here is ""
			var token = GetAuthen(out username, out XPrivetToken);
			if (token != false)
			{
				string mqttServer = null;

				var request = base.GetHttpRequest();
				String protocol = request.QueryString["protocol"];
				if (!String.IsNullOrEmpty(protocol) && protocol == "websocket")
					mqttServer = System.Configuration.ConfigurationManager.AppSettings["wsmqtt"];
				else {
					String mqttServerDomain = System.Configuration.ConfigurationManager.AppSettings["mqttServer"];
					String mqttPort = System.Configuration.ConfigurationManager.AppSettings["mqttPort"];
					mqttServer = String.Format("tcp://{0}:{1}", mqttServerDomain, mqttPort);

				}

				TCUserInfo userInfo = new TCUserInfo();
				userInfo.url = mqttServer;
				userInfo.topic = "dynamsoft/clouduser/" + username;
				userInfo.type = "mqtt";
				return userInfo;
			}

			return null;
		}
		
		// response user token
		public TCTokens Post()
		{
			HttpRequestBase request = GetHttpRequest();
			var name = request["name"];
			var password = request["password"];

			var user = db.DbUsers.FirstOrDefault(u => (u.name == name.ToLower()));

			if (user != null) {
				if (user.password == password) {

					long now = DateTime.Now.Ticks;
					String strAuthen = TokenProvider.getInstance().takeToken(user.name);

					DateTime dt = DateTime.Now;
					dt.AddHours(12);
					String strRefreshToken = TokenProvider.getInstance().takeToken(user.name, dt.Ticks);

					return new TCTokens{ token=strAuthen, refreshToken=strRefreshToken };
				}
			}
			return null;
		}
	}
}
