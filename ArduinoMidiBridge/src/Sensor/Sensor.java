/**
 * 
 */
package Sensor;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

/**
 * @author emilien Bai
 */
public class Sensor {
	final static int MAX_FROM_SENSOR = 1024;
	final static int MAX_VELOCITY = 127; 
	/**
	 * Name given to the sensor
	 */
	private String name;
	/**
	 * Match with the port on which the sensor is connected 
	 */
	private int arduinoIn;
	/**
	 * Midi note matching with a midi port
	 */
	private int midiPort;
	/**
	 * Receiver where the midi command will be sent 
	 */
	private Receiver midiReceiver;
	/**
	 * minimum midi value sent in messages
	 */
	private int minRange;
	/**
	 * maximum midi value sent in messages
	 */
	private int maxRange;
	/**
	 * multiplication factor to reduce or amplify sensor
	 * sensibility 
	 */
	private int preamplifier;
	/**
	 * Muted state of the sensor
	 */
	private boolean isMuted;
	/**
	 * Soloed state of the sensor
	 */
	private boolean isSoloed;
	/**
	 * Muted by mute all button
	 */
	private boolean isMutedAll;
	/**
	 * Muted cause of solo Button
	 */
	private boolean isMutedBySolo;
	/**
	 * last outputValue
	 */
	private int outputValue;
    /**
     * a keyboard shortcut matching with the sensor
     */
    private char shortcut;
	/**
	 * the sensor act like a toggle button once send on, once send off
	 */
	private boolean toggle;
	/**
	 * Last action of the toggle button
	 */
	private boolean lastWasOn;


	/**
	 * @param name Name of the sensor
	 * @param arduinoIn arduino analogInput number for this sensor
     * @param midiPort midi port to use
     * @param shortcut shortcut matching with the sensor Object
     * @param midiReceiver where to send the midi informations
	 */
    public Sensor(String name, int arduinoIn, int midiPort, char shortcut,
                  Receiver midiReceiver) {
		this.name = name;
		this.arduinoIn = arduinoIn;
		this.midiPort = midiPort;
        this.shortcut = shortcut;
        this.midiReceiver = midiReceiver;
		this.minRange = 0;
		this.maxRange = 127;
		this.preamplifier = 100;
		this.isMuted = false;
		this.isSoloed = false;
		this.isMutedAll = false;
		this.isMutedBySolo = false;
		this.toggle = false;
		this.lastWasOn = false;
		this.outputValue = 0;

    }

    /**
     * Constructor of a Sensor Object
     * @param name name of the sensor
     * @param arduinoIn arduino analog input matching with the sensor
     * @param midiPort  midi port to use
     * @param shortcut shortcut matching with the sensor Object
     * @param midiReceiver where to send the midi informations
     * @param minRange Minimal output midi value for this sensor
     * @param maxRange Maximal output midi value for this sensor
     * @param preamplifier factor of mutliplication
     */
    public Sensor(String name, int arduinoIn, int midiPort, char shortcut,
                  Receiver midiReceiver, int minRange,
				  int maxRange, int preamplifier, boolean toggle){
		this.name = name;
		this.arduinoIn = arduinoIn;
		this.midiPort = midiPort;
		this.midiReceiver = midiReceiver;
        this.shortcut = shortcut;
        this.minRange = minRange;
		this.maxRange = maxRange;
		this.preamplifier = preamplifier;
		this.isMuted = false;
		this.isSoloed = false;
		this.isMutedAll = false;
		this.isMutedBySolo = false;
		this.lastWasOn = false;
		this.outputValue = 0;
		this.toggle = toggle;
	}
	/**
	 * This method send midi messages to the receiver
	 * @param dataFromSensor the input value of the sensor
	 */
	public void sendMidiMessage(int dataFromSensor){
		if((!isMuted && !isMutedBySolo && !isMutedAll)||(isSoloed&&!isMutedAll)) {
			if (toggle) {
				if (dataFromSensor != 0) {
					ShortMessage msg = new ShortMessage();
					if (lastWasOn) {
						try {
							msg.setMessage(ShortMessage.NOTE_OFF, this.midiPort, 0);
							lastWasOn = false;
							this.outputValue = 0;
						} catch (InvalidMidiDataException e) {
							e.printStackTrace();
						}
					} else {
						try {
							msg.setMessage(ShortMessage.NOTE_ON, this.midiPort, 127);
							lastWasOn = true;
							this.outputValue = 127;
						} catch (InvalidMidiDataException e) {
							e.printStackTrace();
						}
					}
					this.midiReceiver.send(msg, -1);
				}
			} else {
				int velocity; //velocity of the message to send;
				velocity = calculate(dataFromSensor);
				ShortMessage msg = new ShortMessage();
				try {
					msg.setMessage(ShortMessage.NOTE_ON, this.midiPort, velocity);
					this.midiReceiver.send(msg, -1);
					this.outputValue = velocity;
				/*SimpleDateFormat ft =
						new SimpleDateFormat("hh:mm:ss:SSS");
				System.out.println("Message Envoyé :" + ft.format(new Date()));*/
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
					System.err.println("Error sending message from " + this.name);
				}
			}
		}
	}
	/**
	 * @param data value incoming from the sensor
	 * @return result, a rescaled value between min and max range
	 */
	private int calculate(int data){
		float result;
		result = (this.preamplifier*data)/100;
		//apply the premaplifier modification
		result = result/MAX_FROM_SENSOR;
		//rescale the value to maximum 1
		result = result*(this.maxRange-this.minRange)+this.minRange;
		//rescale with min and max range value
		if (result <= this.maxRange){
			return (int) result;
		}
		else{
			return this.maxRange;
		}
		//when the preamp is saturating the output 
	}
	public void sendImpulsion(){
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.NOTE_ON, this.midiPort, MAX_VELOCITY);
			this.midiReceiver.send(msg, -1);
			//System.out.println("Message sent");
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			System.err.println("Error sending impulsion from " + this.name);
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			System.err.println("Programme interrompu pendant l'envoie d'une impulsion");
		}
		try {
			msg.setMessage(ShortMessage.NOTE_OFF, this.midiPort, MAX_VELOCITY);
			this.midiReceiver.send(msg, -1);
			//System.out.println("Message sent");
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			System.err.println("Error sending impulsion from " + this.name);
		}
	}

	/**
	 * Mute this sensor
	 */
	public void mute(){
		ShortMessage msg = new ShortMessage();
		try {
			msg.setMessage(ShortMessage.NOTE_OFF, this.midiPort, MAX_VELOCITY);
			this.midiReceiver.send(msg, -1);
			//System.out.println("Message sent, channel muted");
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			System.err.println("Error sending mute instruction from " + this.name);
		}
		this.isMuted = true;
	}

	/**
	 * Un-mute this sensor
	 */
	public void unMute(){
		this.isMuted = false;
	}
	public String getName() {
		return name;
	}

	public int getArduinoIn() {
		return arduinoIn;
	}

	public int getMidiPort() {
		return midiPort;
	}

	public void setmidiReceiver(Receiver midiReceiver) {
		this.midiReceiver = midiReceiver;
	}
	public int getMinRange() {
		return minRange;
	}
	public void setMinRange(int minRange) {
		if (minRange >=0 && minRange<=127 && minRange<=this.maxRange){
			//if a valid data is given
			this.minRange = minRange;
		}
		else {
			this.minRange = 0;
		}
		
	}
	public int getMaxRange() {
		return maxRange;
	}
	public void setMaxRange(int maxRange) {
		if (maxRange >= 0 && maxRange <= 127 && maxRange >= this.minRange) {
		this.maxRange = maxRange;
		}
		else {
			this.maxRange = 127;
		}
	}
	public int getPreamplifier() {
		return preamplifier;
	}
	public void setPreamplifier(int preamplifier) {
		if (preamplifier >= 0){
			this.preamplifier = preamplifier;
		}
		else{
			this.preamplifier = 100;
		}
	}

	public void setIsSoloed(boolean isSoloed) {
		this.isSoloed = isSoloed;
	}

	public boolean isSoloed() {
		return isSoloed;
	}

	public int getOutputValue() {
		return outputValue;
	}

	public void setIsMutedAll(boolean isMutedAll) {
		this.isMutedAll = isMutedAll;
	}

	public void setIsMutedBySolo(boolean isMutedBySolo) {
		this.isMutedBySolo = isMutedBySolo;
	}

    public char getShortcut() {
        return shortcut;
	}

	public boolean isToggle() {
		return toggle;
	}

	public void setToggle(boolean toggle) {
		this.toggle = toggle;
	}

	@Override
	public String toString() {
		return "Sensor{" +
				"name = '" + name + '\'' +
				", arduinoIn = " + arduinoIn +
				", midiPort = " + midiPort +
				",  minRange =" + minRange +
				", maxRange = " + maxRange +
				", preamplifer = " + preamplifier + "" +
				", toggle = " + toggle + "}";
	}
}
