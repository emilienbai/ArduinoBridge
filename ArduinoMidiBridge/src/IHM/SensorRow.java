package IHM;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class SensorRow extends JPanel {
    private JLabel nameLabel;
    private JLabel arduinoChannelLabel;
    private JLabel midiPortLabel;
    private JProgressBar incomingSignal;
    private JSlider preamplifierSlider;
    private JTextField preamplifierValue;
    private JTextField minOutValue;
    private JTextField maxOutValue;
    private JLabel outputValue;
    private JButton muteButton;
    private JButton soloButton;
    private JButton impulseButton;
    private JButton deleteButton;

    private static final Color backgroundColor = new Color(70,73,75);
    private static final Color textColor = new Color(191,201,239);

    public SensorRow(String name, int arduChan, int midiPort){
        super(new FlowLayout());
        changeColor(this);
        /*Name*/
        nameLabel = new JLabel(name);
        changeColor(nameLabel);
        this.add(nameLabel);
        /*Arduino input Chanel*/
        arduinoChannelLabel = new JLabel(String.valueOf(arduChan));
        changeColor(arduinoChannelLabel);
        this.add(arduinoChannelLabel);
        /*Midi Port*/
        midiPortLabel = new JLabel(String.valueOf(midiPort));
        changeColor(midiPortLabel);
        this.add(midiPortLabel);
        /*Incoming signal "vu-meter"*/
        incomingSignal = new JProgressBar(SwingConstants.HORIZONTAL, 0, 1024);
        changeColor(incomingSignal);
        this.add(incomingSignal);
        /*Preamplifier Slider*/
        preamplifierSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 300, 100);
        changeColor(preamplifierSlider);
        this.add(preamplifierSlider);
        /*Preamplifier manual value*/
        preamplifierValue = new JTextField("100");
        changeColor(preamplifierValue);
        this.add(preamplifierValue);
        /*minimum output value*/
        minOutValue = new JTextField("000");
        changeColor(minOutValue);
        this.add(minOutValue);
        /*maximum output value*/
        maxOutValue = new JTextField("127");
        changeColor(maxOutValue);
        this.add(maxOutValue);
        /*Output Value*/
        outputValue = new JLabel("000");
        changeColor(outputValue);
        this.add(outputValue);
        /*Mute Button*/
        muteButton = new JButton("Mute");
        changeColor(muteButton);
        this.add(muteButton);
        /*SoloButton*/
        soloButton = new JButton("Solo");
        changeColor(soloButton);
        this.add(soloButton);
        /*Impulse Button*/
        impulseButton = new JButton("Impulsion");
        changeColor(impulseButton);
        this.add(impulseButton);
        /*deleteButton*/
        deleteButton = new JButton("Supprimer");
        changeColor(deleteButton);
        this.add(deleteButton);



    }

    private static void changeColor(JComponent comp){
        comp.setBackground(backgroundColor);
        comp.setForeground(textColor);


    }


    public static void main (String args[]){
        JFrame frame = new JFrame("foo");
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.red);
        panel.add(new SensorRow("Tom Bass", 1, 1));
        frame.setContentPane(panel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
