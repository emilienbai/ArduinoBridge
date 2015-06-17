package Metier;

import Sensor.Sensor;
import org.xml.sax.SAXException;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 06/2015.
 */
public class TestSensorManagement {
    public static void main (String[] args) throws InterruptedException, MidiUnavailableException, SAXException {
        Vector<MidiDevice.Info> rcvrInfo = MidiManager.getAvailableMidiDevices();
        MidiManager.chooseMidiDevice(rcvrInfo.get(0));
        SensorManagement.addSensor("ChannelOne", 0, 60);
        SensorManagement.addSensor("ChannelTwo", 1, 61);
        SensorManagement.sendMidiMessage("0-1024-1-1022");
        Thread.sleep(1000);
        SensorManagement.solo(60);
        SensorManagement.sendMidiMessage("0-512-1-245");
        Thread.sleep(1000);
        SensorManagement.changePreamplifier(60, 80);
        SensorManagement.unSolo(60);
        SensorManagement.sendMidiMessage("0-1024-1-1024");
        Thread.sleep(1000);
        SensorManagement.muteAll();
        SensorManagement.displaySensors();
        File file = new File("temp.xml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("echec à la création du fichier");
        }
        System.out.println("On effectue une sauvegarde");
        boolean saved = SensorManagement.saveSetup(file);
        System.out.println(saved);
        System.out.println("et maintenant on flush la liste");
        SensorManagement.newSetup();
        System.out.println("On charge le fichier");
        boolean loaded = SensorManagement.loadSetup(file);
        System.out.println(loaded);
        SensorManagement.displaySensors();




    }

}
