package Metier;

import IHM.MidiDeviceChoice;
import IHM.OperatingWindows;
import IHM.ServerSettings;
import Network.Server;
import Network.SocOutTh;
import Sensor.ArduinoChan;
import Sensor.Sensor;
import com.jgoodies.common.base.SystemUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class Services {

    private static String arduiLog = "Logs :\n";
    private static boolean serverEnabled = false;
    private static boolean clientEnabled = false;

    /**
     * Add a sensor
     *
     * @param name      name of the sensor
     * @param arduinoIn match with the analog input on the arduino
     * @param midiPort  midiPort where to send data
     */
    public static void addSensor(String name, int arduinoIn, int midiPort, char shortcut) {
        Sensor s = new Sensor(name, arduinoIn, midiPort, shortcut, MidiManager.getMidiReceiver());
        SensorManagement.addSensor(s);
    }

    /**
     * delete a sensor
     *
     * @param midiPort the midiPort to remove
     */
    public static void deleteSensor(int midiPort) {
        SensorManagement.deleteSensor(midiPort);
    }

    /**
     * Deconstruct instruction phrase in order to send messages
     *
     * @param instructions string formed arduinoChan-data-arduinoChan-data...
     */
    public static void sendMidiMessage(String instructions) {
        if (instructions.startsWith("-")) {
            arduiLog += instructions + "\n";
            OperatingWindows.refreshLogs(arduiLog);
        } else {
            if (serverEnabled) {
                Server.sendData(instructions);
            }
            String[] splitted = instructions.split("-");
            int sensorNumber;
            int value;
            if (splitted.length % 2 == 0) {
                for (int i = 0; i < splitted.length; i += 2) {
                    sensorNumber = Integer.parseInt(splitted[i]);
                    value = Integer.parseInt(splitted[i + 1]);
                    SensorManagement.sendMidiMessage(sensorNumber, value);

                }
            }
            OperatingWindows.refreshInterface(instructions);

        }

    }


    /**
     * This function send a litte midi impulsion
     *
     * @param midiPort the port where to send the impulsion
     */
    public static void sendMidiImpulsion(int midiPort) {
        SensorManagement.sendMidiImpulsion(midiPort);
    }


    public static void setToggle(int midiPort, boolean state) {
        SensorManagement.setToggle(midiPort, state);
    }

    /**
     * Set the debounce time in second for a sensor
     *
     * @param sensorNumber  the sensor to change
     * @param debounceValue new time of debounce
     */
    public static void setDebounceOne(int sensorNumber, int debounceValue) {
        ArduinoInData.setDebounceTime(sensorNumber, debounceValue);
        InputManager.setDebounceOne(sensorNumber, debounceValue);
    }

    /**
     * Set the debounce time in second for all sensor
     *
     * @param debounceValue new time of debounce
     */
    public static void setDebounceAll(int debounceValue) {
        ArduinoInData.setDebounceTimeAll(debounceValue);
        InputManager.setDebounceAll(debounceValue);
    }

    /**
     * Set threshold value for a sensor
     *
     * @param sensorNumber   the sensor to change
     * @param thresholdValue new threshold for the sensor
     */
    public static void setThresholdOne(int sensorNumber, int thresholdValue) {
        ArduinoInData.setNoiseGate(sensorNumber, thresholdValue);
        InputManager.setThresholdOne(sensorNumber, thresholdValue);
    }

    /**
     * Set threshold value for all sensor
     *
     * @param thresholdValue new threshold value
     */
    public static void setThresholdAll(int thresholdValue) {
        ArduinoInData.setNoiseGateAll(thresholdValue);
        InputManager.setThresholdAll(thresholdValue);
    }

    /**
     * Set number of input to listen to
     *
     * @param newNumber the new number of input
     */
    public static void setSensorNumber(int newNumber) {
        ArduinoInData.setSensorNumber(newNumber);
        InputManager.chooseChanNb(newNumber);
    }

    /**
     * Calibrate one sensor
     *
     * @param sensorNumber The sensor number to calibrate
     */
    public static void calibrate(int sensorNumber) {
        ArduinoInData.calibrateSensor(sensorNumber);
    }

    /**
     * Calibrate all sensor
     */
    public static void calibrateAll() {
        ArduinoInData.calibrateAllSensor();
    }

    /**
     * Set the calibration duration
     * @param newCalibrationTime the new duration for calibration
     */
    public static void setCalibrationTime(int newCalibrationTime) {
        ArduinoInData.setCalibrationTime(newCalibrationTime);
    }

    /**
     * Get informations about an arduino channel
     *
     * @param channelNumber The concerned channel
     * @return A string tab containing debounce time in [0], threshold in [1] and enable status in [2]
     */
    public static String[] getChannelInfo(int channelNumber) {
        ArduinoChan a = InputManager.getArduinoChan(channelNumber);
        String[] toReturn = new String[3];
        toReturn[0] = String.valueOf(a.getDebounce());
        toReturn[1] = String.valueOf(a.getThreshold());
        toReturn[2] = String.valueOf(a.isEnable());
        return toReturn;
    }

    /**
     * Get the number of active input
     *
     * @return number of active input
     */
    public static int getActiveNumber() {
        return InputManager.getActiveNumber();
    }

    /**
     * Find available serial port for Mac OS and Linux
     *
     * @return String tab of serial port
     */
    public static String[] findSerial() {
        String[] availableSerial;
        if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
            Metier.Filter f = new Metier.Filter();
            File[] fileTab = f.finder("/dev");
            availableSerial = new String[fileTab.length];
            int i = 0;
            for (File file : fileTab) {
                availableSerial[i] = file.getAbsolutePath();
                i++;
            }
        } else {
            availableSerial = new String[1];
            availableSerial[0] = "COMX";
        }
        return availableSerial;
    }

    public static void fillServerSettings(String logs) {
        ServerSettings.fillInfo(logs);
    }

    /**
     * Reset procedure for the arduino
     */
    public static void resetArduino() {
        SensorManagement.muteAll();
        ArduinoInData.resetArduino();
        InputManager.reset();
        SensorManagement.unMuteAll();
    }

    public static void closeApplication() {
        MidiManager.exit();
        ArduinoInData.close();
        if (serverEnabled) {
            Server.close();
        }
        System.exit(0);
    }

    public static void enableServer() {
        new Thread(Server::run).start();
        serverEnabled = true;
    }

    public static void disableServer() {
        Server.close();
        serverEnabled = false;
    }

    public static boolean connectClient(String hostname, int portNumber) {
        SocOutTh sot = new SocOutTh(hostname, portNumber);
        if (SocOutTh.connect()) {
            sot.start();
            MidiDeviceChoice.connect(MidiDeviceChoice.NETWORK_CONNECTION);
            clientEnabled = true;
            return true;
        }
        return false;
    }

    public static void signalDisconnection() {
        OperatingWindows.signalDisconnection();
        clientEnabled = false;
    }

    public static boolean isClient() {
        return clientEnabled;
    }



    /**
     * Save a sensorList and input configuration in a xml file
     *
     * @param saveFile File where to do the save
     * @return true if it worked
     */
    public static boolean saveSetup(File saveFile) {
        Hashtable<Integer, Sensor> sensorList = SensorManagement.getSensorList();
        Vector<ArduinoChan> arduinoInVector = InputManager.getArduinoInVector();
        Sensor s;
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter(saveFile));
            file.write("<?xml version=\"1.0\"?>");
            file.newLine();
            file.write("<!DOCTYPE save [");
            file.newLine();
            file.write("<!ELEMENT save (sensor+, input+)>");
            file.newLine();
            file.write("<!ATTLIST save sensorNumber CDATA \"0\">");
            file.newLine();
            file.write("<!ELEMENT sensor (name, arduinoIn, midiPort, shortcut, minRange, maxRange, preamplifier)>");
            file.write("<!ELEMENT name (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT arduinoIn (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT midiPort (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT shortcut (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT minRange (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT maxRange (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT preamplifier (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT input (number, debounce, threshold, enable)>");
            file.newLine();
            file.write("<!ELEMENT number (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT debounce (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT threshold (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT enable (#PCDATA)>");
            file.newLine();
            file.write("]>");
            file.newLine();
            file.write("<save sensorNumber = \"" + sensorList.size() + "\">");
            file.newLine();
            for (Map.Entry<Integer, Sensor> e : sensorList.entrySet()) {
                s = e.getValue();
                file.write("    <sensor>");
                file.newLine();
                file.write("        <name>" + s.getName() + "</name>");
                file.newLine();
                file.write("        <arduinoIn>" + s.getArduinoIn() + "</arduinoIn>");
                file.newLine();
                file.write("        <midiPort>" + s.getMidiPort() + "</midiPort>");
                file.newLine();
                file.write("        <shortcut>" + s.getShortcut() + "</shortcut>");
                file.newLine();
                file.write("        <minRange>" + s.getMinRange() + "</minRange>");
                file.newLine();
                file.write("        <maxRange>" + s.getMaxRange() + "</maxRange>");
                file.newLine();
                file.write("        <preamplifier>" + s.getPreamplifier() + "</preamplifier>");
                file.newLine();
                file.write("        <toggle>" + s.isToggle() + "</toggle>");
                file.newLine();
                file.write("    </sensor>");
                file.newLine();
            }
            for (ArduinoChan a : arduinoInVector) {
                file.write("    <input>");
                file.newLine();
                file.write("        <number>" + a.getNumber() + "</number>");
                file.newLine();
                file.write("        <debounce>" + a.getDebounce() + "</debounce>");
                file.newLine();
                file.write("        <threshold>" + a.getThreshold() + "</threshold>");
                file.newLine();
                file.write("        <enable>" + a.isEnable() + "</enable>");
                file.write("    </input>");
                file.newLine();
            }
            file.write("</save>");
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load an existing setup from a xml formatted file
     *
     * @param toLoad the xml file to load
     * @return the result of the loading
     */
    public static boolean loadSetup(File toLoad) {
        Hashtable<Integer, Sensor> sensorList = new Hashtable<>();
        Vector<ArduinoChan> arduinoInVector = new Vector<>();
        Document dom;
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse(toLoad);

            //get the root element
            Element docEle = dom.getDocumentElement();

            //get a nodelist of elements
            NodeList nl = docEle.getElementsByTagName("sensor");
            if (nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {

                    //get the sensor element
                    Element el = (Element) nl.item(i);

                    //get the Employee object
                    Sensor s = getSensor(el);
                    //add it to list
                    sensorList.put(s.getMidiPort(), s);
                }
            }
            SensorManagement.loadSetup(sensorList);

            nl = docEle.getElementsByTagName("input");
            if (nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    Element el = (Element) nl.item(i);
                    ArduinoChan a = getArduinoChan(el);
                    arduinoInVector.add(a.getNumber(), a);
                }
            }
            InputManager.loadSetup(arduinoInVector);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Create sensor from a xml element
     *
     * @param sensEl the xml element to analyse
     * @return the matching sensor
     */
    private static Sensor getSensor(Element sensEl) {

        //for each <sensor> element get text or int values of
        //name, arduinoIn, midiPort, minRange, maxRange, preamplifier
        String name = getTextValue(sensEl, "name");
        int arduinoIn = getIntValue(sensEl, "arduinoIn");
        int midiPort = getIntValue(sensEl, "midiPort");
        String shortcut = getTextValue(sensEl, "shortcut");
        char shortChar = shortcut.charAt(0);
        int minRange = getIntValue(sensEl, "minRange");
        int maxRange = getIntValue(sensEl, "maxRange");
        int preamplifier = getIntValue(sensEl, "preamplifier");
        String toggle = getTextValue(sensEl, "toggle");
        if (toggle.equals("true")) {
            return new Sensor(name, arduinoIn, midiPort, shortChar, MidiManager.getMidiReceiver(), minRange, maxRange, preamplifier, true);
        } else {
            return new Sensor(name, arduinoIn, midiPort, shortChar, MidiManager.getMidiReceiver(), minRange, maxRange, preamplifier, false);
        }
        /*Return the new sensor*/
    }

    /**
     * Getter for an arduino channel from the xml saveFile
     *
     * @param ardEl Xml Element
     * @return the arduino channel built
     */
    private static ArduinoChan getArduinoChan(Element ardEl) {
        int number = getIntValue(ardEl, "number");
        int debounce = getIntValue(ardEl, "debounce");
        int threshold = getIntValue(ardEl, "threshold");
        String enable = getTextValue(ardEl, "enable");
        if (enable.equals("true")) {
            return new ArduinoChan(number, debounce, threshold, true);
        } else {
            return new ArduinoChan(number, debounce, threshold, false);
        }

    }

    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is 'name' I will return John
     */
    private static String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private static int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    public static Vector<ArduinoChan> getArduinoChanVector() {
        return InputManager.getArduinoInVector();
    }

}
