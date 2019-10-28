using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer
{
	/// <summary>
	/// Identifies the type of event that has caused the trace.
	/// </summary>
	public enum TCLogLevel
	{
		/// <summary>
		/// Disabled logging.
		/// </summary>
		Off,
		/// <summary>
		/// Fatal error or application crash.
		/// </summary>
		Critical,
		/// <summary>
		/// Recoverable error.
		/// </summary>
		Error,
		/// <summary>
		/// Noncritical problem.
		/// </summary>
		Warning,
		/// <summary>
		/// Informational message.
		/// </summary>
		Info,
		/// <summary>
		/// Debugging trace.
		/// </summary>
		Debug
	}
}