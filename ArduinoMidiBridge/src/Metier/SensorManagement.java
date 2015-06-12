package Metier;

import Sensor.Sensor;
import javax.sound.midi.Receiver;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class SensorManagement {
    private static List<Sensor> sensorList = new ArrayList<Sensor>();
    private static List<Sensor> soloedSensors = new ArrayList<Sensor>();

    /**
     * Add a sensor to the active list
     * @param name name of the sensor
     * @param arduinoIn match with the analog input on the arduino
     * @param midiPort  midiPort where to send data
     *
     */
    public static void addSensor(String name, int arduinoIn, int midiPort){
        Sensor s = new Sensor(name, arduinoIn, midiPort, MidiManager.getMidiReceiver());
        sensorList.add(s);
    }

    /**
     * delete a sensor from the list
     * @param sensor the sensor to remove
     */
    public static void deleteSensor(Sensor sensor){
        sensorList.remove(sensor);
    }

    /**
     * find the sensors concerned and send midi messages for each
     * @param instructions string formed arduinoIn-data-arduinoIn-data
     */
    public static void sendMidiMessage(String instructions){
        String[] splitted = instructions.split("-");
        //every instruction is separated by a -
        for (int i = 0; i<splitted.length; i+=2 ){
            int sensorNumber = Integer.parseInt(splitted[i]);
            for(Sensor s : sensorList){
                if (s.getArduinoIn()==sensorNumber){
                    s.sendMidiMessage(Integer.parseInt(splitted[i+1]));
                }
            }
        }
    }

    /**
     * This function send a litte midi impulsion
     * @param midiPort the port where to send the impulsion
     */
    public static void sendMidiImpulsion(int midiPort) {
        for (Sensor s : sensorList) {
            if (s.getMidiPort() == midiPort) {
                s.sendImpulsion();
            }
        }
    }

    /**
     * Change the max range of a midi port -> Sensor
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changeMaxRange(int midiPort, int newValue){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.setMaxRange(newValue);
            }
        }
    }

    /**
     * Change the min range of a midi port -> Sensor
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changeMinRange(int midiPort, int newValue){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.setMinRange(newValue);
            }
        }
    }

    /**
     * Change the preamplifier for a midi port -> Sensor
     * @param midiPort the midi port matching with the sensor to modify
     * @param newValue the new value for the maximum midi out
     */
    public static void changePreamplifier(int midiPort, int newValue){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.setPreamplifier(newValue);
            }
        }
    }

    /**
     * Mute a midi port
     * @param midiPort midi port to mute
     */
    public static void mute (int midiPort){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.mute();
            }
        }
    }

    /**
     * Un-mute a midi port
     * @param midiPort midi port to un-mute
     */
    public static void unmute (int midiPort){
        for (Sensor s : sensorList){
            if (s.getMidiPort() == midiPort){
                s.unMute();
            }
        }
    }

    /**
     * mute all the midi ports
     */
    public static void muteAll (){
        for (Sensor s : sensorList){
            s.mute();
        }
    }

    /**
     * un-mute all the midi ports
     */
    public static void unMuteAll(){
        for (Sensor s : sensorList){
            s.unMute();
        }
    }

    /**
     * solo one midi port
     * @param midiPort midi port to solo
     */
    public static void solo(int midiPort){
        for (Sensor s : sensorList){
            if(s.getMidiPort() != midiPort){
                if (!s.isSoloed()){
                    s.mute();   //we mute every port which
                    // is not solo already
                }
            }
            else{
                s.setIsSoloed(true);
                soloedSensors.add(s);
                //we put soloed port in a List
            }
        }
    }

    /**
     * unsolo one midi channel
     * @param midiPort
     */
    public static void unSolo (int midiPort){
        for (Sensor s : sensorList){
            if(s.getMidiPort() == midiPort){
                s.setIsSoloed(false);
                soloedSensors.remove(s);
            }
        }
        if (soloedSensors.isEmpty()){
            for (Sensor s : sensorList){
                s.unMute();
            }
        }
    }

    /**
     * In case the user wants to start a new session of the app
     */
    public static void newSetup(){
        sensorList = new ArrayList<Sensor>();
    }

    /**
     * Display information for all the sensors
     */
    protected static void displaySensors(){
        for (Sensor s : sensorList){
            System.out.println(s);
        }
    }

    /**
     * change receiver for all the sensors
     */
    public static void changeReceiver(){
        for (Sensor s :sensorList){
            s.setMidireceiver(MidiManager.getMidiReceiver());
        }
    }
}
