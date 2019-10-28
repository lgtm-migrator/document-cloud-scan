using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using TwainCloudServer.ApiModels;
using TwainCloudServer.Models;

namespace TwainCloudServer.Controllers
{
	public class ClaimController : ControllerBasecs
	{
		private void FillScannerInfoFromDb(TCScannerClaim scannerInfo, DbScanner scannerInDb)
		{
			scannerInfo.id = scannerInDb.id;
			scannerInfo.model = scannerInDb.model;
			scannerInfo.support_url = scannerInDb.support_url;
			scannerInfo.version = scannerInDb.version;
			scannerInfo.setup_url = scannerInDb.setup_url;
			scannerInfo.uptime = scannerInDb.uptime;
			scannerInfo.name = scannerInDb.name;
			scannerInfo.clientId = scannerInDb.clientId;
			scannerInfo.semantic_state = scannerInDb.semantic_state;
			scannerInfo.serial_number = scannerInDb.serial_number;
			scannerInfo.manufacturer = scannerInDb.manufacturer;
			scannerInfo.firmware = scannerInDb.firmware;
			scannerInfo.connection_state = scannerInDb.connection_state;
			scannerInfo.device_state = scannerInDb.device_state;
			scannerInfo.description = scannerInDb.description;
			scannerInfo.update_url = scannerInDb.update_url;
			scannerInfo.type = scannerInDb.type;
		}

		private TCScannerClaim UserAddScanner(string scannerId, string token) {

			String username;
			var tokens = GetAuthen(out username);
			if (false == tokens) {
				// Error: invalid user token
				return null;
			}

			// link user & scanner
			DbScanner scannerInDb = db.DbScanners.FirstOrDefault(u => (u.id == scannerId));
			if (scannerInDb != null && scannerInDb.registerToken == token)
			{
				String accessToken = TokenProvider.getInstance().takeToken(username);
				String refreshToken = TokenProvider.getInstance().takeToken(username);

				DbUserScanner userScanner = new DbUserScanner();
				userScanner.name = username;
				userScanner.scannerId = scannerInDb.id;

				db.DbUserScanners.Add(userScanner);
				db.SaveChanges();

				//string mqttServer = System.Configuration.ConfigurationManager.AppSettings["mqtt"];
				TCScannerClaim scanner = new TCScannerClaim();
				FillScannerInfoFromDb(scanner, scannerInDb);
				return scanner;
			}

			// Error: invalid scanner id & scanner token
			return null;
		}
		public TCScannerClaim Get(string scannerId, string token)
		{
			return UserAddScanner(scannerId, token);
		}

		public TCScannerClaim Post([FromBody] string value)
		{
			HttpContextBase context = (HttpContextBase)Request.Properties["MS_HttpContext"];
			HttpRequestBase request = context.Request;
			var scannerId = request["scannerId"];
			var registToken = request["token"];

			return UserAddScanner(scannerId, registToken);
		}
	}
}
