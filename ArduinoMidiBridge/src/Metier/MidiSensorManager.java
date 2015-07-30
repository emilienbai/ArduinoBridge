package Metier;

import Sensor.MidiSensor;

import javax.sound.midi.Receiver;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class MidiSensorManager {
    private static Hashtable<Integer, MidiSensor> sensorList = new Hashtable<>();
    private static List<MidiSensor> soloedSensors = new ArrayList<>();

    /**
     * Add a sensor to the active list
     *
     * @param s the sensor to add
     */
    public static void addSensor(MidiSensor s) {
        sensorList.put(s.getMidiPort(), s);
    }

    /**
     * delete a sensor from the list
     *
     * @param midiPort the midiPort to remove
     */
    public static void deleteSensor(int midiPort) {
        sensorList.remove(midiPort);
    }

    /**
     * find the sensors concerned and send midi messages for each
     *
     * @param arduinoIn - the arduino input channel
     * @param value     - the incoming value
     */
    public static void sendMidiMessage(int arduinoIn, int value) {
        for (Map.Entry<Integer, MidiSensor> e : sensorList.entrySet()) {
            if (e.getValue().getArduinoIn() == arduinoIn) {
                e.getValue().sendMidiMessage(value);
            }
        }
    }

    /**
     * This function send a litte midi impulsion
     *
     * @param midiPort the port where to send the impulsion
     */
    public static void sendMidiImpulsion(int midiPort) {
        sensorList.get(midiPort).sendImpulsion();
    }

    /**
     * Change the max range of a midi port -> Sensor
     *
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changeMaxRange(int midiPort, int newValue) {
        sensorList.get(midiPort).setMaxRange(newValue);
    }

    /**
     * Getter for the maximum output for a midiPort
     *
     * @param midiPort the midi port we want to know about
     * @return the maximal range we want
     */
    protected static float getMaxRange(int midiPort) {
        return sensorList.get(midiPort).getMaxRange();
    }

    /**
     * Change the min range of a midi port -> Sensor
     *
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    protected static void changeMinRange(int midiPort, int newValue) {
        sensorList.get(midiPort).setMinRange(newValue);
    }

    /**
     * Getter for the minimum range of a midiPort
     *
     * @param midiPort the midi port we want to know about
     * @return the minimal range we want
     */
    protected static float getMinRange(int midiPort) {
        return sensorList.get(midiPort).getMinRange();
    }

    /**
     * Change the preamplifier for a midi port -> Sensor
     *
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    protected static void changePreamplifier(int midiPort, int newValue) {
        sensorList.get(midiPort).setPreamplifier(newValue);
    }

    /**
     * Mute a midi port
     *
     * @param midiPort midi port to mute
     */
    protected static void mute(int midiPort) {
        sensorList.get(midiPort).mute();
    }

    /**
     * Un-mute a midi port
     *
     * @param midiPort midi port to un-mute
     */
    protected static void unMute(int midiPort) {
        sensorList.get(midiPort).unMute();
    }

    /**
     * mute all the midi ports
     */
    protected static void muteAll() {
        for (Map.Entry<Integer, MidiSensor> e : sensorList.entrySet()) {
            e.getValue().setIsMutedAll(true);
        }
    }

    /**
     * un-mute all the midi ports
     */
    protected static void unMuteAll() {
        for (Map.Entry<Integer, MidiSensor> e : sensorList.entrySet()) {
            e.getValue().setIsMutedAll(false);
        }
    }

    /**
     * solo one midi port
     *
     * @param midiPort midi port to solo
     */
    protected static void solo(int midiPort) {
        for (Map.Entry<Integer, MidiSensor> e : sensorList.entrySet()) {
            MidiSensor s = e.getValue();
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
     *
     * @param midiPort the midi port to unSolo
     */
    protected static void unSolo(int midiPort) {
        MidiSensor s = sensorList.get(midiPort);
        s.setIsSoloed(false);
        soloedSensors.remove(s);
        if (soloedSensors.isEmpty()) {
            for (Map.Entry<Integer, MidiSensor> e : sensorList.entrySet()) {
                e.getValue().setIsMutedBySolo(false);
            }
        }
    }

    /**
     * Set the mode of action of a midi port
     *
     * @param midiPort midi port to set
     * @param mode     mode of action
     */
    protected static void setMode(int midiPort, int mode) {
        sensorList.get(midiPort).setMode(mode);
    }

    /**
     * Set a noise threshold for a midi port acting like a momentary or toggle button
     *
     * @param midiPort  the midi port to modify
     * @param threshold new noise threshold
     */
    protected static void setLineThreshold(int midiPort, int threshold) {
        sensorList.get(midiPort).setNoiseThreshold(threshold);
    }

    /**
     * Getter for the noise threshold of a midi port
     *
     * @param midiPort the midi port we want to know about
     * @return the noise threshold of this midi port
     */
    protected static int getNoiseThreshold(int midiPort) {
        return sensorList.get(midiPort).getNoiseThreshold();
    }

    /**
     * Set a time debounce of for a midi port acting like a momentary or toggle button
     *
     * @param midiPort the midi port to modify
     * @param debounce new time of debounce
     */
    protected static void setLineDebounce(int midiPort, int debounce) {
        sensorList.get(midiPort).setDebounceTime(debounce);
    }

    /**
     * Getter for the debounce time of a midi port
     *
     * @param midiPort the midi port we want to know about
     * @return the time debounce of this port
     */
    protected static int getDebounceTime(int midiPort) {
        return sensorList.get(midiPort).getDebounceTime();
    }

    /**
     * In case the user wants to start a new session of the app
     */
    protected static void newSetup() {
        sensorList.clear();
        soloedSensors = new ArrayList<>();
    }

    /**
     * get the output value of a sensor
     *
     * @param midiPort the midi port we want
     * @return the output value
     */
    protected static int getOutputValue(int midiPort) {
        return (int) sensorList.get(midiPort).getOutputValue();
    }

    /**
     * change receiver for all the sensors
     */
    protected static void changeReceiver() {
        Receiver r = MidiManager.getMidiReceiver();
        for (Map.Entry<Integer, MidiSensor> e : sensorList.entrySet()) {
            e.getValue().setmidiReceiver(r);
        }
    }

    /**
     * Getter for the sensor list
     *
     * @return the list of sensor
     */
    protected static Hashtable<Integer, MidiSensor> getSensorList() {
        return sensorList;
    }

    /**
     * Load an existing List of Sensors
     *
     * @param newSensorList, the sensor list to load
     */
    protected static void loadSetup(Hashtable<Integer, MidiSensor> newSensorList) {
        newSetup();
        sensorList = newSensorList;
    }
}
