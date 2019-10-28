using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer
{
	/// <summary>
	/// Main Logger adapter for BankingOn product.
	/// </summary>
	public class TCLogger
	{
		/// <summary>
		/// Initializes a new instance of the <see cref="Logger"/> class.
		/// </summary>
		/// <param name="context">Type context for this logger instance.</param>
		protected TCLogger(Type context)
		{
			Context = context;
		}
		public Type Context { get; }

		/// <summary>
		/// Fabric method that returns new instance of the logger initialized 
		/// with context of the specified type.
		/// </summary>
		/// <typeparam name="T">
		/// Context of the logger.
		/// </typeparam>
		/// <returns>Configured instance of the logger.</returns>
		public static TCLogger GetLogger<T>()
		{
			return new TCLogger(typeof(T));
		}


		/// <summary>
		/// Writes an critical message to the log using specified message.
		/// </summary>
		/// <param name="message">The informative message to write.</param>
		public void LogCritical(string message)
		{
			LogMessage(TCLogLevel.Critical, message, null);
		}

		/// <summary>
		/// Writes an error message to the log using specified message.
		/// </summary>
		/// <param name="message">The informative message to write.</param>
		public void LogError(string message)
		{
			LogMessage(TCLogLevel.Error, message, null);
		}

		/// <summary>
		/// Writes a warning message to the log using specified message.
		/// </summary>
		/// <param name="message">The informative message to write.</param>
		public void LogWarning(string message)
		{
			LogMessage(TCLogLevel.Warning, message, null);
		}

		/// <summary>
		/// Writes an informational message to the log using specified message.
		/// </summary>
		/// <param name="message">The informative message to write.</param>
		public void LogInfo(string message)
		{
			LogMessage(TCLogLevel.Info, message, null);
		}

		/// <summary>
		/// Writes a verbose message to the log using specified message.
		/// </summary>
		/// <param name="message">The informative message to write.</param>
		public void LogDebug(string message)
		{
			LogMessage(TCLogLevel.Debug, message, null);
		}

		public void LogException(TCLogLevel level, Exception ex, string message)
		{
		}

		public void LogMessage(TCLogLevel level, string message, Dictionary<string, string> props)
		{
			System.Console.WriteLine(message);
			System.Diagnostics.Debug.WriteLine(message);
		}

	}
}