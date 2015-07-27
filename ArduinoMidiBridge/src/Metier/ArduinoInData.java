package Metier;

import IHM.OperatingWindows;
import gnu.io.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class ArduinoInData implements SerialPortEventListener {
    /**
     * The port we're normally going to use.
     */
    public static final String PORT_NAMES[] = {
            "/dev/tty.usbmodem", // Mac OS X
            "/dev/tty.usbserial",
            "/dev/ttyACM", // Raspberry Pi
            "/dev/ttyUSB", // Linux
            "COM", // Windows
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
    /**
     * The Serial port used to communicate with the arduino
     */
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

    protected static boolean resetArduino() {
        return sendAsciiString("rst\n");
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
     * @param newNumber the new number of sensor
     * @return true if the message have been sent
     */
    protected static boolean setSensorNumber(int newNumber) {
        String s = "setnb " + newNumber + "\n";
        return sendAsciiString(s);
    }

    /**
     * Set the calibration time for the sensors
     *
     * @param newCalibrationTime the time of calibration in second
     * @return true if the message have been sent
     */
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
        } catch (InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }

        return true;
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
        System.setProperty("gnu.io.rxtx.SerialPorts", port);

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().startsWith(portName)) {
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
            /*SimpleDateFormat ft =
                    new SimpleDateFormat("hh:mm:ss:SSS");
            System.out.println("DATA :" + ft.format(new Date()));*/
            try {
                String inputLine = input.readLine();
                if(inputLine.startsWith("*")){
                    Services.resetArduino();
                }
                if (inputLine.startsWith("-")) {
                    arduiLog += inputLine + "\n";
                    OperatingWindows.refreshLogs(arduiLog);
                } else {
                    new Thread(() -> {
                        Services.sendMessage(inputLine);
                        //OperatingWindows.refreshInterface(inputLine);
                    }).start();
                }

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }
}