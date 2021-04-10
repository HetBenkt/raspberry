package nl.bos.test;

import com.pi4j.io.gpio.*;

public class ControlGPIOExample4 {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("GPIO Blinking example started...");

        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalOutput ledYellow = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29);

        ledYellow.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);

        ledYellow.blink(500, 20000);

        System.out.println(" ... the LED will continue blinking until the program is done.");
        Thread.sleep(30000);
        gpio.shutdown();
    }
}
