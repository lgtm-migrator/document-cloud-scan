using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using TwainCloudServer.ApiModels;

namespace TwainCloudServer.Utils
{
	public class MqttResult
	{
		public String result;
		public bool bTimeout;
		public String errorString;
	}
	public class MqttPool
	{
		private TCLogger Logger = TCLogger.GetLogger<MqttPool>();

		private Dictionary<String, TwainCloudServer> map;
		private static MqttPool _instance;
		public static MqttPool getInstance() {
			if (_instance == null)
				_instance = new MqttPool();
			return _instance;
		}
		private MqttPool() {
			map = new Dictionary<String, TwainCloudServer>();
		}

		public TwainCloudServer GetMqttServer(String username)
		{
			if (map.ContainsKey(username))
				return map[username];
			return null;
		}

		public MqttResult SendMqttMessage(String id, String username, String sendMsg, String cmdId)
		{
			MqttResult ret = null;
			this.Logger.LogDebug("start sendMsg :" + sendMsg);
			Task.Run(async () =>
			{
				try
				{
					ret = await SendMqttMessageAsync(id, username, sendMsg, cmdId);
				} catch (Exception e) {
					this.Logger.LogError(e.Message);
				}
			}).Wait();

			this.Logger.LogDebug("sendMsg ok:" + sendMsg);

			return ret;
		}

		private async Task<MqttResult> SendMqttMessageAsync(String id, String username, String sendMsg, String cmdId) {

			TwainCloudServer tcs = null;

			if (map.ContainsKey(username)) {
				tcs = map[username];
			}

			if (null == tcs || !tcs.isConnected())
			{
				tcs = new TwainCloudServer(StringUtil.GetTopic_RecieveMsgFromDevice(username));
				map[username] = tcs;

				await tcs.Connect();
			}

			if (cmdId == null || cmdId.Length == 0) {
				tcs.ClearLastMsg();
			}

			DateTime startSend = DateTime.Now;
			TimeSpan timeoutSpan = new TimeSpan(0, 0, 30);  // 30s
			
			if (sendMsg.IndexOf("x-privet-token:\\\"\\\"") >0) {
				timeoutSpan = new TimeSpan(0, 0, 5);  // 5s
			}

			await tcs.Send(StringUtil.GetTopic_SendMsgToDevice(id), sendMsg);

			MqttResult ret = new MqttResult();

			ret.bTimeout = false;
			while (tcs.GetMessage(cmdId) == null)
			{
				if (DateTime.Now - startSend > timeoutSpan) {
					// timeout
					ret.bTimeout = true;
					ret.errorString = "timed out.";
					break;
				}
				await Task.Delay(100);
			}

			ret.result = tcs.GetMessage(cmdId);
			Logger.LogDebug("Get Message:" + ret);

			return ret;
		}

		public void Close(String username)
		{
			Logger.LogDebug("Close Mqtt:" + username);

			TwainCloudServer tcs = null;
			if (!map.ContainsKey(username))
			{
				return;
			}
			tcs = map[username];

			if (null != tcs && tcs.isConnected()) {

				Task.Run(async () =>
				{
					await tcs.Disconnect();
				}).Wait();
				tcs.Dispose();
			}
			else if (tcs.isConnected())
			{
				tcs.Dispose();
			}

			map.Remove(username);
		}
	}
}