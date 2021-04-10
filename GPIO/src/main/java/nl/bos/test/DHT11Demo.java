package nl.bos.test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DHT11Demo {

    public static void main(String[] args) throws InterruptedException {

        DHT11 dht11 = new DHT11(29); //use GPIO pin 15

        while (true) {
            DHT11Result result = dht11.read();
            if (result.isValid()) {
                System.out.println("Last valid input: " + new Date());
                System.out.printf("Temperature: %.1f C\n", result.getTemperature());
                System.out.printf("Humidity:    %.1f %%\n", result.getHumidity());
            }
            TimeUnit.SECONDS.sleep(10);
        }
    }
}