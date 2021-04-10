package nl.bos.test;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.Random;

public class ControlGPIOExample3 {

    private static boolean run = true;
    private static final GpioController GPIO = GpioFactory.getInstance();

    public ControlGPIOExample3() throws InterruptedException {
        GpioPinDigitalOutput pinRed = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_23);
        GpioPinDigitalOutput pinGreen = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_24);
        GpioPinDigitalOutput pinBlue = GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_25);
        GpioPinDigitalInput myButton = GPIO.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);

        pinRed.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        pinGreen.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        pinBlue.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);

        myButton.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isHigh()) {
                run = false;
            } else {
                System.out.println("Button release");
                // do nothing
            }
        });

        while (run) {
            pinRed.pulse(new Random().nextInt(900) + 100);
            pinGreen.pulse(new Random().nextInt(900) + 100);
            pinBlue.pulse(new Random().nextInt(900) + 100);
            Thread.sleep(900);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("GPIO Follow leds example started...");
        new ControlGPIOExample3();
        GPIO.shutdown();
    }
}
