package Metier;

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
}
