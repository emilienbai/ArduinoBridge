package Metier;

import Sensor.Sensor;

import javax.sound.midi.Receiver;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class SensorManagement {
    private static Hashtable<Integer, Sensor> sensorList = new Hashtable<>();
    private static List<Sensor> soloedSensors = new ArrayList<>();


    /**
     * Add a sensor to the active list
     * @param s the sensor to add
     */
    public static void addSensor(Sensor s) {
        sensorList.put(s.getMidiPort(), s);
    }

    /**
     * delete a sensor from the list
     * @param midiPort the midiPort to remove
     */
    public static void deleteSensor(int midiPort){
        sensorList.remove(midiPort);
    }

    /**
     * find the sensors concerned and send midi messages for each
     * @param sensorNumber - the arduino input channel
     * @param value - the incoming value
     */
    public static void sendMidiMessage(int sensorNumber, int value) {
        for (Map.Entry<Integer, Sensor> e : sensorList.entrySet()) {
            if (e.getValue().getArduinoIn() == sensorNumber) {
                e.getValue().sendMidiMessage(value);
            }
        }
    }


    /**
     * This function send a litte midi impulsion
     * @param midiPort the port where to send the impulsion
     */
    public static void sendMidiImpulsion(int midiPort) {
        sensorList.get(midiPort).sendImpulsion();
    }

    /**
     * Change the max range of a midi port -> Sensor
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changeMaxRange(int midiPort, int newValue){
        sensorList.get(midiPort).setMaxRange(newValue);
    }

    /**
     * Change the min range of a midi port -> Sensor
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changeMinRange(int midiPort, int newValue){
        sensorList.get(midiPort).setMinRange(newValue);
    }

    /**
     * Change the preamplifier for a midi port -> Sensor
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changePreamplifier(int midiPort, int newValue){
        sensorList.get(midiPort).setPreamplifier(newValue);
    }

    /**
     * Mute a midi port
     * @param midiPort midi port to mute
     */
    public static void mute (int midiPort){
        sensorList.get(midiPort).mute();
    }

    /**
     * Un-mute a midi port
     * @param midiPort midi port to un-mute
     */
    public static void unmute (int midiPort){
        sensorList.get(midiPort).unMute();
    }

    /**
     * mute all the midi ports
     */
    public static void muteAll (){
        for (Map.Entry<Integer, Sensor> e : sensorList.entrySet()) {
            e.getValue().setIsMutedAll(true);
        }
    }

    /**
     * un-mute all the midi ports
     */
    public static void unMuteAll(){
        for (Map.Entry<Integer, Sensor> e : sensorList.entrySet()) {
            e.getValue().setIsMutedAll(false);
        }
    }

    /**
     * solo one midi port
     * @param midiPort midi port to solo
     */
    public static void solo(int midiPort) {
        for (Map.Entry<Integer, Sensor> e : sensorList.entrySet()) {
            Sensor s = e.getValue();
            if (s.getMidiPort() == midiPort) {
                s.setIsSoloed(true);
                soloedSensors.add(s);
            } else {
                s.setIsMutedBySolo(true);
            }
        }
    }

    /**
     * unsolo one midi channel
     * @param midiPort the midi port to unSolo
     */
    public static void unSolo (int midiPort){
        Sensor s = sensorList.get(midiPort);
        s.setIsSoloed(false);
        soloedSensors.remove(s);
        if (soloedSensors.isEmpty()){
            for (Map.Entry<Integer, Sensor> e : sensorList.entrySet()) {
                e.getValue().setIsMutedBySolo(false);
            }
        }
    }

    protected static void setMode(int midiPort, int mode) {
        sensorList.get(midiPort).setMode(mode);
    }

    protected static void setLineThreshold(int midiPort, int threshold) {
        sensorList.get(midiPort).setNoiseThreshold(threshold);
    }

    protected static void setLineDebounce(int midiPort, int debounce) {
        sensorList.get(midiPort).setDebounceTime(debounce);
    }

    /**
     * In case the user wants to start a new session of the app
     */
    public static void newSetup(){
        sensorList.clear();
        soloedSensors = new ArrayList<>();
    }

    /**
     * get the output value of a sensor
     * @param midiPort the midi port we want
     * @return the output value
     */
    public static int getOutputValue(int midiPort){
        return sensorList.get(midiPort).getOutputValue();
    }
    /**
     * change receiver for all the sensors
     */
    public static void changeReceiver(){
        Receiver r = MidiManager.getMidiReceiver();
        for (Map.Entry<Integer, Sensor> e : sensorList.entrySet()) {
            e.getValue().setmidiReceiver(r);
        }
    }

    /**
     * Getter for the sensor list
     *
     * @return the list of sensor
     */
    public static Hashtable<Integer, Sensor> getSensorList() {
        return sensorList;
    }


    /**
     * Load an existing List of Sensors
     * @param newSensorList, the sensor list to load
     */
    public static void loadSetup(Hashtable<Integer, Sensor> newSensorList) {
        newSetup();
        sensorList = newSensorList;
    }


    protected static int getMaxRange(int midiPort) {
        return sensorList.get(midiPort).getMaxRange();
    }

    protected static int getMinRange(int midiPort) {
        return sensorList.get(midiPort).getMinRange();
    }

    protected static int getDebounceTime(int midiPort) {
        return sensorList.get(midiPort).getDebounceTime();
    }

    protected static int getNoiseThreshold(int midiPort) {
        return sensorList.get(midiPort).getNoiseThreshold();
    }


}
