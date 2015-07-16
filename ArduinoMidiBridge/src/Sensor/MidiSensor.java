/**
 *
 */
package Sensor;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.Date;

/**
 * @author emilien Bai
 */
public class MidiSensor {

    final static int MAX_FROM_SENSOR = 1024;
    final static int MAX_VELOCITY = 127;

    /**
     * Name given to the sensor
     */
    private String name;
    /**
     * Match with the port on which the sensor is connected
     */
    private int arduinoIn;
    /**
     * Midi note matching with a midi port
     */
    private int midiPort;
    /**
     * Receiver where the midi command will be sent
     */
    private Receiver midiReceiver;
    /**
     * minimum midi value sent in messages
     */
    private int minRange;
    /**
     * maximum midi value sent in messages
     */
    private int maxRange;
    /**
     * multiplication factor to reduce or amplify sensor
     * sensibility
     */
    private int preamplifier;
    /**
     * Muted state of the sensor
     */
    private boolean isMuted;
    /**
     * Soloed state of the sensor
     */
    private boolean isSoloed;
    /**
     * Muted by mute all button
     */
    private boolean isMutedAll;
    /**
     * Muted cause of solo Button
     */
    private boolean isMutedBySolo;
    /**
     * last outputValue
     */
    private int outputValue;
    /**
     * a keyboard shortcut matching with the sensor
     */
    private char shortcut;
    /**
     * how sensor does act : fader - toggle button - momentary button
     */
    private int mode;
    /**
     * Last action of the toggle button
     */
    private boolean lastWasOn;
    /**
     * Noise threshold for toggle or momentary
     */
    private int noiseThreshold;
    /**
     * Debounce time for toggle or momentary
     */
    private int debounceTime;
    /**
     * Date of the last impulsion
     */
    private Date lastChange;

    /**
     * @param name         Name of the sensor
     * @param arduinoIn    arduino analogInput number for this sensor
     * @param midiPort     midi port to use
     * @param shortcut     shortcut matching with the sensor Object
     * @param midiReceiver where to send the midi informations
     */
    public MidiSensor(String name, int arduinoIn, int midiPort, char shortcut,
                      Receiver midiReceiver) {
        this.name = name;
        this.arduinoIn = arduinoIn;
        this.midiPort = midiPort;
        this.shortcut = shortcut;
        this.midiReceiver = midiReceiver;
        this.minRange = 0;
        this.maxRange = 127;
        this.preamplifier = 100;
        this.isMuted = false;
        this.isSoloed = false;
        this.isMutedAll = false;
        this.isMutedBySolo = false;
        this.mode = Sensor.FADER;
        this.lastWasOn = false;
        this.noiseThreshold = 0;
        this.debounceTime = 0;
        this.outputValue = 0;
        this.lastChange = new Date();

    }

    /**
     * Constructor of a Sensor Object
     *
     * @param name         name of the sensor
     * @param arduinoIn    arduino analog input matching with the sensor
     * @param midiPort     midi port to use
     * @param shortcut     shortcut matching with the sensor Object
     * @param midiReceiver where to send the midi informations
     * @param minRange     Minimal output midi value for this sensor
     * @param maxRange     Maximal output midi value for this sensor
     * @param preamplifier factor of mutliplication
     */
    public MidiSensor(String name, int arduinoIn, int midiPort, char shortcut,
                      Receiver midiReceiver, int minRange,
                      int maxRange, int preamplifier, int mode, int noiseThreshold, int debounceTime) {
        this.name = name;
        this.arduinoIn = arduinoIn;
        this.midiPort = midiPort;
        this.midiReceiver = midiReceiver;
        this.shortcut = shortcut;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.preamplifier = preamplifier;
        this.isMuted = false;
        this.isSoloed = false;
        this.isMutedAll = false;
        this.isMutedBySolo = false;
        this.lastWasOn = false;
        this.outputValue = 0;
        this.mode = mode;
        this.noiseThreshold = noiseThreshold;
        this.debounceTime = debounceTime;
        this.lastChange = new Date();
    }

    /**
     * This method send midi messages to the receiver
     *
     * @param dataFromSensor the input value of the sensor
     */
    public void sendMidiMessage(int dataFromSensor) {
        if ((!isMuted && !isMutedBySolo && !isMutedAll) || (isSoloed && !isMutedAll)) {
            Date now = new Date();
            if (mode == Sensor.TOGGLE) {
                if (dataFromSensor > noiseThreshold && (now.getTime() - lastChange.getTime()) > debounceTime) {
                    lastChange = now;
                    ShortMessage msg = new ShortMessage();
                    if (lastWasOn) {
                        try {
                            msg.setMessage(ShortMessage.NOTE_OFF, this.midiPort, 0);
                            lastWasOn = false;
                            this.outputValue = 0;
                        } catch (InvalidMidiDataException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            msg.setMessage(ShortMessage.NOTE_ON, this.midiPort, MAX_VELOCITY);
                            lastWasOn = true;
                            this.outputValue = MAX_VELOCITY;
                        } catch (InvalidMidiDataException e) {
                            e.printStackTrace();
                        }
                    }
                    this.midiReceiver.send(msg, -1);
                }
            } else if (mode == Sensor.MOMENTARY) {
                if (dataFromSensor > noiseThreshold && (now.getTime() - lastChange.getTime()) > debounceTime) {
                    lastChange = now;
                    ShortMessage msg = new ShortMessage();
                    if (!lastWasOn) {
                        try {
                            msg.setMessage(ShortMessage.NOTE_ON, this.midiPort, MAX_VELOCITY);
                            lastWasOn = true;
                            this.outputValue = MAX_VELOCITY;
                        } catch (InvalidMidiDataException e) {
                            e.printStackTrace();
                        }
                    }
                    this.midiReceiver.send(msg, -1);
                } else if (dataFromSensor <= noiseThreshold) {
                    lastChange = now;
                    ShortMessage msg = new ShortMessage();
                    try {
                        msg.setMessage(ShortMessage.NOTE_OFF, this.midiPort, 0);
                        lastWasOn = false;
                        this.outputValue = 0;
                    } catch (InvalidMidiDataException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                int velocity; //velocity of the message to send;
                velocity = calculate(dataFromSensor);
                ShortMessage msg = new ShortMessage();
                try {
                    msg.setMessage(ShortMessage.NOTE_ON, this.midiPort, velocity);
                    this.midiReceiver.send(msg, -1);
                    this.outputValue = velocity;
                } catch (InvalidMidiDataException e) {
                    e.printStackTrace();
                    System.err.println("Error sending message from " + this.name);
                }
            }
        }
    }

    /**
     * @param data value incoming from the sensor
     * @return result, a rescaled value between min and max range
     */
    private int calculate(int data) {
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

    public void sendImpulsion() {
        ShortMessage msg = new ShortMessage();
        try {
            msg.setMessage(ShortMessage.NOTE_ON, this.midiPort, MAX_VELOCITY);
            this.midiReceiver.send(msg, -1);
            //System.out.println("Message sent");
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.err.println("Error sending impulsion from " + this.name);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
            System.err.println("Programme interrompu pendant l'envoie d'une impulsion");
        }
        try {
            msg.setMessage(ShortMessage.NOTE_OFF, this.midiPort, MAX_VELOCITY);
            this.midiReceiver.send(msg, -1);
            //System.out.println("Message sent");
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.err.println("Error sending impulsion from " + this.name);
        }
    }

    /**
     * Mute this sensor
     */
    public void mute() {
        ShortMessage msg = new ShortMessage();
        try {
            msg.setMessage(ShortMessage.NOTE_OFF, this.midiPort, MAX_VELOCITY);
            this.midiReceiver.send(msg, -1);
            //System.out.println("Message sent, channel muted");
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
            System.err.println("Error sending mute instruction from " + this.name);
        }
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

    public int getMidiPort() {
        return midiPort;
    }

    public void setmidiReceiver(Receiver midiReceiver) {
        this.midiReceiver = midiReceiver;
    }

    public int getMinRange() {
        return minRange;
    }

    public void setMinRange(int minRange) {
        if (minRange >= 0 && minRange <= 127 && minRange <= this.maxRange) {
            //if a valid data is given
            this.minRange = minRange;
        } else {
            this.minRange = 0;
        }

    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        if (maxRange >= 0 && maxRange <= 127 && maxRange >= this.minRange) {
            this.maxRange = maxRange;
        } else {
            this.maxRange = 127;
        }
    }

    public int getPreamplifier() {
        return preamplifier;
    }

    public void setPreamplifier(int preamplifier) {
        if (preamplifier >= 0) {
            this.preamplifier = preamplifier;
        } else {
            this.preamplifier = 100;
        }
    }

    public void setIsSoloed(boolean isSoloed) {
        this.isSoloed = isSoloed;
    }

    public int getOutputValue() {
        return outputValue;
    }

    public void setIsMutedAll(boolean isMutedAll) {
        this.isMutedAll = isMutedAll;
    }

    public void setIsMutedBySolo(boolean isMutedBySolo) {
        this.isMutedBySolo = isMutedBySolo;
    }

    public char getShortcut() {
        return shortcut;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
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

    @Override
    public String toString() {
        return "Sensor{" +
                "name = '" + name + '\'' +
                ", arduinoIn = " + arduinoIn +
                ", midiPort = " + midiPort +
                ",  minRange =" + minRange +
                ", maxRange = " + maxRange +
                ", preamplifer = " + preamplifier + "" +
                ", mode = " + mode + "}";
    }
}
