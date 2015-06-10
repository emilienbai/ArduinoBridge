package Metier;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import Metier.SensorManagement;
import Sensor.Sensor;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 06/2015.
 */
public class TestSensorManagement {
    public static void main (String[] args) throws InterruptedException, MidiUnavailableException {
        Receiver rcvr = init();
        SensorManagement.addSensor("ChannelOne", 0, 60, rcvr);
        SensorManagement.addSensor("ChannelTwo", 1, 61, rcvr);
        SensorManagement.sendMidiMessage("0-1024-1-1022");
        Thread.sleep(1000);
        SensorManagement.solo(60);
        SensorManagement.sendMidiMessage("0-512-1-245");
        Thread.sleep(1000);
        SensorManagement.unSolo(60);
        SensorManagement.sendMidiMessage("0-1024-1-1024");
        Thread.sleep(1000);
        SensorManagement.muteAll();
        SensorManagement.displaySensors();
        System.out.println("et maintenant on flush la liste");
        SensorManagement.newSetup();
        SensorManagement.displaySensors();




    }

    public static Receiver init() throws MidiUnavailableException, InterruptedException{
        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        for (int i=0; i < info.length; i++) {
            System.out.println(i + ") " + info[i]);
            System.out.println("Name: " + info[i].getName());
            System.out.println("Description: " + info[i].getDescription());
            MidiDevice device = MidiSystem.getMidiDevice(info[i]);
            System.out.println("Device: " + device);
        }
        //For each MidiDevice, open it up,
        // obtain it’s receiver, and try it out
        MidiDevice dev = MidiSystem.getMidiDevice(info[50]);
        // 66 correspond à la piste 1 du virtual midi 0
        //c'est elle qu'il faut connecter pour faire des choses dans des logiciels extérieurs
        //VirMIDI [hw:2,0,1]
        dev.open(); //(at program start)
        Receiver receiver = dev.getReceiver();

        return receiver;
    }
}
