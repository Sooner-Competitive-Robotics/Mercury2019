package ouscr.mercury.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Arduino {

    public static class ArduinoEvent {

        private static GsonBuilder builder = new GsonBuilder();
        private static Gson gson = builder.create();

        String event;
        int[] data;

        public ArduinoEvent(String event, int[] data) {
            this.event = event;
            this.data = data;
        }

        public String getJson() {
            return gson.toJson(this) + '\n';
        }
    }

    private static final Logger LOGGER = Logger.getLogger( Arduino.class.getName() );
    private SerialPort comPort;

    public Arduino() throws InterruptedException {
        SerialPort[] comPorts = SerialPort.getCommPorts();
        comPort = getArduinoPort(comPorts);

        while (comPort == null) {
            LOGGER.log(Level.INFO, "Waiting for Arduino...");
            Thread.sleep(1000);
            comPorts = SerialPort.getCommPorts();
            comPort = getArduinoPort(comPorts);
        }
    }

    public void open() {

        comPort.setBaudRate(9600);
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);
    }

    public void close() {
        comPort.closePort();
    }

    public void write(ArduinoEvent event) {
        //write json data to arduino
        byte[] jsonbytes = event.getJson().getBytes();
        comPort.writeBytes(jsonbytes, jsonbytes.length);

        //read anything to confirm done
        byte[] buf = new byte[1];
        comPort.readBytes(buf, 1);

        System.out.println("read " + (int)buf[0]);
    }

    public void writeRaw(String data) {
        //write json data to arduino
        byte[] jsonbytes = data.getBytes();
        comPort.writeBytes(jsonbytes, jsonbytes.length);

        //read anything to confirm done
        byte[] buf = new byte[1];
        comPort.readBytes(buf, 1);

        System.out.println("read " + buf[0]);
    }

    private static SerialPort getArduinoPort(SerialPort[] comPorts) {
        if (comPorts.length > 0) {
            for (SerialPort com : comPorts) {
                System.out.println("Found " + com.getDescriptivePortName());
                if (com.getDescriptivePortName().contains("USB")){
                    return com;
                }
            }
        }
        return null;
    }
}