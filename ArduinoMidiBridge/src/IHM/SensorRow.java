package IHM;

import Metier.MidiManager;
import Metier.SensorManagement;

import javax.sound.midi.MidiDevice;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class SensorRow extends JPanel {
    private JLabel nameLabel;
    private JLabel arduinoChannelLabel;
    private JLabel midiPortLabel;
    private JLabel inputLabel;
    private JProgressBar incomingSignal;
    private JLabel preampLabel;
    private JSlider preamplifierSlider;
    private JTextField preamplifierValue;
    private JLabel minLabel;
    private JLabel maxLabel;
    private JLabel outLabel;
    private JTextField minOutValue;
    private JTextField maxOutValue;
    private JLabel outputValue;
    private JButton muteButton;
    private JButton soloButton;
    private JButton impulseButton;
    private JButton deleteButton;
    private int arduinoChannel;
    private int midiPort;
    private int minOutVal;
    private int maxOutVal;
    private boolean muteState;
    private boolean soloState;

    private static final Color BACK_COLOR = new Color(70,73,75);
    private static final Color FOR_COLOR = new Color(191,201,239);
    private static final Color MUTE_COLOR = new Color(174, 36, 33);
    private static final Color SOLO_COLOR = new Color(169, 162, 0);
    private static final Color IMP_COLOR = new Color(45, 121, 36);

    public SensorRow(String name, int arduChan, int midiPort){
        super(new FlowLayout(FlowLayout.CENTER));
        minOutVal = 0;
        maxOutVal = 127;
        muteState = false;
        soloState = false;
        this.arduinoChannel = arduChan;
        this.midiPort = midiPort;
        changeColor(this);


        /*Name*/
        nameLabel = new JLabel(name);
        nameLabel.setMaximumSize(new Dimension(115,50));
        nameLabel.setMinimumSize(new Dimension(80, 10));
        nameLabel.setPreferredSize(new Dimension(115,10));

        nameLabel.setForeground(new Color(221, 101, 4));
        this.add(nameLabel);
        /*Arduino input Chanel*/
        arduinoChannelLabel = new JLabel("Arduino : "+String.valueOf(arduChan));
        changeColor(arduinoChannelLabel);
        this.add(arduinoChannelLabel);
        /*Midi Port*/
        midiPortLabel = new JLabel(String.valueOf("Midi : "+midiPort));
        changeColor(midiPortLabel);
        this.add(midiPortLabel);
        /*Label for Input Signal*/
        inputLabel = new JLabel("In :");
        changeColor(inputLabel);
        this.add(inputLabel);
        /*Incoming signal "vu-meter"
        * TODO A method to animate theses fuckers*/
        incomingSignal = new JProgressBar(SwingConstants.HORIZONTAL, 0, 1024);
        changeColor(incomingSignal);
        this.add(incomingSignal);
        /* Preamplifier Label*/
        preampLabel = new JLabel("Preamp :");
        changeColor(preampLabel);
        this.add(preampLabel);
        /*Preamplifier Slider*/
        preamplifierSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 300, 100);
        changeColor(preamplifierSlider);
        this.add(preamplifierSlider);

        preamplifierSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        int newValue = preamplifierSlider.getValue();
                        SensorManagement.changePreamplifier(midiPort, newValue);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                preamplifierValue.setText(String.valueOf(newValue));
                            }
                        });
                    }
                }).start();
            }
        });
        /*Preamplifier manual value*/
        preamplifierValue = new JTextField("100");
        changeColor(preamplifierValue);
        this.add(preamplifierValue);

        preamplifierValue.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        int key = e.getKeyCode();
                        if(key == KeyEvent.VK_ENTER) {
                            int newValue = Integer.parseInt(preamplifierValue.getText());
                            if(newValue>0){
                                SensorManagement.changePreamplifier(midiPort, newValue);
                            }
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    if(newValue<=preamplifierSlider.getMaximum()){
                                        preamplifierSlider.setValue(newValue);
                                    }
                                    else if (newValue<=0){
                                        preamplifierValue.setText(String.valueOf(preamplifierSlider.getValue()));
                                    }
                                    else
                                    {
                                        preamplifierSlider.setValue(preamplifierSlider.getMaximum());
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        /*Minimum Label*/
        minLabel = new JLabel("%  Min :");
        changeColor(minLabel);
        this.add(minLabel);
        /*minimum output value*/
        minOutValue = new JTextField("000");
        changeColor(minOutValue);
        this.add(minOutValue);

        minOutValue.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        int key = e.getKeyCode();
                        if(key == KeyEvent.VK_ENTER) {
                            int newValue = Integer.parseInt(minOutValue.getText());
                            if(newValue>0 && newValue<=maxOutVal){
                                SensorManagement.changeMinRange(midiPort, newValue);
                                minOutVal = newValue;
                            }
                            else if(newValue<0){
                                SensorManagement.changeMinRange(midiPort, 0);
                                minOutVal = 0;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        minOutValue.setText("000");
                                    }
                                });
                            }
                            else{
                                SensorManagement.changeMinRange(midiPort, maxOutVal);
                                minOutVal = maxOutVal;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        minOutValue.setText(String.valueOf(maxOutVal));
                                    }
                                });
                            }

                        }
                    }
                }).start();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        /*MaximumLabel*/
        maxLabel = new JLabel("Max :");
        changeColor(maxLabel);
        this.add(maxLabel);
        /*maximum output value*/
        maxOutValue = new JTextField("127");
        changeColor(maxOutValue);
        this.add(maxOutValue);

        maxOutValue.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        int key = e.getKeyCode();
                        if(key == KeyEvent.VK_ENTER) {
                            int newValue = Integer.parseInt(maxOutValue.getText());
                            if(newValue<127 && newValue>=minOutVal){
                                SensorManagement.changeMaxRange(midiPort, newValue);
                                maxOutVal = newValue;
                            }
                            else if(newValue>127){
                                SensorManagement.changeMinRange(midiPort, 127);
                                minOutVal = 127;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        maxOutValue.setText("127");
                                    }
                                });
                            }
                            else{
                                SensorManagement.changeMinRange(midiPort, minOutVal);
                                maxOutVal = minOutVal;
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        maxOutValue.setText(String.valueOf(minOutVal));
                                    }
                                });
                            }

                        }
                    }
                }).start();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                
            }
        });

        /*Output Label*/
        outLabel = new JLabel("Out :");
        changeColor(outLabel);
        this.add(outLabel);
        /*Output Value*/
        outputValue = new JLabel("000");
        changeColor(outputValue);
        this.add(outputValue);
        /*Mute Button*/
        muteButton = new JButton("Mute");
        changeColor(muteButton);
        this.add(muteButton);

        muteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        if (muteState){
                            SensorManagement.unmute(midiPort);
                            muteState = false;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    muteButton.setBackground(BACK_COLOR);
                                }
                            });
                        }
                        else {
                            SensorManagement.mute(midiPort);
                            muteState = true;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    muteButton.setBackground(MUTE_COLOR);
                                }
                            });
                        }
                        
                    }
                }).start();
            }
        });

        /*SoloButton*/
        soloButton = new JButton("Solo");
        changeColor(soloButton);
        this.add(soloButton);

        soloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        if (soloState) {
                            SensorManagement.unSolo(midiPort);
                            soloState = false;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    soloButton.setBackground(BACK_COLOR);
                                }
                            });
                        } else {
                            SensorManagement.solo(midiPort);
                            soloState = true;
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    soloButton.setBackground(SOLO_COLOR);
                                }
                            });
                        }

                    }
                }).start();
            }
        });

        /*Impulse Button*/
        impulseButton = new JButton("Impulsion");
        changeColor(impulseButton);
        this.add(impulseButton);

        impulseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                impulseButton.setBackground(IMP_COLOR);
                            }
                        });
                        SensorManagement.sendMidiImpulsion(midiPort);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                impulseButton.setBackground(BACK_COLOR);
                            }
                        });

                    }
                }).start();
            }
        });

        /*deleteButton*/
        deleteButton = new JButton("Supprimer");
        changeColor(deleteButton);
        this.add(deleteButton);
    }

    /**
     * Adapt the color of a swing Component
     * @param comp the swing component to adapt
     */
    private static void changeColor(JComponent comp){
        comp.setBackground(BACK_COLOR);
        comp.setForeground(FOR_COLOR);
    }

    public int getMidiPort() {
        return midiPort;
    }

    public int getArduinoChannel() {
        return arduinoChannel;
    }

    public static void main (String args[]){
        JFrame frame = new JFrame("foo");
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.red);
        panel.add(new SensorRow("Tom Bass", 1, 1));
        frame.setContentPane(panel);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
