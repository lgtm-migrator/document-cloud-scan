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
	public class RegisterController : ControllerBasecs
	{
		public TCScannerRegister Post(TCScannerRegisterInput scannerToRegister)
		{
			String newId = TokenProvider.getInstance().takeUUID();
			String registerToken = TokenProvider.getInstance().takeToken("_");

			DbScanner scannerInDb = new DbScanner();
			scannerInDb.name = scannerToRegister.name;
			scannerInDb.description = scannerToRegister.description;
			scannerInDb.manufacturer = scannerToRegister.manufacturer;
			scannerInDb.model = scannerToRegister.model;
			scannerInDb.serial_number = scannerToRegister.serial_number;
			scannerInDb.id = newId;
			scannerInDb.registerToken = registerToken;

			db.DbScanners.Add(scannerInDb);
			db.SaveChanges();

			String root = HttpContext.Current.Request.Url.GetLeftPart(UriPartial.Authority);

			TCScannerRegister ret = new TCScannerRegister();
			ret.scannerId = newId;

			String queryString = String.Format("?scannerId={0}&token={1}", newId, registerToken);
			ret.pollingUrl = root + "/api/polling" + queryString;
			ret.inviteUrl = root + "/api/claim" + queryString;

			return ret;
		}
	}
}
