package nl.bos;

import org.eclipse.paho.client.mqttv3.*;

public class Subscribe {
    public static void main(String[] args) throws MqttException {
        MqttClient client=new MqttClient("tcp://192.168.2.114:1883", MqttClient.generateClientId());
        client.setCallback( new SimpleMqttCallBack() );
        client.connect();
        client.subscribe("test/java");
    }

    private static class SimpleMqttCallBack implements MqttCallback {
        public void connectionLost(Throwable throwable) {
            System.out.println("Connection to MQTT broker lost!");
        }

        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            System.out.println("Message received:\n\t"+ new String(mqttMessage.getPayload()) );
        }

        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            // not used in this example
        }
    }
}
