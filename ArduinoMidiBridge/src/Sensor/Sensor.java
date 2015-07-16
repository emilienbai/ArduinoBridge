package Sensor;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 */
public abstract class Sensor {
    public final static int FADER = 0;
    public final static int TOGGLE = 1;
    public final static int MOMENTARY = 2;


    public Sensor(String name, int arduinoIn, char shortcut, int minRange, int maxRange, int preamplifier, int mode, int noiseThreshold, int debounceTime) {

    }
}
