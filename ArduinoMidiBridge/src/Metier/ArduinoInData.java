package Metier;

import IHM.OperatingWindows;
import gnu.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class ArduinoInData implements SerialPortEventListener {
    /**
     * The port we're normally going to use.
     */
    public static final String PORT_NAMES[] = {
            "/dev/tty.usbmodem * - Mac OS X - Uno et Mega 2560", // Mac OS X
            "/dev/tty.usbserial * - Mac OS X - anciennes cartes",
            "/dev/ttyACM * - Raspberry Pi & Linux", // Raspberry Pi
            "/dev/ttyUSB * - Linux", // Linux
            "COM * - Windows", // Windows
    };
    public static final int NO_ERR = 0;
    public static final int PORT_NOT_FOUND = 1;
    public static final int PORT_IN_USE = 2;
    public static final int SERIAL_ERR = 3;
    public static final int TOO_MANY_LIST_ERR = 4;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 4000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 230400;
    private static SerialPort serialPort;
    /**
     * Log from the arduino
     **/
    private static String arduiLog = "Logs :\n";
    /**
     * The output stream to the port
     */
    private static OutputStream output;
    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */
    private BufferedReader input;

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
     * set the noise gate for an input on the arduino
     *
     * @param sensorNumber the sensor to change
     * @param newValue     the new value of the gate
     * @return true if it worked
     */
    protected synchronized static boolean setNoiseGate(int sensorNumber, int newValue) {
        String s = "nthr " + newValue + " " + sensorNumber + "\n";
        return sendAsciiString(s);
    }

    /**
     * set the noise gate for all the input on the arduino
     *
     * @param newValue the new noise gate
     * @return true if it worked
     */
    protected static boolean setNoiseGateAll(int newValue) {
        String s = "nthr " + newValue + "\n";
        return sendAsciiString(s);

    }

    /**
     * set the number of debounce cycles for a sensor
     *
     * @param sensorNumber the sensor to change
     * @param newValue     the new number of debounce cycles
     * @return true if it worked
     */
    protected static boolean setDebounceTime(int sensorNumber, int newValue) {
        String s = "tthr " + newValue + " " + sensorNumber + "\n";
        return sendAsciiString(s);
    }

    /**
     * set the number of debounce cycle for all the sensors
     *
     * @param newValue new numer of debounce cycle
     * @return true if it worked
     */
    protected static boolean setDebounceTimeAll(int newValue) {
        String s = "tthr " + newValue + "\n";
        return sendAsciiString(s);
    }

    /**
     * Launch the calibration of a sensor
     *
     * @param sensorNumber the sensor to calibrate
     * @return true if it worked
     */
    protected static boolean calibrateSensor(int sensorNumber) {
        String s = "cal " + sensorNumber + "\n";
        return sendAsciiString(s);
    }

    /**
     * Launch the calibration of all sensors
     *
     * @return true if it worked
     */
    protected static boolean calibrateAllSensor() {
        String s = "cal\n";
        return sendAsciiString(s);
    }

    /**
     * set the number of sensor used by the arduino
     *
     * @param newNumber
     * @return
     */
    protected static boolean setSensorNumber(int newNumber) {
        String s = "setnb " + newNumber + "\n";
        return sendAsciiString(s);
    }


    protected static boolean setCalibrationTime(int newCalibrationTime) {
        String s = "caltm " + newCalibrationTime + "\n";
        return sendAsciiString(s);
    }


    /**
     * Send a command String on the arduino serial port
     *
     * @param toSend the string to send
     * @return true if it worked
     */
    private synchronized static boolean sendAsciiString(String toSend) {
        try {
            output.write(toSend.getBytes("ASCII"));
            Thread.sleep(200);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        ArduinoInData aid = new ArduinoInData();
        List<String> test = aid.getAvailablePorts();
        for (String s : test) {
            System.out.println(s);
        }
        listPorts();
        //aid.initialize("/dev/ttyACM0");

    }

    static void listPorts() {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName() + " - " + getPortTypeName(portIdentifier.getPortType()));
        }
    }

    static String getPortTypeName(int portType) {
        switch (portType) {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }

    public List<String> getAvailablePorts() {

        List<String> list = new ArrayList<String>();

        Enumeration portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {
            CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                list.add(portId.getName());
            }
        }

        return list;
    }

    /**
     * Initialize the connection with the arduino using specified port
     *
     * @param port the port to use (example /dev/ttyACM0 )
     * @return code of good connection or error.
     */
    public int initialize(String port) {
        // the next line is for Raspberry Pi and
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        String[] truePort = port.split(" - | ");
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
            output = serialPort.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return SERIAL_ERR;
        }


        // add event listeners
        try {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (TooManyListenersException e) {
            e.printStackTrace();
            return TOO_MANY_LIST_ERR;
        }
        return NO_ERR;
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                if (inputLine.startsWith("-")) {
                    arduiLog += inputLine + "\n";
                    OperatingWindows.refreshLogs(arduiLog);
                } else {
                    new Thread(() -> {
                        SensorManagement.sendMidiMessage(inputLine);
                        OperatingWindows.refreshInterface(inputLine);
                    }).start();
                }
                System.out.println(inputLine);

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }
}
