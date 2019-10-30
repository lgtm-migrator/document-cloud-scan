using TwainCloudServer.Models;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Web;

namespace TwainCloudServer
{
	public class AccountInitializer : DropCreateDatabaseIfModelChanges<AccountContext>
	{
		protected override void Seed(AccountContext context)
		{
			var resources = new List<DbUser>
			{
				new DbUser { name = "dynamsoft", password="1" ,email="support@dynamsoft.com"},
            };

			resources.ForEach(s => context.DbUsers.Add(s));
			context.SaveChanges();

			base.Seed(context);
		}
	}
}