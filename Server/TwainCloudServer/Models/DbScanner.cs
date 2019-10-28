using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace TwainCloudServer.Models
{
	/// <summary>
	/// Scanner information fields.
	/// </summary>
	public class DbScanner : ScannerClaimBase
	{
		[Key]
		public int dbId { get; set; }

	}

}