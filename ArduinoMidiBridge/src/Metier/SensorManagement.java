package Metier;

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
import java.util.Iterator;
import java.util.List;


/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class SensorManagement {
    private static List<Sensor> sensorList = new ArrayList<Sensor>();
    private static List<Sensor> soloedSensors = new ArrayList<Sensor>();

    /**
     * Add a sensor to the active list
     * @param name name of the sensor
     * @param arduinoIn match with the analog input on the arduino
     * @param midiPort  midiPort where to send data
     */
    public static void addSensor(String name, int arduinoIn, int midiPort){
        Sensor s = new Sensor(name, arduinoIn, midiPort, MidiManager.getMidiReceiver());
        sensorList.add(s);
    }

    /**
     * delete a sensor from the list
     * @param midiPort the midiPort to remove
     */
    public static void deleteSensor(int midiPort){
        Iterator<Sensor> iterator = sensorList.iterator();
        while (iterator.hasNext()){
            Sensor s = iterator.next();
            if (s.getMidiPort()==midiPort){
                iterator.remove();
            }
        }
    }

    /**
     * find the sensors concerned and send midi messages for each
     * @param instructions string formed arduinoIn-data-arduinoIn-data
     */
    public static void sendMidiMessage(String instructions){
        String[] splitted = instructions.split("-");
        //every instruction is separated by a -
        for (int i = 0; i<splitted.length; i+=2 ){
            int sensorNumber = Integer.parseInt(splitted[i]);
            for(Sensor s : sensorList){
                if (s.getArduinoIn()==sensorNumber){
                    s.sendMidiMessage(Integer.parseInt(splitted[i+1]));
                }
            }
        }
    }

    /**
     * This function send a litte midi impulsion
     * @param midiPort the port where to send the impulsion
     */
    public static void sendMidiImpulsion(int midiPort) {
        for (Sensor s : sensorList) {
            if (s.getMidiPort() == midiPort) {
                s.sendImpulsion();
            }
        }
    }

    /**
     * Change the max range of a midi port -> Sensor
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changeMaxRange(int midiPort, int newValue){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.setMaxRange(newValue);
            }
        }
    }

    /**
     * Change the min range of a midi port -> Sensor
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changeMinRange(int midiPort, int newValue){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.setMinRange(newValue);
            }
        }
    }

    /**
     * Change the preamplifier for a midi port -> Sensor
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changePreamplifier(int midiPort, int newValue){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.setPreamplifier(newValue);
            }
        }
    }

    /**
     * Mute a midi port
     * @param midiPort midi port to mute
     */
    public static void mute (int midiPort){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.mute();
            }
        }
    }

    /**
     * Un-mute a midi port
     * @param midiPort midi port to un-mute
     */
    public static void unmute (int midiPort){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.unMute();
            }
        }
    }

    /**
     * mute all the midi ports
     */
    public static void muteAll (){
        for (Sensor s : sensorList){
            s.setIsMutedAll(true);
        }
    }

    /**
     * un-mute all the midi ports
     */
    public static void unMuteAll(){
        for (Sensor s : sensorList){
            s.setIsMutedAll(false);
        }
    }

    /**
     * solo one midi port
     * @param midiPort midi port to solo
     */
    public static void solo(int midiPort){
        for (Sensor s : sensorList){
            if(s.getMidiPort() != midiPort){
                if (!s.isSoloed()){
                    s.setIsMutedBySolo(true);   //we mute every port which
                    // is not solo already
                }
            }
            else{
                s.setIsSoloed(true);
                soloedSensors.add(s);
                //we put soloed port in a List
            }
        }
    }

    /**
     * unsolo one midi channel
     * @param midiPort
     */
    public static void unSolo (int midiPort){
        for (Sensor s : sensorList){
            if(s.getMidiPort() == midiPort){
                s.setIsSoloed(false);
                soloedSensors.remove(s);
            }
        }
        if (soloedSensors.isEmpty()){
            for (Sensor s : sensorList){
                s.setIsMutedBySolo(false);
            }
        }
    }

    /**
     * In case the user wants to start a new session of the app
     */
    public static void newSetup(){
        sensorList = new ArrayList<Sensor>();
        soloedSensors = new ArrayList<Sensor>();
    }

    /**
     * Display information for all the sensors
     */
    protected static void displaySensors(){
        for (Sensor s : sensorList){
            System.out.println(s);
        }
    }

    /**
     * get the output value of a sensor
     * @param midiPort the midi port we want
     * @return the output value
     */
    public static int getOutputValue(int midiPort){
        for (Sensor s: sensorList){
            if(s.getMidiPort()==midiPort){
                return s.getOutputValue();
            }
        }
        return -1;
    }
    /**
     * change receiver for all the sensors
     */
    public static void changeReceiver(){
        for (Sensor s :sensorList){
            s.setmidiReceiver(MidiManager.getMidiReceiver());
        }
    }

    public static List<Sensor> getSensorList() {
        return sensorList;
    }

    /**
     * Save the actuatl setup in xml format in the specified file
     * @param saveFile where the save is effected
     * @return true if it worked
     */
    //Todo adapt interface to save answer
    public static boolean saveSetup(File saveFile){
        BufferedWriter file = null;
        try {
            file = new BufferedWriter(new FileWriter(saveFile));
            file.write("<?xml version=\"1.0\"?>");
            file.newLine();
            file.write("<!DOCTYPE save [");
            file.newLine();
            file.write("<!ELEMENT save (sensor+)>");
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
            file.write("]>");
            file.newLine();
            file.write("<save sensorNumber = \""+sensorList.size()+"\">");
            file.newLine();
            for (Sensor s : sensorList){
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
            file.write("</save>");
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loadSetup(File toLoad){
        newSetup();
        String name = null;
        int arduinoIn;
        int midiPort;
        int minRange;
        int maxRange;
        int preamplifier;

        Document dom ;
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try{
            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse(toLoad);

            //get the root element
            Element docEle = dom.getDocumentElement();

            //get a nodelist of elements
            NodeList nl = docEle.getElementsByTagName("sensor");
            System.out.println(nl.getLength());
            if(nl != null && nl.getLength() > 0) {
                for(int i = 0 ; i < nl.getLength();i++) {

                    //get the sensor element
                    Element el = (Element)nl.item(i);

                    //get the Employee object
                    Sensor s = getSensor(el);

                    //add it to list
                    sensorList.add(s);
                }
            }
            return true;
        }
        catch (Exception e){
            return false;
        }

    }

    /**
     * Create sensor from a xml element
     * @param sensEl the xml element to analyse
     * @return the matching sensor
     */
    private static Sensor getSensor(Element sensEl) {

        //for each <sensor> element get text or int values of
        //name, arduinoIn, midiPort, minRange, maxRange, preamplifier
        String name = getTextValue(sensEl,"name");
        int arduinoIn = getIntValue(sensEl,"arduinoIn");
        int midiPort = getIntValue(sensEl,"midiPort");
        int minRange = getIntValue(sensEl, "minRange");
        int maxRange = getIntValue(sensEl, "maxRange");
        int preamplifier = getIntValue(sensEl, "preamplifier");

        //Create a new sensor
        Sensor s = new Sensor(name, arduinoIn, midiPort, MidiManager.getMidiReceiver(), minRange, maxRange, preamplifier);

        return s;
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
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }



    /**
     * Calls getTextValue and returns a int value
     */
    private static int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele,tagName));
    }

}
