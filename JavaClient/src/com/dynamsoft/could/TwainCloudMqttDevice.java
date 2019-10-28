package com.dynamsoft.could;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.dynamsoft.DLogger;

public class TwainCloudMqttDevice {

	private MqttClient mqttClient;
	private String topic;
	
	public TwainCloudMqttDevice(String mqttServer) throws MqttSecurityException, MqttException
	{
		String clientId = "TwainCloudMqttClient";
		this.mqttClient = new MqttClient(mqttServer, clientId, new MemoryPersistence());
		

        DLogger.println("Connecting to broker: " + mqttServer);
		// mqtt options
		MqttConnectOptions options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setConnectionTimeout(100);
 
		this.mqttClient.connect(options);
		this.topic = null;

        DLogger.println("Connected");
	}

    public void send(String topic, MqttMessage message)
    {
        if(this.mqttClient.isConnected()) {
			try {
		        DLogger.println("Publishing topic: " + topic);
		        DLogger.println("   --> message: " + message);
				this.mqttClient.publish(topic, message);
	            DLogger.println("Message published");
			} catch (MqttPersistenceException e) {
			} catch (MqttException e) {
			}        	
        }
    }

	public void close() {
		
		try {
			this.unsubscribe();
			this.mqttClient.disconnect();
            DLogger.println("Disconnected");
		} catch (MqttException e) {
		}
		
		try {
			this.mqttClient.close();
            DLogger.println("close");
		} catch (MqttException me) {
			if(DLogger.debug)
			{
	            DLogger.println("reason "+me.getReasonCode());
	            DLogger.println("msg "+me.getMessage());
	            DLogger.println("loc "+me.getLocalizedMessage());
	            DLogger.println("cause "+me.getCause());
	            DLogger.println("excep "+me);
			}
		}
	}

	public void subscribe(String topic, int qos, IMqttMessageListener listener) throws MqttException {
		if(null != this.topic) {
			this.mqttClient.unsubscribe(this.topic);
		}
		
		this.topic = topic;
		this.mqttClient.subscribe(topic, qos, listener);
	}

	public void unsubscribe() throws MqttException {
		if(null != this.topic) {
			this.mqttClient.unsubscribe(this.topic);
		}
		this.topic = null;
	}

}
