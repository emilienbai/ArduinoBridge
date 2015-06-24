package Metier;

import Sensor.ArduinoChan;

import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class InputManager {
    private static final int MAX_INPUT = 16;


    private static Vector<ArduinoChan> arduinoInVector = new Vector(16);
    private static int activeNumber;

    public static void init() {

        for (int i = 0; i < MAX_INPUT; i++) {
            ArduinoChan a = new ArduinoChan(i);
            arduinoInVector.add(i, a);
        }
        activeNumber = MAX_INPUT;
    }

    protected static void chooseChanNb(int newNumber) {
        if (newNumber != activeNumber) {
            for (int i = newNumber; i < MAX_INPUT; i++) {
                ArduinoChan a = (ArduinoChan) arduinoInVector.get(i);
                a.setEnable(false);
                arduinoInVector.set(i, a);
            }
        }
    }

    protected static void setDebounceOne(int inputNumber, int debounceValue) {
        ArduinoChan a = (ArduinoChan) arduinoInVector.get(inputNumber);
        a.setDebounce(debounceValue);
        arduinoInVector.set(inputNumber, a);
    }

    protected static void setThresholdOne(int inputNumber, int thresholdValue) {
        ArduinoChan a = (ArduinoChan) arduinoInVector.get(inputNumber);
        a.setThreshold(thresholdValue);
        arduinoInVector.set(inputNumber, a);
    }

    protected static void setDebounceAll(int debounceValue) {
        for (ArduinoChan a : arduinoInVector) {
            a.setDebounce(debounceValue);
        }
    }

    protected static void setThresholdAll(int thresholdValue) {
        for (ArduinoChan a : arduinoInVector) {
            a.setThreshold(thresholdValue);
        }
    }

    private static void listInput() {
        for (ArduinoChan a : arduinoInVector) {
            System.out.println(a);
        }
    }

    protected static ArduinoChan getArduinoChan(int chanNumber) {
        return arduinoInVector.get(chanNumber);
    }

    protected static Vector<ArduinoChan> getArduinoInVector() {
        return arduinoInVector;
    }

    public static void loadSetup(Vector<ArduinoChan> a) {
        arduinoInVector = a;
    }


    public static void main(String[] args) {
        InputManager.init();
        InputManager.listInput();
        System.out.println();
        InputManager.chooseChanNb(10);
        InputManager.listInput();
        System.out.println();
        InputManager.setDebounceOne(5, 400);
        InputManager.setThresholdOne(4, 800);
        InputManager.listInput();
        System.out.println();
        InputManager.setDebounceAll(200);
        InputManager.setThresholdAll(150);
        InputManager.listInput();

    }
}



