package nl.bos.test;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class ControlGPIOExample {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("GPIO Blinking example started...");

        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalOutput ledWhite = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29);
        final GpioPinDigitalOutput ledRed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_03, PinPullResistance.PULL_DOWN);

        ledWhite.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        ledRed.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);

        myButton.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isHigh()) {
                System.out.println("Button push");
                ledRed.blink(200);
            } else {
                System.out.println("Button release");
                ledRed.blink(1000);
            }
        });

        ledWhite.blink(500, 20000);
        ledRed.blink(1000);

        System.out.println(" ... the LED will continue blinking until the program is done.");
        Thread.sleep(30000);
        gpio.shutdown();
    }
}
