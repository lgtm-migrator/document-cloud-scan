using System;
using System.Threading.Tasks;

namespace TwainCloudServer
{
	public abstract class EventBrokerClient : IDisposable, IMessageReceivedHandler
	{
		private MqttClient _mqttClient = null;
		protected bool bConnect;

		/// <summary>
		/// Occurs when asynchronious message from TWAIN Cloud is received.
		/// </summary>
		public event EventHandler<string> Received;

		public async Task Connect(string url, int port)
		{
			_mqttClient = new MqttClient();
			_mqttClient.ReceivedHandler = this;
			await _mqttClient.Connect(url, port);
			this.bConnect = true;
		}

		public bool isConnected() {

			if (null != _mqttClient)
			{
				return _mqttClient.isConnected();
			}

			return false;
		}

		public void Dispose()
		{
			if (null != _mqttClient) {
				_mqttClient.Dispose();
				_mqttClient = null;
			}
		}

		public void OnReceived(string msg)
		{
			if (this.bConnect)
				Received?.Invoke(this, msg);
		}

		/// <summary>
		/// Sends the specified message to the TWAIN Cloud.
		/// </summary>
		/// <param name="message">The message.</param>
		/// <returns></returns>
		/// <exception cref="InvalidOperationException">If the session is in disconnected state.</exception>
		public async Task Send(string topic, string message)
		{
			if (this.bConnect)
				await _mqttClient?.Send(topic, message);
		}

		public async Task Subscribe(string topic)
		{
			if (this.bConnect)
				await _mqttClient?.Subscribe(topic);
		}
		public async Task Unsubscribe(string topic)
		{
			if (this.bConnect)
   				await _mqttClient?.Unsubscribe(topic);
		}
		public virtual async Task Disconnect()
		{
			if (this.bConnect && _mqttClient != null)
			{
				_mqttClient.ReceivedHandler = null;
				await _mqttClient.Disconnect();
				_mqttClient.Dispose();
				_mqttClient = null;
				this.bConnect = false;
			}
		}
	}

}