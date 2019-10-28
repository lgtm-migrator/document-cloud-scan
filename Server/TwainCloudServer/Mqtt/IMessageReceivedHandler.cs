using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace TwainCloudServer
{
	public interface IMessageReceivedHandler
	{
		void OnReceived(string msg);
	}
}