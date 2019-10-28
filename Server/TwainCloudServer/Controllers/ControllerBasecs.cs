using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.Http;
using TwainCloudServer.Models;

namespace TwainCloudServer.Controllers
{
	public class ControllerBasecs : ApiController
	{
		protected AccountContext db = new AccountContext();

		protected bool GetAuthen(out String username)
		{
			HttpRequestBase request = GetHttpRequest();
			String strAuthen = request.Headers["authorization"];

			username = null;
			if (String.IsNullOrEmpty(strAuthen))
				return false;

			Boolean bExpired = true;
			bool isValid = TokenProvider.getInstance().checkToken(strAuthen, out username, out bExpired);
			if(isValid && !bExpired)
				return true;

			return false;
		}

		protected bool GetAuthen(out String username, out String XPrivetToken)
		{
			HttpRequestBase request = GetHttpRequest();
			String strAuthen = request.Headers["authorization"];
			XPrivetToken = request.Headers["x-privet-token"];

			username = null;
			if (String.IsNullOrEmpty(strAuthen))
				return false;

			Boolean bExpired = true;
			bool isValid = TokenProvider.getInstance().checkToken(strAuthen, out username, out bExpired);
			if (isValid && !bExpired)
				return true;

			return false;
		}
		
		protected HttpRequestBase GetHttpRequest()
		{
			HttpContextBase context = (HttpContextBase)Request.Properties["MS_HttpContext"];
			return context.Request;
		}

		protected String GetHeadersAsString()
		{
			StringBuilder s = new StringBuilder();

			HttpRequestBase request = GetHttpRequest();
			string XPrivetToken = request.Headers["x-privet-token"];

			s.Append("x-privet-token");
			s.Append(":");
			s.Append(XPrivetToken);

			return s.ToString();
		}

		protected String GetPostDataAsString()
		{
			var request = this.GetHttpRequest();
			return new StreamReader(request.InputStream).ReadToEnd();
		}

		
	}
}