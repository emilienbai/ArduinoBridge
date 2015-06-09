import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
 
public class Example {
 
   /**
    * @param args
    */
   public static void main(String[] args) throws Exception  {
     ShortMessage myMsg = new ShortMessage();
      // Start playing the note Middle C (60), 
      // moderately loud (velocity = 93).
      long timeStamp = -1;
      Synthesizer synth = MidiSystem.getSynthesizer();
      synth.open();
      Receiver       rcvr = synth.getReceiver();
      myMsg.setMessage(ShortMessage.NOTE_ON, 60, 93);
      rcvr.send(myMsg, timeStamp);
      try {
         Thread.sleep(5000);
      } catch (InterruptedException e) {}
      myMsg.setMessage(ShortMessage.NOTE_OFF, 60, 0);
      rcvr.send(myMsg, timeStamp);
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {}
   }
}