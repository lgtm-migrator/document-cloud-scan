using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Http;

namespace TwainCloudServer.Controllers
{
	public class RefreshController : ControllerBasecs
	{
		public IEnumerable<string> Post(String token)
		{
			return new string[] { "RefreshController" + token };
		}
	}
}
