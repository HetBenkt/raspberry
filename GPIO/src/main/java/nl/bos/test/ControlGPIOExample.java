package nl.bos.test;

import com.pi4j.io.gpio.*;

public class ControlGPIOExample {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("GPIO Follow leds example started...");

        final GpioController gpio = GpioFactory.getInstance();

        final GpioPinDigitalOutput led1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
        final GpioPinDigitalOutput led2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
        final GpioPinDigitalOutput led3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);
        final GpioPinDigitalOutput led4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        final GpioPinDigitalOutput led5 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
        final GpioPinDigitalOutput led6 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06);

        led1.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        led2.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);

        led1.pulse(1000);
        led2.pulse(2000);
        led3.pulse(3000);
        led4.pulse(4000);
        led5.pulse(5000);
        led6.pulse(6000);

        Thread.sleep(10000);
        gpio.shutdown();
    }
}
