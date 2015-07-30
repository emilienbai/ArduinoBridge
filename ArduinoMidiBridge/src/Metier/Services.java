package Metier;

import IHM.OperatingWindows;
import IHM.Settings.MidiDeviceChoice;
import IHM.Settings.ServerSettings;
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
    private static boolean oscEnabled = false;
    private static boolean mutedAll = false;
    private static boolean isResetting = false;

    /******************************************************************************************************************/
    /**                                                                                                              **/
    /*****************************************************SENSORS******************************************************/
    /**                                                                                                              **/
    /******************************************************************************************************************/

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
        s.setIsMutedAll(mutedAll);
        MidiSensorManager.addSensor(s);
    }

    /**
     * Add a basic osc sensor
     *
     * @param name       name of the sensor
     * @param arduinoIn  match with the analog input on the arduino
     * @param oscAddress osc address where to send the data
     * @param mode       mode of action of the sensor
     */
    public static void addOscSensor(String name, int arduinoIn, String oscAddress, int mode) {
        OSCSensor s = new OSCSensor(name, arduinoIn, oscAddress, mode, OSCManager.getOscPortOut());
        s.setIsMutedAll(mutedAll);
        OSCSensorManager.addOscSensor(s);
    }

    /**
     * Add an alternate Osc sensor
     *
     * @param name          name of the sensor
     * @param arduinoIn     match with the analog input on the arduino
     * @param oscAddress    osc address where to send the data
     * @param oscAddressBis secondary osc address where to send data
     * @param mode          mode of action of the sensor
     */
    public static void addOscSensor(String name, int arduinoIn, String oscAddress, String oscAddressBis, int mode) {
        OSCSensor s = new OSCSensor(name, arduinoIn, oscAddress, oscAddressBis, mode, OSCManager.getOscPortOut());
        s.setIsMutedAll(mutedAll);
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
        if (instructions.startsWith("-") && !isResetting() && !ArduinoInData.isResetting()) {
            arduiLog += instructions + "\n";
            //OperatingWindows.refreshLogs(arduiLog);
        } else {
            if (serverEnabled) {
                Server.sendData(instructions);
            }
            String[] splitted = instructions.split("-");
            int sensorNumber;
            int value;
            if (splitted.length % 2 == 0) {
                for (int i = 0; i < splitted.length; i += 2) {
                    try {
                        sensorNumber = Integer.parseInt(splitted[i]);
                        value = Integer.parseInt(splitted[i + 1]);
                        MidiSensorManager.sendMidiMessage(sensorNumber, value);
                        OSCSensorManager.sendOscMessage(sensorNumber, value);
                    } catch (NumberFormatException e) {

                    }
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
    public static void changeOscMaxRange(String address, float newValue) {
        OSCSensorManager.changeMaxRange(address, newValue);
    }

    /**
     * Get the maximal output for a midi line
     *
     * @param midiPort midi port we want to know about
     * @return maximal output valu of the concerned midi port
     */
    public static int getMidiMaxRange(int midiPort) {
        return (int) MidiSensorManager.getMaxRange(midiPort);
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
    public static void changeOscMinRange(String address, float newValue) {
        OSCSensorManager.changeMinRange(address, newValue);
    }

    /**
     * Getter for the minimum range of a midiPort
     *
     * @param midiPort the midi port we want to know about
     * @return the minimal range we want
     */
    public static int getMidiMinRange(int midiPort) {
        return (int) MidiSensorManager.getMinRange(midiPort);
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
    private static void midiMuteAll() {
        MidiSensorManager.muteAll();
    }

    /**
     * Mute all the Osc addresses
     */
    private static void oscMuteAll() {
        OSCSensorManager.muteAll();
    }

    /**
     * un-mute all the midi ports
     */
    private static void midiUnMuteAll() {
        MidiSensorManager.unMuteAll();
    }

    /**
     * un-mute all the osc Ports
     */
    private static void oscUnMuteAll() {
        OSCSensorManager.unMuteAll();
    }

    /**
     * Mute all
     */
    public static void muteAll() {
        midiMuteAll();
        oscMuteAll();
        mutedAll = true;
    }

    /**
     * Un Mute all
     */
    public static void unMuteAll() {
        midiUnMuteAll();
        oscUnMuteAll();
        mutedAll = false;
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
     * get the output value of a sensor
     *
     * @param midiPort the midi port we want
     * @return the output value
     */
    public static int getMidiOutputValue(int midiPort) {
        return MidiSensorManager.getOutputValue(midiPort);
    }

    public static float getoscOutputValue(String address) {
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
        InputManager.setCalibrationTime(newCalibrationTime);
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

    /**
     * Reset procedure for the arduino
     */
    public static void resetArduino() {
        isResetting = true;
        muteAll();
        ArduinoInData.resetArduino();
        InputManager.reset();
        unMuteAll();
        isResetting = false;
    }

    public static boolean isResetting() {
        return isResetting;
    }

    public static void setIsResetting(boolean isResetting) {
        Services.isResetting = isResetting;
    }

    /**
     * Getter for all the arduino channel
     *
     * @return the vector of arduinoChan used in the current configuration
     */
    public static Vector<ArduinoChan> getArduinoChanVector() {
        return InputManager.getArduinoInVector();
    }

    /******************************************************************************************************************/
    /**                                                                                                              **/
    /*************************************************SERVER SETTINGS**************************************************/
    /**                                                                                                              **/
    /******************************************************************************************************************/

    /**
     * Fill the server settings frame logs
     *
     * @param logs String used to fille the server logs
     */
    public static void fillServerSettings(String logs) {
        ServerSettings.fillInfo(logs);
    }

    /**
     * Enable the server
     */
    public static void enableServer() {
        new Thread(Server::run).start();
        serverEnabled = true;
    }

    /**
     * Disable the server
     */
    public static void disableServer() {
        Server.close();
        serverEnabled = false;
    }

    /******************************************************************************************************************/
    /**                                                                                                              **/
    /***************************************************CLIENT*********************************************************/
    /**                                                                                                              **/
    /******************************************************************************************************************/

    /**
     * Connect a client to a server
     *
     * @param hostname   Name or ip address of the server
     * @param portNumber Port Number to use with the server
     * @return true if the connexion have been established
     */
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

    /**
     * Function called if the connection with the server have been lost
     */
    public static void signalDisconnection() {
        OperatingWindows.signalDisconnection();
        clientEnabled = false;
    }

    /**
     * Getter for the client status of the application
     *
     * @return true if the application is a client
     */
    public static boolean isClient() {
        return clientEnabled;
    }


    /******************************************************************************************************************/
    /**                                                                                                              **/
    /*****************************************************OSC**********************************************************/
    /**                                                                                                              **/
    /******************************************************************************************************************/

    /**
     * Launch the osc server
     *
     * @param address the address where to send Osc messages
     * @param port    the port to use
     * @return true if the server started
     */
    public static boolean launchOscServer(String address, int port) {
        if (OSCManager.chooseOSCParams(address, port)) {
            oscEnabled = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Close the connection with the Osc server
     */
    public static void closeOscServer() {
        OSCManager.exit();
        oscEnabled = false;
    }

    /**
     * Getter for the currently used address for Osc communication
     *
     * @return the currently used ip address
     */
    public static String getOscAddress() {
        return OSCManager.getAddress();
    }

    /**
     * Getter for the currently used port for Osc communication
     *
     * @return the currently used port
     */
    public static int getOscPort() {
        return OSCManager.getPort();
    }

    /**
     * Getter for the status of the Osc server
     *
     * @return true if the server is launched
     */
    public static boolean getOscStatus() {
        return oscEnabled;
    }


    /******************************************************************************************************************/
    /**                                                                                                              **/
    /**************************************************Save/Load*******************************************************/
    /**                                                                                                              **/
    /******************************************************************************************************************/

    /**
     * Save a sensorList and input configuration in a xml file
     *
     * @param saveFile File where to do the save
     * @return true if it worked
     */
    public static boolean saveSetup(File saveFile) {
        Hashtable<Integer, MidiSensor> midiSensorList = Services.getMidiTable();
        Hashtable<String, OSCSensor> oscSensorHashtable = Services.getOSCTable();
        Vector<ArduinoChan> arduinoInVector = InputManager.getArduinoInVector();

        MidiSensor ms;
        OSCSensor os;
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter(saveFile));
            file.write("<?xml version=\"1.0\"?>");
            file.newLine();
            file.write("<!DOCTYPE save [");
            file.newLine();
            file.write("<!ELEMENT save (midiSensor+, oscSensor+, input+)>");
            file.newLine();
            file.write("<!ATTLIST save midiSensorNumber CDATA \"0\">");
            file.newLine();
            /*****************************Midi Sensor**************************/
            file.write("<!ELEMENT midiSensor (name, arduinoIn, midiPort, shortcut, minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime)>");
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
            /*****************************Osc Sensor**************************/
            file.write("<!ELEMENT oscSensor (name, arduinoIn, oscAddress, minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime)>");
            file.newLine();
            file.write("<!ELEMENT name (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT arduinoIn (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT oscAddresss (#PCDATA)>");
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
            /*****************************Arduino Input**************************/
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
            file.write("<save midiSensorNumber = \"" + midiSensorList.size() + "\">");
            file.newLine();
            for (Map.Entry<Integer, MidiSensor> e : midiSensorList.entrySet()) {
                ms = e.getValue();
                file.write("    <midiSensor>");
                file.newLine();
                file.write("        <name>" + ms.getName() + "</name>");
                file.newLine();
                file.write("        <arduinoIn>" + ms.getArduinoIn() + "</arduinoIn>");
                file.newLine();
                file.write("        <midiPort>" + ms.getMidiPort() + "</midiPort>");
                file.newLine();
                file.write("        <shortcut>" + ms.getShortcut() + "</shortcut>");
                file.newLine();
                file.write("        <minRange>" + ms.getMinRange() + "</minRange>");
                file.newLine();
                file.write("        <maxRange>" + ms.getMaxRange() + "</maxRange>");
                file.newLine();
                file.write("        <preamplifier>" + ms.getPreamplifier() + "</preamplifier>");
                file.newLine();
                file.write("        <mode>" + ms.getMode() + "</mode>");
                file.newLine();
                file.write("        <noiseThreshold>" + ms.getNoiseThreshold() + "</noiseThreshold>");
                file.newLine();
                file.write("        <debounceTime>" + ms.getDebounceTime() + "</debounceTime>");
                file.newLine();
                file.write("    </midiSensor>");
                file.newLine();
            }
            for (Map.Entry<String, OSCSensor> e : oscSensorHashtable.entrySet()) {
                os = e.getValue();
                file.write("    <oscSensor>");
                file.newLine();
                file.write("        <name>" + os.getName() + "</name>");
                file.newLine();
                file.write("        <arduinoIn>" + os.getArduinoIn() + "</arduinoIn>");
                file.newLine();
                file.write("        <oscAddress>" + os.getOscAddress() + "</oscAddress>");
                file.newLine();
                file.write("        <minRange>" + os.getMinRange() + "</minRange>");
                file.newLine();
                file.write("        <maxRange>" + os.getMaxRange() + "</maxRange>");
                file.newLine();
                file.write("        <preamplifier>" + os.getPreamplifier() + "</preamplifier>");
                file.newLine();
                file.write("        <mode>" + os.getMode() + "</mode>");
                file.newLine();
                file.write("        <noiseThreshold>" + os.getNoiseThreshold() + "</noiseThreshold>");
                file.newLine();
                file.write("        <debounceTime>" + os.getDebounceTime() + "</debounceTime>");
                file.newLine();
                file.write("    </oscSensor>");
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
        Hashtable<Integer, MidiSensor> midiSensorList = new Hashtable<>();
        Hashtable<String, OSCSensor> oscSensorHashtable = new Hashtable<>();
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

            /**********MIDI********/
            NodeList nl = docEle.getElementsByTagName("midiSensor");
            if (nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {

                    //get the sensor element
                    Element el = (Element) nl.item(i);

                    //get the Employee object
                    MidiSensor s = getMidiSensor(el);
                    //add it to list
                    midiSensorList.put(s.getMidiPort(), s);
                }
            }
            MidiSensorManager.loadSetup(midiSensorList);

            /**********OSC*********/
            nl = docEle.getElementsByTagName("oscSensor");
            if (nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {

                    //get the sensor element
                    Element el = (Element) nl.item(i);

                    //get the Employee object
                    OSCSensor s = getOscSensor(el);
                    //add it to list
                    oscSensorHashtable.put(s.getOscAddress(), s);
                }
            }
            OSCSensorManager.loadSetup(oscSensorHashtable);
            /*********INPUT********/
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
     * Create OSCSensor from a xml element
     *
     * @param el the xml element to analyse
     * @return the matching OSCSensor
     */
    private static OSCSensor getOscSensor(Element el) {
        String name = getTextValue(el, "name");
        int arduinoIn = getIntValue(el, "arduinoIn");
        String oscAddress = getTextValue(el, "oscAddress");
        float minRange = getFloatValue(el, "minRange");
        float maxRange = getFloatValue(el, "maxRange");
        int preamplifier = getIntValue(el, "preamplifier");
        int mode = getIntValue(el, "mode");
        int noiseThreshold = getIntValue(el, "noiseThreshold");
        int debounceTime = getIntValue(el, "debounceTime");
        return new OSCSensor(name, arduinoIn, oscAddress, minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime, OSCManager.getOscPortOut());
    }

    /**
     * Create midiSensor from a xml element
     *
     * @param sensEl the xml element to analyse
     * @return the matching MidiSensor
     */
    private static MidiSensor getMidiSensor(Element sensEl) {

        //for each <midiSensor> element get text or int values of
        //name, arduinoIn, midiPort, minRange, maxRange, preamplifier
        String name = getTextValue(sensEl, "name");
        int arduinoIn = getIntValue(sensEl, "arduinoIn");
        int midiPort = getIntValue(sensEl, "midiPort");
        String shortcut = getTextValue(sensEl, "shortcut");
        char shortChar = shortcut.charAt(0);
        int minRange = (int) getFloatValue(sensEl, "minRange");
        int maxRange = (int) getFloatValue(sensEl, "maxRange");
        int preamplifier = getIntValue(sensEl, "preamplifier");
        int mode = getIntValue(sensEl, "mode");
        int noiseThreshold = getIntValue(sensEl, "noiseThreshold");
        int debounceTime = getIntValue(sensEl, "debounceTime");
        return new MidiSensor(name, arduinoIn, midiPort, shortChar, MidiManager.getMidiReceiver(), minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime);
        /*Return the new midiSensor*/
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
     * Get the string content between two xml tags
     *
     * @param ele     the xml element where to search
     * @param tagName the tag name
     * @return the content between the tag
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
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    /**
     * Calls getTextValue and returns a float value
     *
     * @param ele
     * @param tagName
     * @return
     */
    private static float getFloatValue(Element ele, String tagName) {
        try {
            return Float.parseFloat(getTextValue(ele, tagName));
        } catch (NumberFormatException e) {
            return (-1.0f);
        }
    }

    /**
     * Properly close the application
     */
    public static void closeApplication() {
        MidiManager.exit();
        ArduinoInData.close();
        if (serverEnabled) {
            Server.close();
        }
        System.exit(0);
    }
}
