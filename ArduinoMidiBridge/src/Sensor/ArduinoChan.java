package Sensor;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class ArduinoChan {
    public static final int INIT_DEBOUNCE = 200;
    public static final int INIT_THRESHOLD = 100;
    private int number;
    private int debounce;
    private int threshold;
    private boolean enable;

    public ArduinoChan(int number) {
        this.number = number;
        this.debounce = INIT_DEBOUNCE;
        this.threshold = INIT_THRESHOLD;
        this.enable = true;
    }

    public ArduinoChan(int number, int debounce, int threshold) {
        this.number = number;
        this.debounce = debounce;
        this.threshold = threshold;
        this.enable = true;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDebounce() {
        return debounce;
    }

    public void setDebounce(int debounce) {
        this.debounce = debounce;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArduinoChan)) return false;

        ArduinoChan that = (ArduinoChan) o;

        if (number != that.number) return false;
        if (debounce != that.debounce) return false;
        if (threshold != that.threshold) return false;
        return enable == that.enable;

    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + debounce;
        result = 31 * result + threshold;
        result = 31 * result + (enable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ArduinoChan{} :" + number + " Debounce time :"
                + debounce + " Threshold :" + threshold
                + " Enable :" + enable;
    }
}
