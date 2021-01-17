package nl.bos.test;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.ArrayList;
import java.util.List;

public class ControlGPIOExample2 {

    private static boolean run = true;
    private int speed = 1;
    private static final GpioController GPIO = GpioFactory.getInstance();

    public ControlGPIOExample2() throws InterruptedException {
        List<GpioPinDigitalOutput> leds = new ArrayList();
        leds.add(GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_01));
        leds.add(GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_02));
        leds.add(GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_03));
        leds.add(GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_04));
        leds.add(GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_05));
        leds.add(GPIO.provisionDigitalOutputPin(RaspiPin.GPIO_06));
        GpioPinDigitalInput myButton = GPIO.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);

        for (GpioPinDigitalOutput led : leds) {
            led.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        }

        myButton.addListener((GpioPinListenerDigital) event -> {
            if (event.getState().isHigh()) {
                speed++;
                System.out.println(String.format("Button push; speed is %d", speed));
                if (speed == 20) {
                    System.out.println("Stopping program!");
                    run = false;
                }
            } else {
                System.out.println("Button release");
                // do nothing
            }
        });

        while (run) {
            for (GpioPinDigitalOutput led : leds) {
                led.pulse(1000 / speed);
                Thread.sleep(750 / speed);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("GPIO Follow leds example started...");
        new ControlGPIOExample2();
        GPIO.shutdown();
    }
}
