using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;

namespace TwainCloudServer.Controllers
{
	public class PollingController : ControllerBasecs
	{
		public IEnumerable<string> Get(string scannerId, string token)
		{
			return new string[] { "PollingController" + token };
		}
	}
}
