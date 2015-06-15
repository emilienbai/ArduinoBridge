package IHM;

import Arduino.arduinoInData;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class TestIHM {
    public static void main(String[] args){
        arduinoInData aid = new arduinoInData();
        aid.initialize();
        MidiDeviceChoice myFrame = new MidiDeviceChoice();
        System.out.println("started");

    }
}
