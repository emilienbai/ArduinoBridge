package Metier;

import IHM.OperatingWindows;
import gnu.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class arduinoInData implements SerialPortEventListener {
    private static SerialPort serialPort;
    /** The port we're normally going to use. */
    public static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-* - Mac OS X - remplacer l'étoile par le vrai numéro", // Mac OS X
            "/dev/ttyACM0 - Raspberry Pi & Linux", // Raspberry Pi
            "/dev/ttyUSB0 - Linux", // Linux
            "COM3 - Windows", // Windows
    };

    public static final int NO_ERR = 0;
    public static final int PORT_NOT_FOUND = 1;
    public static final int PORT_IN_USE = 2;
    public static final int SERIAL_ERR = 3;
    public static final int TOO_MANY_LIST_ERR = 4;
    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */
    private BufferedReader input;
    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 500;
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 115200;

    /**
     * Initialize the connection with the arduino using specified port
     * @param port the port to use (example /dev/ttyACM0 )
     * @return code of good connection or error.
     */
    public int initialize(String port) {
        // the next line is for Raspberry Pi and
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        String[] truePort = port.split(" - ");
        System.setProperty("gnu.io.rxtx.SerialPorts", truePort[0]);

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                String[] newPortName = portName.split(" - | ");
                if (currPortId.getName().startsWith(newPortName[0])) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.err.println("Could not find COM port.");
            return PORT_NOT_FOUND;
        }

            // open serial port, and use class name for the appName.
        try {
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);
        } catch (PortInUseException e) {
            e.printStackTrace();
            return PORT_IN_USE;
        }

        // set port parameters
        try {
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
            return SERIAL_ERR;
        }

        // open the streams
        try {
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return SERIAL_ERR;
        }


            // add event listeners
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
            System.out.println("ajout de l'envent listener");
        } catch (TooManyListenersException e) {
            e.printStackTrace();
            return TOO_MANY_LIST_ERR;
        }
        return NO_ERR;
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public static synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine=input.readLine();
                System.out.println("lecture d'une ligne");
                new Thread(() -> {
                    SensorManagement.sendMidiMessage(inputLine);
                    OperatingWindows.refreshInterface(inputLine);
                }).start();
                System.out.println(inputLine);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    public static void main (String[] args){
        arduinoInData aid = new arduinoInData();
        aid.initialize("/dev/ttyACM0");
    }
}
