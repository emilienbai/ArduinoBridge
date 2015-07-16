package Metier;

import Sensor.ArduinoChan;

import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class InputManager {
    private static final int MAX_INPUT = 16;
    private static Vector<ArduinoChan> arduinoInVector = new Vector<>(16);
    private static int activeNumber;

    /**
     * Initialize the arduino manager
     */
    public static void init() {

        for (int i = 0; i < MAX_INPUT; i++) {
            ArduinoChan a = new ArduinoChan(i);
            arduinoInVector.add(i, a);
        }
        activeNumber = MAX_INPUT;
    }

    /**
     * Change the number of active channels
     *
     * @param newNumber number of active channels
     */
    protected static void chooseChanNb(int newNumber) {
        if (newNumber != activeNumber) {
            for (int i = newNumber; i < MAX_INPUT; i++) {
                ArduinoChan a = arduinoInVector.get(i);
                a.setEnable(false);
                arduinoInVector.set(i, a);
            }
            activeNumber = newNumber;
        }
    }

    /**
     * Set the debounce value for an arduinoInput
     *
     * @param inputNumber   The input to modify
     * @param debounceValue the new time of debounce
     */
    protected static void setDebounceOne(int inputNumber, int debounceValue) {
        ArduinoChan a = arduinoInVector.get(inputNumber);
        a.setDebounce(debounceValue);
        arduinoInVector.set(inputNumber, a);
    }

    /**
     * Set the threshold value for an arduinoInput
     *
     * @param inputNumber    The input to modify
     * @param thresholdValue the new threshold
     */
    protected static void setThresholdOne(int inputNumber, int thresholdValue) {
        ArduinoChan a = arduinoInVector.get(inputNumber);
        a.setThreshold(thresholdValue);
        arduinoInVector.set(inputNumber, a);
    }

    /**
     * Set the value of debounce for every input
     *
     * @param debounceValue the new time of debounce
     */
    protected static void setDebounceAll(int debounceValue) {
        for (ArduinoChan a : arduinoInVector) {
            a.setDebounce(debounceValue);
        }
    }

    /**
     * Set the threshold value for every input
     *
     * @param thresholdValue the new value of threshold
     */
    protected static void setThresholdAll(int thresholdValue) {
        for (ArduinoChan a : arduinoInVector) {
            a.setThreshold(thresholdValue);
        }
    }


    /**
     * Getter for an arduino channel object from its input number
     *
     * @param chanNumber channel number
     * @return matching ArduinoChan object
     */
    protected static ArduinoChan getArduinoChan(int chanNumber) {
        return arduinoInVector.get(chanNumber);
    }

    /**
     * Getter for every arduino channel object
     *
     * @return vector of all the arduinoChan
     */
    protected static Vector<ArduinoChan> getArduinoInVector() {
        return arduinoInVector;
    }

    /**
     * Load an existing vector of arduino Channel
     *
     * @param arduiChanVector The vector to load
     */
    public static void loadSetup(Vector<ArduinoChan> arduiChanVector) {
        arduinoInVector = arduiChanVector;
        int number;
        int debounce;
        int threshold;
        int activeCounter = 0;
        for (ArduinoChan ac : arduinoInVector) {
            number = ac.getNumber();
            debounce = ac.getDebounce();
            threshold = ac.getThreshold();
            if (ac.isEnable()) {
                activeCounter++;
            }
            setDebounceOne(number, debounce);
            setThresholdOne(number, threshold);

        }
        chooseChanNb(activeCounter);
    }


    /**
     * Get the number of active channel
     *
     * @return the number of active channel in this setup
     */
    public static int getActiveNumber() {
        return activeNumber;
    }

    /**
     * Re-set all the input after a software reset
     */
    public static void reset() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (ArduinoChan a : arduinoInVector) {
            int debounce = a.getDebounce();
            int threshold = a.getThreshold();
            int number = a.getNumber();
            ArduinoInData.setDebounceTime(number, debounce);
            try {
                Thread.sleep(20);

                ArduinoInData.setNoiseGate(number, threshold);
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
