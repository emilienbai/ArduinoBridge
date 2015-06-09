package Sensor;

import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

public class TestSensor {

	/**
	 * @param args
	 * @throws MidiUnavailableException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws MidiUnavailableException, InterruptedException {
		Receiver rcvr = init();
		Sensor sensor1 = new Sensor("Do", 15, 60, rcvr);
		validationCheck("Envoyer une impulsion ?");		
		sensor1.sendImpulsion();
		validationCheck("Envoyer une note à Balle?");
		sensor1.sendMidiMessage(1023);
		validationCheck("Baisser le son de cette note?");
		sensor1.sendMidiMessage(512);
		validationCheck("Couper le son de cette note?");
		sensor1.sendMidiMessage(0);
		validationCheck("Stopper la note?");
		sensor1.mute();
		
	}
	
	public static Receiver init() throws MidiUnavailableException, InterruptedException{
		MidiDevice.Info[] info =MidiSystem.getMidiDeviceInfo();
		for (int i=0; i < info.length; i++) {
			System.out.println(i + ") " + info[i]);
			System.out.println("Name: " + info[i].getName());
			System.out.println("Description: " + info[i].getDescription());
			MidiDevice device = MidiSystem.getMidiDevice(info[i]);
			System.out.println("Device: " + device);
	}
		//For each MidiDevice, open it up,
		// obtain it’s receiver, and try it out
		MidiDevice dev = MidiSystem.getMidiDevice(info[66]);
		// 66 correspond à la piste 1 du virtual midi 0
		//c'est elle qu'il faut connecter pour faire des choses dans des logiciels extérieurs
		//VirMIDI [hw:2,0,1]
		dev.open(); //(at program start)
		Receiver receiver = dev.getReceiver();
		
		return receiver;
	}

	public static void validationCheck(String toAsk){
		Scanner sc = new Scanner (System.in);
		System.out.println(toAsk);
		String str = sc.nextLine();
		
	}
}
