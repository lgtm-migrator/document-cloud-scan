using System;
using System.Data.Entity;
using System.Security.Cryptography;
using System.Text;
using System.Web;
using System.Web.Http;
using System.Web.Mvc;
using System.Web.Routing;

namespace TwainCloudServer
{
    public class WebApiApplication : System.Web.HttpApplication
	{
		protected void Application_Start()
        {
			AreaRegistration.RegisterAllAreas();
			GlobalConfiguration.Configure(WebApiConfig.Register);
			FilterConfig.RegisterGlobalFilters(GlobalFilters.Filters);
			//RouteConfig.RegisterRoutes(RouteTable.Routes);

			Database.SetInitializer<AccountContext>(new AccountInitializer());

			// return json 
			GlobalConfiguration.Configuration.Formatters.XmlFormatter.SupportedMediaTypes.Clear();

			GlobalConfiguration.Configuration.Formatters
				.JsonFormatter.SerializerSettings = new Newtonsoft.Json.JsonSerializerSettings()
				{
					NullValueHandling = Newtonsoft.Json.NullValueHandling.Ignore
				};
		}

		public override void Init()
		{
			this.AuthenticateRequest += WebApiApplication_AuthenticateRequest;
			base.Init();
		}

		void WebApiApplication_AuthenticateRequest(object sender, EventArgs e)
		{
			HttpContext.Current.SetSessionStateBehavior(System.Web.SessionState.SessionStateBehavior.Required);
		}
	}
}
