package Metier;

import Sensor.ArduinoChan;
import Sensor.Sensor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class Services {


    public static void setDebounceOne(int sensorNumber, int debounceValue) {
        arduinoInData.setDebounceTime(sensorNumber, debounceValue);
        InputManager.setDebounceOne(sensorNumber, debounceValue);
    }

    public static void setDebounceAll(int debounceValue) {
        arduinoInData.setDebounceTimeAll(debounceValue);
        InputManager.setDebounceAll(debounceValue);
    }

    public static void setThresholdOne(int sensorNumber, int thresholdValue) {
        arduinoInData.setNoiseGate(sensorNumber, thresholdValue);
        InputManager.setThresholdOne(sensorNumber, thresholdValue);
    }

    public static void setThresholdAll(int thresholdValue) {
        arduinoInData.setNoiseGateAll(thresholdValue);
        InputManager.setThresholdAll(thresholdValue);
    }

    public static void setSensorNumber(int newNumber) {
        arduinoInData.setSensorNumber(newNumber);
        InputManager.chooseChanNb(newNumber);
    }

    public static void calibrate(int sensorNumber) {
        arduinoInData.calibrateSensor(sensorNumber);
    }

    public static void calibrateAll() {
        arduinoInData.calibrateAllSensor();
    }

    public static String[] getChannelInfo(int channelNumber) {
        ArduinoChan a = InputManager.getArduinoChan(channelNumber);
        String[] toReturn = new String[3];
        toReturn[0] = String.valueOf(a.getDebounce());
        toReturn[1] = String.valueOf(a.getThreshold());
        toReturn[2] = String.valueOf(a.isEnable());
        return toReturn;
    }

    public static int getActiveNumber() {
        return InputManager.getActiveNumber();
    }

    public static boolean saveSetup(File saveFile) {
        List<Sensor> sensorList = SensorManagement.getSensorList();
        Vector<ArduinoChan> arduinoInVector = InputManager.getArduinoInVector();
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
            file.write("<!ELEMENT sensor (name, arduinoIn, midiPort, minRange, maxRange, preamplifier)>");
            file.write("<!ELEMENT name (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT arduinoIn (#PCDATA)>");
            file.newLine();
            file.write("<!ELEMENT midiPort (#PCDATA)>");
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
            for (Sensor s : sensorList) {
                file.write("    <sensor>");
                file.newLine();
                file.write("        <name>" + s.getName() + "</name>");
                file.newLine();
                file.write("        <arduinoIn>" + s.getArduinoIn() + "</arduinoIn>");
                file.newLine();
                file.write("        <midiPort>" + s.getMidiPort() + "</midiPort>");
                file.newLine();
                file.write("        <minRange>" + s.getMinRange() + "</minRange>");
                file.newLine();
                file.write("        <maxRange>" + s.getMaxRange() + "</maxRange>");
                file.newLine();
                file.write("        <preamplifier>" + s.getPreamplifier() + "</preamplifier>");
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
        List<Sensor> sensorList = new ArrayList<>();
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
                    sensorList.add(s);
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
        int minRange = getIntValue(sensEl, "minRange");
        int maxRange = getIntValue(sensEl, "maxRange");
        int preamplifier = getIntValue(sensEl, "preamplifier");

        /*Return the new sensor*/
        return new Sensor(name, arduinoIn, midiPort, MidiManager.getMidiReceiver(), minRange, maxRange, preamplifier);
    }

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
