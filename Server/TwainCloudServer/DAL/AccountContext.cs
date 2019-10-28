using TwainCloudServer.Models;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Data.Entity.ModelConfiguration.Conventions;
using System.Linq;
using System.Threading.Tasks;

namespace TwainCloudServer
{
	public class AccountContext : DbContext
	{
		public AccountContext() : base("AccountContext")
		{ }

		public DbSet<DbUser> DbUsers {get; set; }
		public DbSet<DbScanner> DbScanners { get; set; }

		public DbSet<DbUserScanner> DbUserScanners { get; set; }
		public DbSet<DbImageBlock> DbImageBlocks { get; set; }
		protected override void OnModelCreating(DbModelBuilder modelBuilder)
		{
			modelBuilder.Conventions.Remove<PluralizingTableNameConvention>();
		}


	}
}
