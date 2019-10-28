using System.Web.Http;
using Ninject;
using TwainCloudServer.Models;
using WebApiContrib.IoC.Ninject;


namespace TwainCloudServer
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
		{
			//config.Filters.Add(new ValidateAttribute());

			config.MapHttpAttributeRoutes();

			config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );

		}
    }
}
