package Metier;

import IHM.MidiDeviceChoice;
import IHM.OperatingWindows;
import IHM.ServerSettings;
import Network.Server;
import Network.SocOutTh;
import Sensor.ArduinoChan;
import Sensor.MidiSensor;
import Sensor.OSCSensor;
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
     * Add a midi sensor
     *
     * @param name      name of the sensor
     * @param arduinoIn match with the analog input on the arduino
     * @param midiPort  midiPort where to send data
     * @param shortcut  key to press to send an impulsion
     */
    public static void addMidiSensor(String name, int arduinoIn, int midiPort, char shortcut) {
        MidiSensor s = new MidiSensor(name, arduinoIn, midiPort, shortcut, MidiManager.getMidiReceiver());
        MidiSensorManager.addSensor(s);
    }

    public static void addOscSensor(String name, int arduinoIn, String oscAddress, int mode) {
        OSCSensor s = new OSCSensor(name, arduinoIn, oscAddress, mode, OSCManager.getOscPortOut());
        OSCSensorManager.addOscSensor(s);
    }

    /**
     * delete a sensor
     *
     * @param midiPort the midiPort to remove
     */
    public static void deleteMidiSensor(int midiPort) {
        MidiSensorManager.deleteSensor(midiPort);
    }

    /**
     * Delete an osc sensor from the table
     *
     * @param address address of the sensor to delete
     */
    public static void deleteOscSensor(String address) {
        OSCSensorManager.deleteOscSensor(address);
    }

    /**
     * Deconstruct instruction phrase in order to send messages
     *
     * @param instructions string formed arduinoChan-data-arduinoChan-data...
     */
    public static void sendMessage(String instructions) {
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
                    MidiSensorManager.sendMidiMessage(sensorNumber, value);
                    OSCSensorManager.sendOscMessage(sensorNumber, value);
                }
            }
            OperatingWindows.refreshInterface(instructions);

        }

    }

    /**
     * Change maximal output value for a midi line
     *
     * @param midiPort midiPort to modify
     * @param newValue new max value to apply
     */
    public static void changeMidiMaxRange(int midiPort, int newValue) {
        MidiSensorManager.changeMaxRange(midiPort, newValue);
    }

    /**
     * Change maximal output for an osc line
     *
     * @param address  the osc address to set
     * @param newValue new max value to apply
     */
    public static void changeOscMaxRange(String address, int newValue) {
        OSCSensorManager.changeMaxRange(address, newValue);
    }

    /**
     * Get the maximal output for a midi line
     *
     * @param midiPort midi port we want to know about
     * @return maximal output valu of the concerned midi port
     */
    public static int getMidiMaxRange(int midiPort) {
        return MidiSensorManager.getMaxRange(midiPort);
    }

    /**
     * Getter for the maximum output value of an osc address
     *
     * @param address address we want to know about
     * @return the maximum output value for this
     */
    public static int getOscMaxRange(String address) {
        return OSCSensorManager.getMaxRange(address);
    }

    /**
     * Change minimal output value for a midi line
     *
     * @param midiPort midiPort to modify
     * @param newValue new max value to apply
     */
    public static void changeMidiMinRange(int midiPort, int newValue) {
        MidiSensorManager.changeMinRange(midiPort, newValue);
    }

    /**
     * Change the minimum output value for an osc address
     *
     * @param address  address to modify
     * @param newValue new value for the minimum range
     */
    public static void changeOscMinRange(String address, int newValue) {
        OSCSensorManager.changeMinRange(address, newValue);
    }

    /**
     * Getter for the minimum range of a midiPort
     *
     * @param midiPort the midi port we want to know about
     * @return the minimal range we want
     */
    public static int getMidiMinRange(int midiPort) {
        return MidiSensorManager.getMinRange(midiPort);
    }

    /**
     * Getter for the minimum output value of an osc address
     *
     * @param address address we want to know about
     * @return the minimum output value for this
     */
    public static int getOscMinRange(String address) {
        return OSCSensorManager.getMinRange(address);
    }

    /**
     * Change the preamplifier for a midi port -> Sensor
     *
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changeMidiPreamplifier(int midiPort, int newValue) {
        MidiSensorManager.changePreamplifier(midiPort, newValue);
    }

    /**
     * Change the preamplifier for an osc Address
     *
     * @param address  address to modify
     * @param newValue new value of the preamplifier
     */
    public static void changeOscPreamplifier(String address, int newValue) {
        OSCSensorManager.changePreamplifier(address, newValue);
    }

    /**
     * Mute a midi Port
     *
     * @param midiPort midi port to mute
     */
    public static void midiMute(int midiPort) {
        MidiSensorManager.mute(midiPort);
    }

    /**
     * Mute an osc address
     *
     * @param address the address to mute
     */
    public static void oscMute(String address) {
        OSCSensorManager.mute(address);
    }

    /**
     * Un-mute a midi port
     *
     * @param midiPort midi port to un-mute
     */
    public static void midiUnMute(int midiPort) {
        MidiSensorManager.unMute(midiPort);
    }

    /**
     * Un-Mute an osc address
     *
     * @param address address to un-mute
     */
    public static void oscUnMute(String address) {
        OSCSensorManager.unMute(address);
    }

    /**
     * Mute all the midi port
     */
    public static void midiMuteAll() {
        MidiSensorManager.muteAll();
    }

    /**
     * Mute all the Osc addresses
     */
    public static void oscMuteAll() {
        OSCSensorManager.muteAll();
    }

    /**
     * un-mute all the midi ports
     */
    public static void midiUnMuteAll() {
        MidiSensorManager.unMuteAll();
    }

    /**
     * Solo a midi port
     *
     * @param midiPort the midi port to solo
     */
    public static void midiSolo(int midiPort) {
        MidiSensorManager.solo(midiPort);
    }

    /**
     * Solo an osc address
     *
     * @param address the address to mute
     */
    public static void oscSolo(String address) {
        OSCSensorManager.solo(address);
    }

    /**
     * unsolo a midi channel
     *
     * @param midiPort the midi port to unSolo
     */
    public static void midiUnSolo(int midiPort) {
        MidiSensorManager.unSolo(midiPort);
    }

    /**
     * Un-Solo an osc address
     *
     * @param address the osc address to Un-Solo
     */
    public static void oscUnSolo(String address) {
        OSCSensorManager.unSolo(address);
    }

    /**
     * This function send a litte midi impulsion
     *
     * @param midiPort the port where to send the impulsion
     */
    public static void sendMidiImpulsion(int midiPort) {
        MidiSensorManager.sendMidiImpulsion(midiPort);
    }

    /**
     * Send an osc Message to test if it work with the receiver
     *
     * @param address the address where to send the message
     */
    public static void sendOscTestMessage(String address) {
        OSCSensorManager.sendTestMessage(address);
    }
    /**
     * Set the mode of action of a midi port
     *
     * @param midiPort midi port to set
     * @param mode     mode of action
     */
    public static void setMidiMode(int midiPort, int mode) {
        MidiSensorManager.setMode(midiPort, mode);
    }

    /**
     * Set a noise threshold for a midi port acting like a momentary or toggle button
     *
     * @param midiPort  the midi port to modify
     * @param threshold new noise threshold
     */
    public static void setMidiLineThreshold(int midiPort, int threshold) {
        MidiSensorManager.setLineThreshold(midiPort, threshold);
    }

    /**
     * Set a noise threshold for an osc acting like a momentary or toggle button
     *
     * @param address   the osc address to modify
     * @param threshold new noise threshold
     */
    public static void setOscLineThreshold(String address, int threshold) {
        OSCSensorManager.setLineThreshold(address, threshold);
    }

    /**
     * Getter for the noise threshold of a midi port
     *
     * @param midiPort the midi port we want to know about
     * @return the noise threshold of this midi port
     */
    public static int getMidiLineThreshold(int midiPort) {
        return MidiSensorManager.getNoiseThreshold(midiPort);
    }

    /**
     * Getter for the noise threshold of an osc address
     *
     * @param address the osc address we want to know about
     * @return the noise threshold of this osc address
     */
    public static int getOscLineThreshold(String address) {
        return OSCSensorManager.getLineThreshold(address);
    }

    /**
     * Set a time of for a midi port acting like a momentary or toggle button
     *
     * @param midiPort the midi port to modify
     * @param debounce new time of debounce
     */
    public static void setMidiLineDebounce(int midiPort, int debounce) {
        MidiSensorManager.setLineDebounce(midiPort, debounce);
    }

    /**
     * Set a time debounce of for an osc address acting like a momentary or toggle button
     *
     * @param address  the osc address
     * @param debounce new time of debounce
     */
    public static void setOscLineDebounce(String address, int debounce) {
        OSCSensorManager.setLineDebounce(address, debounce);
    }


    /**
     * Getter for the debounce time of a midi port
     *
     * @param midiPort the midi port we want to know about
     * @return the time debounce of this port
     */
    public static int getMidiLineDebounce(int midiPort) {
        return MidiSensorManager.getDebounceTime(midiPort);
    }

    /**
     * Getter for the debounce time for an osc address
     *
     * @param address the osc address we want to know about
     * @return the debounce time of the osc address
     */
    public static int getOscLineDebounce(String address) {
        return OSCSensorManager.getLineDebounce(address);
    }


    /**
     * get the output value of a sensor
     *
     * @param midiPort the midi port we want
     * @return the output value
     */
    public static int getMidiOutputValue(int midiPort) {
        return MidiSensorManager.getOutputValue(midiPort);
    }

    public static int getoscOutputValue(String address) {
        return OSCSensorManager.getOutputValue(address);
    }

    /**
     * To start a new Session of the application
     */
    public static void newSetup() {
        MidiSensorManager.newSetup();
        OSCSensorManager.newSetup();
    }

    /**
     * change receiver for all the sensors
     */
    public static void changeMidiReceiver() {
        MidiSensorManager.changeReceiver();
    }

    /**
     * Change osc port out for all the sensors
     */
    public static void changeOscPortOut() {
        OSCSensorManager.changeOscPortOut();
    }

    /**
     * Getter for the sensor list
     *
     * @return the list of sensor
     */
    public static Hashtable<Integer, MidiSensor> getMidiTable() {
        return MidiSensorManager.getSensorList();
    }

    /**
     * Getter for the sensor table
     *
     * @return the sensor table
     */
    public static Hashtable<String, OSCSensor> getOSCTable() {
        return OSCSensorManager.getOscSensorTable();
    }


    /******************************************************************************************************************/
    /**                                                                                                              **/
    /**************************************************ARDUINO INPUTS**************************************************/
    /**                                                                                                              **/
    /******************************************************************************************************************/
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
     *
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
        MidiSensorManager.muteAll();
        ArduinoInData.resetArduino();
        InputManager.reset();
        MidiSensorManager.unMuteAll();
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
        Hashtable<Integer, MidiSensor> sensorList = MidiSensorManager.getSensorList();
        Vector<ArduinoChan> arduinoInVector = InputManager.getArduinoInVector();
        MidiSensor s;
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
            file.write("<!ELEMENT sensor (name, arduinoIn, midiPort, shortcut, minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime)>");
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
            file.write("<!ELEMENT noiseThreshold (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT debounceTime (#PCDATA)>");
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
            for (Map.Entry<Integer, MidiSensor> e : sensorList.entrySet()) {
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
                file.write("        <mode>" + s.getMode() + "</mode>");
                file.newLine();
                file.write("        <noiseThreshold>" + s.getNoiseThreshold() + "</noiseThreshold>");
                file.newLine();
                file.write("        <debounceTime>" + s.getDebounceTime() + "</debounceTime>");
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
        Hashtable<Integer, MidiSensor> sensorList = new Hashtable<>();
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
                    MidiSensor s = getMidiSensor(el);
                    //add it to list
                    sensorList.put(s.getMidiPort(), s);
                }
            }
            MidiSensorManager.loadSetup(sensorList);

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
    private static MidiSensor getMidiSensor(Element sensEl) {

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
        int mode = getIntValue(sensEl, "mode");
        int noiseThreshold = getIntValue(sensEl, "noiseThreshold");
        int debounceTime = getIntValue(sensEl, "debounceTime");
        return new MidiSensor(name, arduinoIn, midiPort, shortChar, MidiManager.getMidiReceiver(), minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime);
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
