package Metier;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 06/2015.
 */
public class MidiManager {
    private static MidiDevice choosenDevice = null;
    private static Receiver midiReceiver = null;

    public static List<MidiDevice.Info> getAvailableMidiDevices(){
        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        List<MidiDevice.Info> toReturn = new ArrayList<MidiDevice.Info>();
        //we check which of the devices are available to send midi message
        for(int i = 0; i<info.length; i++){
            MidiDevice d = null;
            try {
                d = MidiSystem.getMidiDevice(info[i]);
                d.open();
                d.getReceiver();
                toReturn.add(info[i]);
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
            if (d != null) {
                d.close();
            }
        }
        return toReturn;
    }

    public static boolean chooseMidiDevice(MidiDevice.Info info){
        try {
            choosenDevice = MidiSystem.getMidiDevice(info);
            midiReceiver = choosenDevice.getReceiver();
            return true;
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void exit(){
        choosenDevice.close();
    }


}
