using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer.ApiModels
{
	public class TCCloudMethod : TCMethodBase
	{
		// cloud methods
		public object claim;
		public object poll;
		public object refresh;
		public object register;
		public object scanners;
		public object signin;
	}
}