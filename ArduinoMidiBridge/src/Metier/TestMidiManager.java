package Metier;

import javax.sound.midi.MidiDevice;
import java.util.List;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class TestMidiManager {
    public static void main (String [] args){
        List<MidiDevice.Info> deviceList =  MidiManager.getAvailableMidiDevices();
        int counter = 0;
        for(MidiDevice.Info d : deviceList){
            System.out.println((counter++) + " - " + d.getName());
            System.out.println(d.getDescription());
        }
    }
}
