package Metier;

import Sensor.OSCSensor;
import com.illposed.osc.OSCPortOut;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 */
public class OSCSensorManager {
    private static Hashtable<String, OSCSensor> oscSensorTable = new Hashtable<>();
    private static List<OSCSensor> soloedSensors = new ArrayList<>();

    /**
     * Add a sensor to the active list
     *
     * @param s the Sensor to add
     */
    protected static void addOscSensor(OSCSensor s) {
        oscSensorTable.put(s.getOscAddress(), s);
    }

    /**
     * delete a sensor from the table
     *
     * @param address address of the sensor to delete
     */
    protected static void deleteOscSensor(String address) {
        oscSensorTable.remove(address);
    }

    /**
     * Send an osc message to the concerned adresses
     *
     * @param arduinoIn arduino input matching with the address
     * @param value     value from the sensor
     */
    protected static void sendOscMessage(int arduinoIn, int value) {
        for (Map.Entry<String, OSCSensor> e : oscSensorTable.entrySet()) {
            if (e.getValue().getArduinoIn() == arduinoIn) {
                e.getValue().sendOSCMessage(value);
            }
        }
    }

    /**
     * Change the maximum output value for an osc address
     *
     * @param address  address to modify
     * @param newValue new value for the maximum range
     */
    protected static void changeMaxRange(String address, int newValue) {
        oscSensorTable.get(address).setMaxRange(newValue);
    }

    /**
     * Getter for the maximum output value of an osc address
     *
     * @param address address we want to know about
     * @return the maximum output value for this
     */
    protected static int getMaxRange(String address) {
        return oscSensorTable.get(address).getMaxRange();
    }

    /**
     * Change the minimum output value for an osc address
     *
     * @param address  address to modify
     * @param newValue new value for the minimum range
     */
    protected static void changeMinRange(String address, int newValue) {
        oscSensorTable.get(address).setMinRange(newValue);
    }

    /**
     * Getter for the minimum output value of an osc address
     *
     * @param address address we want to know about
     * @return the minimum output value for this
     */
    protected static int getMinRange(String address) {
        return oscSensorTable.get(address).getMinRange();
    }

    /**
     * Change the preamplifier for an osc Address
     *
     * @param address  address to modify
     * @param newValue new value of the preamplifier
     */
    protected static void changePreamplifier(String address, int newValue) {
        oscSensorTable.get(address).setPreamplifier(newValue);
    }

    /**
     * Mute an osc address
     *
     * @param address the address to mute
     */
    protected static void mute(String address) {
        oscSensorTable.get(address).mute();
    }

    /**
     * Un-Mute an osc address
     *
     * @param address address to un-mute
     */
    protected static void unMute(String address) {
        oscSensorTable.get(address).unMute();
    }

    /**
     * Mute all the Osc addresses
     */
    protected static void muteAll() {
        for (Map.Entry<String, OSCSensor> e : oscSensorTable.entrySet()) {
            e.getValue().setIsMutedAll(true);
        }
    }

    /**
     * Solo an osc address
     *
     * @param address the address to mute
     */
    protected static void solo(String address) {
        for (Map.Entry<String, OSCSensor> e : oscSensorTable.entrySet()) {
            OSCSensor s = e.getValue();
            if (s.getOscAddress().equals(address)) {
                s.setIsSoloed(true);
                soloedSensors.add(s);
            } else {
                s.setIsMutedBySolo(true);
            }
        }
    }

    /**
     * Un-Solo an osc address
     *
     * @param address the osc address to Un-Solo
     */
    protected static void unSolo(String address) {
        OSCSensor s = oscSensorTable.get(address);
        s.setIsSoloed(false);
        soloedSensors.remove(s);
        if (soloedSensors.isEmpty()) {
            for (Map.Entry<String, OSCSensor> e : oscSensorTable.entrySet()) {
                e.getValue().setIsMutedBySolo(false);
            }
        }
    }

    /**
     * Set a noise threshold for an osc acting like a momentary or toggle button
     *
     * @param address   the osc address to modify
     * @param threshold new noise threshold
     */
    protected static void setLineThreshold(String address, int threshold) {
        oscSensorTable.get(address).setNoiseThreshold(threshold);
    }

    /**
     * Getter for the noise threshold of an osc address
     *
     * @param address the osc address we want to know about
     * @return the noise threshold of this osc address
     */
    protected static int getLineThreshold(String address) {
        return oscSensorTable.get(address).getNoiseThreshold();
    }

    /**
     * Set a time debounce of for an osc address acting like a momentary or toggle button
     *
     * @param address  the osc address
     * @param debounce new time of debounce
     */
    protected static void setLineDebounce(String address, int debounce) {
        oscSensorTable.get(address).setDebounceTime(debounce);
    }

    /**
     * Getter for the debounce time for an osc address
     *
     * @param address the osc address we want to know about
     * @return the debounce time of the osc address
     */
    protected static int getLineDebounce(String address) {
        return oscSensorTable.get(address).getDebounceTime();
    }

    /**
     * to start a new Session of the application
     */
    protected static void newSetup() {
        oscSensorTable.clear();
        soloedSensors = new ArrayList<>();
    }

    /**
     * Getter for the last outputValue for an osc Sensor
     *
     * @param address the address we want to know about
     * @return the last output value of this sensor
     */
    protected static int getOutputValue(String address) {
        return oscSensorTable.get(address).getOutputValue();
    }

    /**
     * Change osc port out for all the sensors
     */
    protected static void changeOscPortOut() {
        OSCPortOut o = OSCManager.getOscPortOut();
        for (Map.Entry<String, OSCSensor> e : oscSensorTable.entrySet()) {
            e.getValue().setOscPortOut(o);
        }
    }

    /**
     * Getter for the sensor table
     *
     * @return the sensor table
     */
    protected static Hashtable<String, OSCSensor> getOscSensorTable() {
        return oscSensorTable;
    }

    /**
     * Load an existing table of sensors
     *
     * @param newSensorTable the table to load
     */
    protected static void loadSetup(Hashtable<String, OSCSensor> newSensorTable) {
        newSetup();
        oscSensorTable = newSensorTable;
    }
}
