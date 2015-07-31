package Sensor;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class ArduinoChan {
    public static final int INIT_DEBOUNCE = 200;
    public static final int INIT_THRESHOLD = 100;
    public static final int INIT_CAL_VALUE = 0;
    /**
     * The input number on the arduino
     */
    private int number;
    /**
     * Debounce time in ms
     */
    private int debounce;
    /**
     * Noise threshold
     */
    private int threshold;
    /**
     * Calibration value
     */
    private int calValue;
    /**
     * Status of the input
     */
    private boolean enable;

    /**
     * Default constructor for an arduinoChan
     *
     * @param number The input number
     */
    public ArduinoChan(int number) {
        this.number = number;
        this.debounce = INIT_DEBOUNCE;
        this.threshold = INIT_THRESHOLD;
        this.calValue = INIT_CAL_VALUE;
        this.enable = true;
    }

    /**
     * Constructor of an arduinoChan
     *
     * @param number    The arduino input number
     * @param debounce  Debounce time in ms
     * @param threshold Noise threshold for this input
     * @param enable    status of the sensor
     */
    public ArduinoChan(int number, int debounce, int threshold, int calValue, boolean enable) {
        this.number = number;
        this.debounce = debounce;
        this.threshold = threshold;
        this.calValue = calValue;
        this.enable = enable;
    }

    /**
     * getter for the channel number
     *
     * @return the channel number
     */
    public int getNumber() {
        return number;
    }

    /**
     * getter for the debounce time
     *
     * @return debounce time in ms
     */
    public int getDebounce() {
        return debounce;
    }

    /**
     * setter for the debounce time
     *
     * @param debounce debounce time in ms
     */
    public void setDebounce(int debounce) {
        this.debounce = debounce;
    }

    /**
     * getter for the noise threshold
     *
     * @return the noise threshold
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * setter for the noise threshold
     *
     * @param threshold the new threshold value
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * Getter for the calibration value
     *
     * @return the calibration value
     */
    public int getCalValue() {
        return calValue;
    }

    /**
     * Setter for the calibration value
     *
     * @param calValue the new calibration value
     */
    public void setCalValue(int calValue) {
        this.calValue = calValue;
    }

    /**
     * getter for the channel status
     *
     * @return true if the input is enabled
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Set the status of the input channel
     *
     * @param enable true for enable the input
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArduinoChan)) return false;

        ArduinoChan that = (ArduinoChan) o;

        return number == that.number && debounce == that.debounce
                && threshold == that.threshold && enable == that.enable;
    }

    @Override
    public String toString() {
        return "ArduinoChan{} :" + number + " Debounce time :"
                + debounce + " Threshold :" + threshold
                + " Enable :" + enable;
    }
}
