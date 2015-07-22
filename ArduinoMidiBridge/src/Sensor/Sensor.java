package Sensor;

import java.util.Date;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 */
public abstract class Sensor {

    public final static int FADER = 0;
    public final static int TOGGLE = 1;
    public final static int MOMENTARY = 2;
    public final static int ALTERNATE = 3;
    final static int MAX_FROM_SENSOR = 1024;
    /**
     * Name given to the sensor
     */
    protected String name;
    /**
     * Match with the port on which the sensor is connected
     */
    protected int arduinoIn;
    /**
     * minimum midi value sent in messages
     */
    protected int minRange;
    /**
     * maximum midi value sent in messages
     */
    protected int maxRange;
    /**
     * multiplication factor to reduce or amplify sensor
     * sensibility
     */
    protected int preamplifier;
    /**
     * Muted state of the sensor
     */
    protected boolean isMuted;
    /**
     * Soloed state of the sensor
     */
    protected boolean isSoloed;
    /**
     * Muted by mute all button
     */
    protected boolean isMutedAll;
    /**
     * Muted cause of solo Button
     */
    protected boolean isMutedBySolo;
    /**
     * Last action of the toggle button
     */
    protected boolean lastWasOn;
    /**
     * Noise threshold for toggle or momentary
     */
    protected int noiseThreshold;
    /**
     * Debounce time for toggle or momentary
     */
    protected int debounceTime;
    /**
     * action mode of the line - fader, toggle or momentary button
     */
    protected int mode;
    /**
     * Date of the last impulsion
     */
    protected Date lastChange;
    /**
     * last outputValue
     */
    protected int outputValue;


    public Sensor(String name, int arduinoIn, int minRange, int maxRange, int preamplifier, int mode, int noiseThreshold, int debounceTime) {
        this.name = name;
        this.arduinoIn = arduinoIn;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.preamplifier = preamplifier;
        this.isMuted = false;
        this.isSoloed = false;
        this.isMutedAll = false;
        this.isMutedBySolo = false;
        this.lastWasOn = false;
        this.noiseThreshold = noiseThreshold;
        this.debounceTime = debounceTime;
        this.lastChange = new Date();
        this.mode = mode;
        this.lastChange = new Date();
        this.outputValue = 0;

    }

    /**
     * @param data value incoming from the sensor
     * @return result, a rescaled value between min and max range
     */
    protected int calculate(int data) {
        float result;
        result = (this.preamplifier * data) / 100;
        //apply the premaplifier modification
        result = result / MAX_FROM_SENSOR;
        //rescale the value to maximum 1
        result = result * (this.maxRange - this.minRange) + this.minRange;
        //rescale with min and max range value
        if (result <= this.maxRange) {
            return (int) result;
        } else {
            return this.maxRange;
        }
        //when the preamp is saturating the output
    }

    /**
     * Mute this sensor
     */
    public void mute() {
        this.isMuted = true;
    }

    /**
     * Un-mute this sensor
     */
    public void unMute() {
        this.isMuted = false;
    }

    public String getName() {
        return name;
    }

    public int getArduinoIn() {
        return arduinoIn;
    }

    public int getMinRange() {
        return minRange;
    }

    public void setMinRange(int minRange) {
        if (minRange > this.maxRange) {
            this.minRange = maxRange;
        } else {
            this.minRange = minRange;

        }
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        if (maxRange < this.minRange) {
            this.maxRange = this.minRange;
        } else {
            this.maxRange = maxRange;
        }
    }

    public int getPreamplifier() {
        return preamplifier;
    }

    public void setPreamplifier(int preamplifier) {
        this.preamplifier = preamplifier;
    }

    public void setIsSoloed(boolean isSoloed) {
        this.isSoloed = isSoloed;
    }

    public void setIsMutedAll(boolean isMutedAll) {
        this.isMutedAll = isMutedAll;
    }

    public void setIsMutedBySolo(boolean isMutedBySolo) {
        this.isMutedBySolo = isMutedBySolo;
    }

    public int getNoiseThreshold() {
        return noiseThreshold;
    }

    public void setNoiseThreshold(int noiseThreshold) {
        this.noiseThreshold = noiseThreshold;
    }

    public int getDebounceTime() {
        return debounceTime;
    }

    public void setDebounceTime(int debounceTime) {
        this.debounceTime = debounceTime;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getOutputValue() {
        return outputValue;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "name = '" + name + '\'' +
                ", arduinoIn = " + arduinoIn +
                ",  minRange =" + minRange +
                ", maxRange = " + maxRange +
                ", preamplifer = " + preamplifier + "" +
                ", mode = " + mode + "}";
    }
}
