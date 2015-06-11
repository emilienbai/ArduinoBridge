package Metier;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 06/2015.
 */
public class MidiManager {
    private static MidiDevice choosenDevice = null;
    private static Receiver midiReceiver = null;

    /**
     * Obtain all midi device on which it is possible to send
     * midi messages
     * @return Vector of the devices information
     */
    public static Vector<MidiDevice.Info> getAvailableMidiDevices(){
        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        Vector<MidiDevice.Info> toReturn = new Vector<MidiDevice.Info>();
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

    /**
     * Choose the midi device you want to use to send your messages
     * @param info The MidiDevice.Info matching the device we want to set
     * @return  True if the midiReceiver is succesfully set.
     */
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
