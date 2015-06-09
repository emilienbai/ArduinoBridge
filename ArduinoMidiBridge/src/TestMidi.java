//Ceci importe la classe Scanner du package java.util
import java.util.Scanner; 
//Ceci importe toutes les classes du package java.util
import java.util.*;
//Ceci importe la ibrairie necessaire à faire du midi
import javax.sound.midi.*;


public class TestMidi {

	/**
	 * @param args
	 * @throws MidiUnavailableException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws MidiUnavailableException, InterruptedException {
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
			
			// Send middle C (60) "note on"
			// at maximum velocity (127)
			ShortMessage msg1 = new ShortMessage();
			Scanner sc = new Scanner(System.in);
			String str = sc.nextLine();
			try {
				msg1.setMessage(ShortMessage.NOTE_ON, 60, 127);
				receiver.send(msg1, -1);
				System.out.println("Message 1 envoyé");
			} catch (InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// Wait a second
			Thread.sleep(5000);
			
			// Send middle C "note off"
			ShortMessage msg2 = new ShortMessage();
			try {
				msg2.setMessage(ShortMessage.NOTE_OFF, 60, 0);
				receiver.send(msg2, -1);
				System.out.println("Message 2 envoyé");
			} catch (InvalidMidiDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			// Close the device (at program exit)
			dev.close();

	}
	

}
