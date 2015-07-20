package Sensor;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
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

    public OSCSensor(String name, int arduinoIn, String oscAddress, int minRange, int maxRange, int preamplifier,
                     int mode, int noiseThreshold, int debounceTime, OSCPortOut oscPortOut) {
        super(name, arduinoIn, minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime);
        this.oscAddress = oscAddress;
        this.oscPortOut = oscPortOut;
    }

    public OSCSensor(String name, int arduinoIn, String oscAddress, int mode, OSCPortOut oscPortOut) {
        this(name, arduinoIn, oscAddress, 0, 100, 100, mode, 0, 0, oscPortOut);
    }

    public static void main(String[] args) {
        OSCPortOut oscPortOut = null;
        try {
            oscPortOut = new OSCPortOut(InetAddress.getByName("192.168.1.34"), 9000);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        OSCSensor oscSensor1 = new OSCSensor("Nouveau", 0, "/print", FADER, oscPortOut);

        System.out.println("Marche 855");
        oscSensor1.sendOSCMessage(855);
        System.out.println("Marche 0");
        oscSensor1.sendOSCMessage(0);
        System.out.println("Marche 1023");
        oscSensor1.sendOSCMessage(1023);


        oscSensor1 = new OSCSensor("Nouveau", 0, "/print", MOMENTARY, oscPortOut);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Marche 1");
        oscSensor1.sendOSCMessage(200);

        oscSensor1.setDebounceTime(200);
        oscSensor1.setNoiseThreshold(400);

        System.out.println("Marche pas");
        oscSensor1.sendOSCMessage(1000);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Marche 200");
        oscSensor1.sendOSCMessage(200);
        System.out.println("Marche 1");
        oscSensor1.sendOSCMessage(600);

        oscSensor1 = new OSCSensor("Nouveau", 0, "/print", ALTERNATE, oscPortOut);
        oscSensor1.setOscAdressBis("/motor");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        oscSensor1.sendOSCMessage(200);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        oscSensor1.sendOSCMessage(500);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        oscSensor1.sendOSCMessage(600);

    }

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
                            outputValue = OSC_ON;
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
                    int value = calculate(dataFromSensor);
                    args.add(value);
                    outputValue = value;
                    toSend = new OSCMessage(oscAddress, args);
                    break;
                case ALTERNATE:
                    if (dataFromSensor > noiseThreshold && (now.getTime() - lastChange.getTime()) > debounceTime) {
                        lastChange = now;
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
                    }
                    break;
            }

            try {
                oscPortOut.send(toSend);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
            oscPortOut.send(toSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOscPortOut(OSCPortOut oscPortOut) {
        this.oscPortOut = oscPortOut;
    }

    public void setOscAdressBis(String oscAdressBis) {
        this.oscAddressBis = oscAdressBis;
    }

    public String getOscAddress() {
        return oscAddress;
    }
}
