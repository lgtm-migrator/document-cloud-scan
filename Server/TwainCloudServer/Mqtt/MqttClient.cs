using System;
using System.Diagnostics;
using System.Net;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using MQTTnet;
using MQTTnet.Client;
using MQTTnet.Client.Connecting;
using MQTTnet.Client.Disconnecting;
using MQTTnet.Client.Options;
using MQTTnet.Client.Receiving;
using MQTTnet.Diagnostics;

namespace TwainCloudServer
{
	/// <summary>
	/// TWAIN Cloud MQTT client.
	/// </summary>
	/// <seealso cref="System.IDisposable" />
	internal class MqttClient : IDisposable, IMqttApplicationMessageReceivedHandler, IMqttClientDisconnectedHandler
	{
		private static TCLogger Logger = TCLogger.GetLogger<MqttClient>();
        private static readonly Encoding DefaultMessageEncoding = Encoding.UTF8;

        private IMqttClient _client = null;
		private string _tcpServer = null;
		private int _tcpPort = 1883;
		
		public IMessageReceivedHandler ReceivedHandler;

		/// <summary>
		/// Initializes a new instance of the <see cref="MqttClient"/> class.
		/// </summary>
		public MqttClient()
		{
#if DEBUG
			// Write all trace messages to the console window.
			MqttNetGlobalLogger.LogMessagePublished += OnShowMsg;
#endif
		}
		private void OnShowMsg(object s, MqttNetLogMessagePublishedEventArgs e)
		{
			var trace = $">> [{e.TraceMessage.Timestamp:O}] [{e.TraceMessage.ThreadId}] [{e.TraceMessage.Source}] [{e.TraceMessage.Level}]: {e.TraceMessage.Message}";
			if (e.TraceMessage.Exception != null)
				trace += Environment.NewLine + e.TraceMessage.Exception.ToString();

			Debug.WriteLine(trace);
			Logger.LogDebug(trace);
		}

		/// <summary>
		/// Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
		/// </summary>
		public void Dispose()
		{
#if DEBUG
			// Write all trace messages to the console window.
			MqttNetGlobalLogger.LogMessagePublished -= OnShowMsg;
#endif

			if (_client != null)
			{
				_tcpServer = null;
				_client.DisconnectedHandler = null;
				_client.ApplicationMessageReceivedHandler = null;
				if(_client.IsConnected)
					_client.DisconnectAsync().Wait();
			}
        }

		/// <summary>
		/// Connects to MQTT broker.
		/// </summary>
		/// <param name="mqttUrl">The MQTT broker URL.</param>
		/// <returns></returns>
		public async Task Connect(String tcpServer, int port)
        {
			_tcpServer = tcpServer;
			_tcpPort = port;

			if (_client == null) {
				// Create a new MQTT client.
				_client = new MqttFactory().CreateMqttClient();
			}

			_client.DisconnectedHandler = this;
			_client.ApplicationMessageReceivedHandler = this;

			await ConnectMqttBroker(false);
        }

		public Task HandleApplicationMessageReceivedAsync(MqttApplicationMessageReceivedEventArgs e)
		{
			if (e.ApplicationMessage.Payload != null) {
				String msg = DefaultMessageEncoding.GetString(e.ApplicationMessage.Payload);
				return Task.Run(() => ReceivedHandler.OnReceived(msg));
			}
			return Task.Run(() => { });
		}

		public async Task HandleDisconnectedAsync(MqttClientDisconnectedEventArgs eventArgs)
		{
			Logger.LogDebug("Disconnected from server");

			// TODO: implement exponential backoff instead.
			await Task.Delay(TimeSpan.FromSeconds(2));

			try
			{
				await ConnectMqttBroker(true);
			}
			catch
			{
				Logger.LogDebug("Reconnection failed");
			}
		}

		/// <summary>
		/// Subscribes to the specified topic.
		/// </summary>
		/// <param name="topic">The topic.</param>
		/// <returns></returns>
		public async Task Subscribe(string topic)
        {
			if (this.isConnected())
			{
				Logger.LogDebug("Subscribing to topic: " + topic);
				// '#' is the wildcard to subscribe to anything under the 'root' topic
				// the QOS level here - I only partially understand why it has to be this level - it didn't seem to work at anything else.
				await _client.SubscribeAsync(topic);
			}
        }

		public async Task Unsubscribe(string topic)
		{
			if (this.isConnected())
			{
				Logger.LogDebug("UnsubscribeAsync to topic: " + topic);
				await _client.UnsubscribeAsync(new string[] { topic });
			}
		}
		public async Task Disconnect()
		{
			if (this.isConnected())
			{
				Logger.LogDebug("DisconnectAsync");
				await _client.DisconnectAsync();
			}
		}
		
		/// <summary>
		/// Publishes a message to the specified topic.
		/// </summary>
		/// <param name="topic">The topic.</param>
		/// <param name="message">The message.</param>
		/// <returns></returns>
		public async Task Send(string topic, string message)
        {
			if (this.isConnected()) {
				Logger.LogDebug("Publishing a message to topic: " + topic);
				Logger.LogDebug(message);
				await _client.PublishAsync(new MqttApplicationMessage
				{
					Topic = topic,
					Payload = DefaultMessageEncoding.GetBytes(message)
				});
			}
		}
		public bool isConnected()
		{
			return this._client != null && this._client.IsConnected;
		}

		private async Task<MqttClientAuthenticateResult> ConnectMqttBroker(bool isReconnected)
		{
			Logger.LogDebug("Connecting to broker");
			// A wild hack to ensure that HTTP connection is not closed.
			// See https://github.com/chkr1011/MQTTnet/issues/158 for details

			if (_client != null)
			{
				var defaultIdleTime = ServicePointManager.MaxServicePointIdleTime;
				ServicePointManager.MaxServicePointIdleTime = Timeout.Infinite;

				// Use WebSocket connection.
				IMqttClientOptions _options = new MqttClientOptionsBuilder()
					//.WithWebSocketServer(_tcpServer)
					//.WithTls()
					.WithCommunicationTimeout(new TimeSpan(0, 0, 10))   // 1 minutes
					//.WithCommunicationTimeout(new TimeSpan(0,1,0))	// 1 minutes
					.WithTcpServer(_tcpServer, _tcpPort)
					.WithClientId("twain-direct-proxy-" + Guid.NewGuid()) // TODO: define this constant somewhere
					.Build();

				ServicePointManager.MaxServicePointIdleTime = defaultIdleTime;

				return await _client.ConnectAsync(_options);
			}

			return await new Task<MqttClientAuthenticateResult>(() => { return null; });
		}


	}
}
