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
public class MidiSensor extends Sensor {

    final static int MAX_VELOCITY = 127;

    /**
     * Midi note matching with a midi port
     */
    private int midiPort;
    /**
     * Receiver where the midi command will be sent
     */
    private Receiver midiReceiver;
    /**
     * a keyboard shortcut matching with the sensor
     */
    private char shortcut;


    /**
     * @param name         Name of the sensor
     * @param arduinoIn    arduino analogInput number for this sensor
     * @param midiPort     midi port to use
     * @param shortcut     shortcut matching with the sensor Object
     * @param midiReceiver where to send the midi informations
     */
    public MidiSensor(String name, int arduinoIn, int midiPort, char shortcut,
                      Receiver midiReceiver) {
        this(name, arduinoIn, midiPort, shortcut, midiReceiver, 0, 127, 100, Sensor.FADER, 0, 0);
    }

    /**
     * Constructor of a Sensor Object
     *
     * @param name           name of the sensor
     * @param arduinoIn      arduino analog input matching with the sensor
     * @param midiPort       midi port to use
     * @param shortcut       shortcut matching with the sensor Object
     * @param midiReceiver   where to send the midi informations
     * @param minRange       Minimal output midi value for this sensor
     * @param maxRange       Maximal output midi value for this sensor
     * @param preamplifier   factor of multiplication in percent
     * @param mode           mode of action
     * @param noiseThreshold threshold specific for toggle or momentary mode
     * @param debounceTime   time of debounce specific for toggle or momentary mode
     */
    public MidiSensor(String name, int arduinoIn, int midiPort, char shortcut,
                      Receiver midiReceiver, int minRange,
                      int maxRange, int preamplifier, int mode, int noiseThreshold, int debounceTime) {
        super(name, arduinoIn, minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime);
        this.midiPort = midiPort;
        this.midiReceiver = midiReceiver;
        this.shortcut = shortcut;
    }

    /**
     * This method send midi messages to the receiver
     *
     * @param dataFromSensor the input value of the sensor
     */
    public void sendMidiMessage(int dataFromSensor) {
        if ((!isMuted && !isMutedBySolo && !isMutedAll) || (isSoloed && !isMutedAll)) {
            Date now = new Date();
            if (mode == TOGGLE) {
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
            } else if (mode == MOMENTARY) {
                if (dataFromSensor > noiseThreshold && (now.getTime() - lastChange.getTime()) > debounceTime) {
                    lastChange = now;
                    ShortMessage msg1 = new ShortMessage();
                    ShortMessage msg2 = new ShortMessage();
                    try {
                        msg1.setMessage(ShortMessage.NOTE_ON, this.midiPort, MAX_VELOCITY);
                        msg2.setMessage(ShortMessage.NOTE_OFF, this.midiPort, 0);
                        this.outputValue = MAX_VELOCITY;
                    } catch (InvalidMidiDataException e) {
                        e.printStackTrace();
                    }
                    this.midiReceiver.send(msg1, -1);
                    this.midiReceiver.send(msg2, -1);
                    this.outputValue = 0;
                }

            } else {
                int velocity; //velocity of the message to send;
                velocity = (int) calculate(dataFromSensor);
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
     * Send a midi impulsion for 2 second on the selected midi port
     */
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
            Thread.sleep(6000);
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
     * Getter for the midi port used to send messages
     *
     * @return the midi port
     */
    public int getMidiPort() {
        return midiPort;
    }

    /**
     * Set the midi receiver for the Midi Sensor
     *
     * @param midiReceiver the new Midi Receiver
     */
    public void setmidiReceiver(Receiver midiReceiver) {
        this.midiReceiver = midiReceiver;
    }


    /**
     * Set the minimum range for this midi sensor
     *
     * @param minRange the new MinRange
     */
    public void setMinRange(int minRange) {
        if (minRange >= 0 && minRange <= MAX_VELOCITY && minRange <= this.maxRange) {
            //if a valid data is given
            this.minRange = minRange;
        } else {
            this.minRange = 0;
        }
    }

    /**
     * Set the maximum range for this midi sensor
     *
     * @param maxRange the new MaxRange
     */
    public void setMaxRange(int maxRange) {
        if (maxRange >= 0 && maxRange <= 127 && maxRange >= this.minRange) {
            this.maxRange = maxRange;
        } else {
            this.maxRange = 127;
        }
    }

    /**
     * Set the preamplifier value for the midi sensor
     *
     * @param preamplifier the new preamplifier value
     */
    public void setPreamplifier(int preamplifier) {
        if (preamplifier >= 0) {
            this.preamplifier = preamplifier;
        } else {
            this.preamplifier = 0;
        }
    }

    /**
     * Getter for the shortcut for this midi channel
     *
     * @return the shortcut for this midi sensor
     */
    public char getShortcut() {
        return shortcut;
    }
}
