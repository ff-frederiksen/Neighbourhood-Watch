package communication;

import java.util.Optional;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import nwa_functionality.NWA;



public class Mqtt implements MqttCallback {
	MqttClient client;
	MqttConnectOptions options;
	String topic;
	Gson gson;
	
	public Mqtt(String ip, String topic) {
		try {
			gson = new GsonBuilder().setPrettyPrinting().create();
			client = new MqttClient(ip, MqttClient.generateClientId());
			
			options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(false);
			options.setConnectionTimeout(10);
			
			
	        client.setCallback((MqttCallback) this);
	        client.connect(options);
	        this.topic=topic;
	        client.subscribe(topic);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void connectionLost(Throwable throwable) {
	    System.out.println("Connection to MQTT broker lost! Attempting to reconnect...");
	    throwable.printStackTrace();
	  }
	
	@Override
	public void messageArrived(String topic, MqttMessage message)  {
		System.out.println("Message received:\n\t"+ new String(message.getPayload()) );
		
		JsonObject data = new JsonParser().parse(new String(message.getPayload())).getAsJsonObject();
		String devEUI = data.get("devEUI").getAsString();
		
		Optional<JsonObject> result = NWA.getInstance().handleRequest(data);
		
//		System.out.println(result);

		if (result.isPresent()) {
			System.out.println(result.get().toString());
			NWA.getInstance().transmitMessage(result.get(), devEUI);
			
		}
		
	}
	
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Message delivered: "+ token.toString());
		
	}
	
	public boolean disconnectClient() {
		try {
			client.disconnect();
			return true;
		} catch (MqttException e) {
			return false;
		}
	}
	
	
}
