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
				new DbUser { name = "lincoln", password="1" ,email=""},
				new DbUser { name = "a2", password="1" ,email=""},
				new DbUser { name = "a3", password="1" ,email=""}
			};

			resources.ForEach(s => context.DbUsers.Add(s));
			context.SaveChanges();

			base.Seed(context);
		}
	}
}