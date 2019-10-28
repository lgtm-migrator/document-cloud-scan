using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer.Models
{
	public interface IScannersRepository
	{
		IEnumerable<DbScanner> Get();
		bool TryGet(int id, out DbScanner comment);
		DbScanner Add(DbScanner comment);
		bool Delete(int id);
		bool Update(DbScanner comment);
	}
}