package com.dynamsoft;

/// <summary>
/// Identifies the type of event that has caused the trace.
/// </summary>
public enum DLogLevel
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