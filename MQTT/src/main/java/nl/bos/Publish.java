package nl.bos;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Publish {
    public static void main(String[] args) throws MqttException {
        MqttClient client = new MqttClient("tcp://192.168.2.114:1883", MqttClient.generateClientId());
        client.connect();
        MqttMessage message = new MqttMessage();
        message.setPayload("Hello world from Java".getBytes());
        client.publish("test/java", message);
        client.disconnect();
    }
}
