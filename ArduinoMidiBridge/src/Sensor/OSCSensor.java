package Sensor;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 * Project : ArduinoMidiBridge
 */
public class OSCSensor extends Sensor {

    private static final int OSC_ON = 1;
    private static final int OSC_OFF = 0;

    /**
     * Shape of the Message
     */
    private String oscAddress;

    /**
     * Port where to send osc messages
     */
    private OSCPortOut oscPortOut;

    /**
     * Secondary message to send in alternate mode
     */
    private String oscAddressBis;


    /**
     * Constructor for a OSC Sensor
     *
     * @param name           Name of the sensor
     * @param arduinoIn      arduino analog input matching with the sensor
     * @param oscAddress     Shape of the Messages to send
     * @param minRange       Minimal output OSC value for this sensor
     * @param maxRange       Maximal output OSC value for this sensor
     * @param preamplifier   factor of mutliplication in percent
     * @param mode           mode of action
     * @param noiseThreshold threshold specific for toggle, alternate or momentary mode
     * @param debounceTime   time of debounce specific for toggle, alternate or momentary mode
     * @param oscPortOut     port where to send osc messages
     */
    public OSCSensor(String name, int arduinoIn, String oscAddress, float minRange, float maxRange, int preamplifier,
                     int mode, int noiseThreshold, int debounceTime, OSCPortOut oscPortOut) {
        super(name, arduinoIn, minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime);
        this.oscAddress = oscAddress;
        this.oscPortOut = oscPortOut;
    }

    /**
     * Constructor for an Osc Sensor with limited parameters
     *
     * @param name       Name of the sensor
     * @param arduinoIn  arduino analog input matching with the sensor
     * @param oscAddress Shape of the Messages to send
     * @param mode       mode of action
     * @param oscPortOut port where to send osc messages
     */
    public OSCSensor(String name, int arduinoIn, String oscAddress, int mode, OSCPortOut oscPortOut) {
        this(name, arduinoIn, oscAddress, 0, 100, 100, mode, 0, 0, oscPortOut);
    }

    /**
     * Construtor for an Osc sensor in alternate mode
     *
     * @param name          Name of the sensor
     * @param arduinoIn     arduino analog input matching with the sensor
     * @param oscAddress    Shape of the Messages to send
     * @param oscAddressBis Secondary shape of the messages sent
     * @param mode          mode of action
     * @param oscPortOut    port where to send osc messages
     */
    public OSCSensor(String name, int arduinoIn, String oscAddress, String oscAddressBis, int mode, OSCPortOut oscPortOut) {
        this(name, arduinoIn, oscAddress, mode, oscPortOut);
        this.oscAddressBis = oscAddressBis;
    }

    /**
     * Send an Osc Message to the osc address
     *
     * @param dataFromSensor data received from the arduino
     */
    public void sendOSCMessage(int dataFromSensor) {
        if ((!isMuted && !isMutedBySolo && !isMutedAll) || (isSoloed && !isMutedAll)) {
            Date now = new Date();
            OSCMessage toSend = null;
            List<Object> args = new ArrayList<>();
            switch (mode) {
                case TOGGLE:
                    if (dataFromSensor > noiseThreshold && (now.getTime() - lastChange.getTime()) > debounceTime) {
                        lastChange = now;
                        if (lastWasOn) {
                            args.add(OSC_OFF);
                            outputValue = OSC_OFF;
                            toSend = new OSCMessage(oscAddress, args);
                            lastWasOn = false;
                        } else {
                            args.add(OSC_ON);
                            outputValue = 100;
                            toSend = new OSCMessage(oscAddress, args);
                            lastWasOn = true;
                        }
                    }
                    break;
                case MOMENTARY:
                    if (dataFromSensor > noiseThreshold && (now.getTime() - lastChange.getTime()) > debounceTime) {
                        lastChange = now;
                        args.add(OSC_ON);
                        outputValue = OSC_ON;
                        toSend = new OSCMessage(oscAddress, args);
                    }
                    break;
                case FADER:
                    float value = calculate(dataFromSensor);
                    args.add(value);
                    outputValue = value;
                    toSend = new OSCMessage(oscAddress, args);
                    break;
                case ALTERNATE:
                    if (dataFromSensor > noiseThreshold && (now.getTime() - lastChange.getTime()) > debounceTime) {
                        lastChange = now;
                        if (lastWasOn) {
                            args.add(OSC_ON);
                            outputValue = OSC_OFF;
                            toSend = new OSCMessage(oscAddressBis, args);
                            lastWasOn = false;
                        } else {
                            args.add(OSC_ON);
                            outputValue = OSC_ON;
                            toSend = new OSCMessage(oscAddress, args);
                            lastWasOn = true;
                        }
                    }
                    break;
            }

            try {
                if (toSend != null && oscPortOut != null) {
                    oscPortOut.send(toSend);
                }
                if (mode == MOMENTARY) {
                    outputValue = 0;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a test message on the OSC address
     */
    public void sendTestMessage() {
        OSCMessage toSend = null;
        List<Object> args = new ArrayList<>();

        switch (this.mode) {
            case FADER:
                args.add(100);
                toSend = new OSCMessage(oscAddress, args);
                break;
            case TOGGLE:
                if (lastWasOn) {
                    args.add(OSC_OFF);
                    outputValue = OSC_OFF;
                    toSend = new OSCMessage(oscAddress, args);
                    lastWasOn = false;
                } else {
                    args.add(OSC_ON);
                    outputValue = OSC_ON;
                    toSend = new OSCMessage(oscAddress, args);
                    lastWasOn = true;
                }
                break;
            case MOMENTARY:
                args.add(OSC_ON);
                outputValue = OSC_ON;
                toSend = new OSCMessage(oscAddress, args);
                break;
            case ALTERNATE:
                if (lastWasOn) {
                    args.add(OSC_ON);
                    outputValue = OSC_ON;
                    toSend = new OSCMessage(oscAddressBis, args);
                    lastWasOn = false;
                } else {
                    args.add(OSC_ON);
                    outputValue = OSC_ON;
                    toSend = new OSCMessage(oscAddress, args);
                    lastWasOn = true;
                }
                break;
        }
        try {
            if (toSend != null) {
                oscPortOut.send(toSend);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the port to use to send Osc Messages
     *
     * @param oscPortOut the port to use to send osc messages
     */
    public void setOscPortOut(OSCPortOut oscPortOut) {
        this.oscPortOut = oscPortOut;
    }

    /**
     * Get the Osc address of the sensor
     *
     * @return the osc address of the sensor
     */
    public String getOscAddress() {
        return oscAddress;
    }

    /**
     * Get the Secondary Osc address of the sensor
     *
     * @return the secondary osc address of the sensor
     */
    public String getOscAddressBis() {
        return oscAddressBis;
    }
}