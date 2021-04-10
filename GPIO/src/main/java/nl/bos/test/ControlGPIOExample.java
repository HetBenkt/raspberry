package nl.bos.test;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Gpio;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.concurrent.*;

public class ControlGPIOExample {

    /**
     * Time in nanoseconds to separate ZERO and ONE signals.
     */
    private static final int LONGEST_ZERO = 50000;

    /**
     * PI4J Pin number.
     */
    private int pinNumber;

    /**
     * 40 bit Data from sensor
     */
    private byte[] data = null;

    /**
     * Value of last successful humidity reading.
     */
    private Double humidity = null;

    /**
     * Value of last successful temperature reading.
     */
    private Double temperature = null;

    /**
     * Last read attempt
     */
    private Long lastRead = null;

    /**
     * Constructor with pin used for signal.  See PI4J and WiringPI for
     * pin numbering systems.....
     *
     * @param pin
     */
    public ControlGPIOExample(Pin pin) {
        pinNumber = pin.getAddress();
    }

    /**
     * Communicate with sensor to get new reading data.
     *
     * @throws Exception if failed to successfully read data.
     */
    private void getData() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ReadSensorFuture readSensor = new ReadSensorFuture();
        Future<byte[]> future = executor.submit(readSensor);
        // Reset data
        data = new byte[5];
        try {
            data = future.get(3, TimeUnit.SECONDS);
            readSensor.close();
        } catch (TimeoutException e) {
            readSensor.close();
            future.cancel(true);
            executor.shutdown();
            throw e;
        }
        readSensor.close();
        executor.shutdown();
    }

    /**
     * Make a new sensor reading.
     *
     * @throws Exception
     */
    public boolean read() throws Exception {
        checkLastReadDelay();
        lastRead = System.currentTimeMillis();
        getData();
        checkParity();
        humidity = getReadingValueFromBytes(data[0], data[1]);
        temperature = getReadingValueFromBytes(data[2], data[3]);
        lastRead = System.currentTimeMillis();
        return true;
    }

    private void checkLastReadDelay() throws Exception {
        if (Objects.nonNull(lastRead)) {
            if (lastRead > System.currentTimeMillis() - 2000) {
                throw new Exception("Last read was under 2 seconds ago. Please wait longer between reads!");
            }
        }
    }

    private double getReadingValueFromBytes(final byte hi, final byte low) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(hi);
        bb.put(low);
        short shortVal = bb.getShort(0);
        return new Double(shortVal) / 10;
    }

    private void checkParity() throws ParityCheckException {
        if (!(data[4] == (data[0] + data[1] + data[2] + data[3] & 0xFF))) {
            throw new ParityCheckException();
        }
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getTemperature() {
        return temperature;
    }

    /**
     * Run from command line to loop and make readings.
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        System.out.println("Starting KY-015");
        if (Gpio.wiringPiSetup() == -1) {
            System.out.println("GPIO wiringPiSetup Failed!");
            return;
        }

        ControlGPIOExample dht11 = new ControlGPIOExample(RaspiPin.GPIO_29);
        int LOOP_SIZE = 5;
        int countSuccess = 0;
        for (int i = 0; i < LOOP_SIZE; i++) {
            try {
                Thread.sleep(3000);
                System.out.println();
                dht11.read();
                System.out.println("Humidity=" + dht11.getHumidity() +
                        "%, Temperature=" + dht11.getTemperature() + "*C");

                countSuccess++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Read success rate: " + countSuccess + " / " + LOOP_SIZE);
        System.out.println("Ending DHT22");
    }

    /**
     * Callable Future for reading sensor.  Allows timeout if it gets stuck.
     */
    private class ReadSensorFuture implements Callable<byte[]>, Closeable {

        private boolean keepRunning = true;

        public ReadSensorFuture() {
            Gpio.pinMode(pinNumber, Gpio.OUTPUT);
            Gpio.digitalWrite(pinNumber, Gpio.HIGH);
        }

        @Override
        public byte[] call() throws Exception {

            // do expensive (slow) stuff before we start.
            byte[] data = new byte[5];
            long startTime = System.nanoTime();

            sendStartSignal();
            waitForResponseSignal();
            for (int i = 0; i < 40; i++) {
                while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.LOW) {
                }
                startTime = System.nanoTime();
                while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.HIGH) {
                }
                long timeHight = System.nanoTime() - startTime;
                data[i / 8] <<= 1;
                if (timeHight > LONGEST_ZERO) {
                    data[i / 8] |= 1;
                }
            }
            return data;
        }

        private void sendStartSignal() {
            // Send start signal.
            Gpio.pinMode(pinNumber, Gpio.OUTPUT);
            Gpio.digitalWrite(pinNumber, Gpio.LOW);
            Gpio.delay(1);
            Gpio.digitalWrite(pinNumber, Gpio.HIGH);
        }

        /**
         * AM2302 will pull low 80us as response signal, then
         * AM2302 pulls up 80us for preparation to send data.
         */
        private void waitForResponseSignal() {
            Gpio.pinMode(pinNumber, Gpio.INPUT);
            while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.HIGH) {
            }
            while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.LOW) {
            }
            while (keepRunning && Gpio.digitalRead(pinNumber) == Gpio.HIGH) {
            }
        }

        @Override
        public void close() throws IOException {
            keepRunning = false;

            // Set pin high for end of transmission.
            Gpio.pinMode(pinNumber, Gpio.OUTPUT);
            Gpio.digitalWrite(pinNumber, Gpio.HIGH);
        }
    }

    private class ParityCheckException extends Exception {
        private static final long serialVersionUID = 1L;
    }
}
