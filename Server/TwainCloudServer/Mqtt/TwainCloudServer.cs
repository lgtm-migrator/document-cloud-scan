using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using TwainCloudServer.Utils;
using System.Collections;

namespace TwainCloudServer
{
	public class TwainCloudServer : EventBrokerClient
	{
		private bool closed;
		private string username;
		private string lastMsg;
		private Dictionary<String,String> mapMsg;
		private TCLogger Logger = TCLogger.GetLogger<TwainCloudServer>();

		public TwainCloudServer(string username)
		{
			this.closed = false;
			this.lastMsg = null;
			this.username = username;
			this.mapMsg = new Dictionary<String, String>();
		}
		public void OnMsgReceived(object sender, string receiveMsg)
		{
			Logger.LogDebug(receiveMsg);

			if (receiveMsg == "close")	// not normal closed
			{
				Task.Run(async () =>
				{
					await Disconnect();
				});
				return;
			}

			String cmdId = StringUtil.GetCmdId(receiveMsg);
			if (cmdId == null || cmdId.Length == 0)
			{
				Logger.LogDebug("set as last message");
				lastMsg = receiveMsg;
			}
			else
			{
				Logger.LogDebug("set to cmdId:" + cmdId);
				mapMsg[cmdId] = receiveMsg;
			}

		}
		public async Task Connect()
		{
			string mqttServer = System.Configuration.ConfigurationManager.AppSettings["mqttServer"];
			string mqttPort = System.Configuration.ConfigurationManager.AppSettings["mqttPort"];
			string topic = username; // user name as topic

			await base.Connect(mqttServer, Int32.Parse(mqttPort));
			await base.Subscribe(topic);
			base.Received += OnMsgReceived;
		}

		public string GetMessage(String cmdId)
		{
			if (this.closed)
				return "";

			if (null == cmdId || cmdId.Length == 0)
			{
				return this.lastMsg;
			}
			else
			{
				if(this.mapMsg.ContainsKey(cmdId))
					return this.mapMsg[cmdId];
				return null;
			}
		}

		public override async Task Disconnect()
		{
			if (!this.closed)
			{
				Logger.LogDebug("Disconnect Mqtt OK.");
				base.Received -= OnMsgReceived;
				string topic = username; // user name as topic
				await base.Unsubscribe(topic);
				await base.Disconnect();
				this.closed = true;
				this.mapMsg.Clear();
			}
			else {
				Logger.LogDebug("Disconnect Mqtt before.");
			}
		}

		public void ClearLastMsg()
		{
			lastMsg = null;
		}
	}
}