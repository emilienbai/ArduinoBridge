package Metier;

import Sensor.ArduinoChan;
import Sensor.Sensor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

    public static void saveSetup(File saveFile) {
        List<Sensor> sensorList = SensorManagement.getSensorList();
        try {
            BufferedWriter file = new BufferedWriter(new FileWriter(saveFile));
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
            file.write("</save>");
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
