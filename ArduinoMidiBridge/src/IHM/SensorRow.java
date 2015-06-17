package IHM;

import Metier.SensorManagement;
import Sensor.Sensor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
class SensorRow extends JPanel {
    private VuMeter incomingSignal;
    private JSlider preamplifierSlider;
    private JTextField preamplifierValue;
    private JTextField minOutValue;
    private JTextField maxOutValue;
    private JLabel outputValue;
    private JButton muteButton;
    private JButton soloButton;
    private JButton impulseButton;
    private int arduinoChannel;
    private int midiPort;
    private int minOutVal;
    private int maxOutVal;
    private int preamplifierIntValue;
    private boolean muteState;
    private boolean soloState;
    private String name;

    private static final Color BACKGROUND_COLOR = OperatingWindows.BACKGROUND_COLOR;
    private static final Color BUTTON_COLOR = OperatingWindows.BUTTON_COLOR;
    private static final Color FOREGROUND_COLOR = OperatingWindows.FOREGROUND_COLOR;
    private static final Color MUTE_COLOR = OperatingWindows.MUTE_COLOR;
    private static final Color SOLO_COLOR = OperatingWindows.SOLO_COLOR;
    private static final Color IMPULSE_COLOR = OperatingWindows.IMPULSE_COLOR;
    private static final Color NAME_COLOR = OperatingWindows.NAME_COLOR;


    public SensorRow(String name, int arduChan, int midiPort, int minRange, int maxRange, int preamplifier){
        super(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();
        this.name = name;
        this.minOutVal = minRange;
        this.maxOutVal = maxRange;
        this.preamplifierIntValue = preamplifier;
        muteState = false;
        soloState = false;
        this.arduinoChannel = arduChan;
        this.midiPort = midiPort;
        changeColor(this);


        /********************Name************/
        JLabel nameLabel = new JLabel(name);
        nameLabel.setMaximumSize(new Dimension(115, 50));
        nameLabel.setMinimumSize(new Dimension(80, 10));
        nameLabel.setPreferredSize(new Dimension(105, 10));
        nameLabel.setForeground(NAME_COLOR);
        constraint.gridx = 0;
        constraint.gridy = 0;
        //constraint.anchor= GridBagConstraints.LINE_START;
        //constraint.gridwidth = GridBagConstraints.REMAINDER;
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weightx = 1;
        constraint.ipadx = 5;
        this.add(nameLabel, constraint);

        /*********Arduino input Chanel*******/
        JLabel arduinoChannelLabel = new JLabel("Arduino : " + String.valueOf(arduChan));
        changeColor(arduinoChannelLabel);
        arduinoChannelLabel.setPreferredSize(new Dimension(85,10));
        constraint.weightx = 0;
        constraint.gridx = constraint.gridx + 1;
        this.add(arduinoChannelLabel, constraint);

        /**********Midi Port**********/
        JLabel midiPortLabel = new JLabel(String.valueOf("Midi : " + midiPort));
        changeColor(midiPortLabel);
        midiPortLabel.setPreferredSize(new Dimension(70, 10));
        constraint.gridx = constraint.gridx + 1;
        this.add(midiPortLabel, constraint);

        /**********Label for Input Signal**********/
        JLabel inputLabel = new JLabel("In :");
        changeColor(inputLabel);
        constraint.gridx = constraint.gridx + 1;
        this.add(inputLabel, constraint);

        /**********Incoming signal "vu-meter"*********/
        incomingSignal = new VuMeter(SwingConstants.HORIZONTAL, 0, 1024);
        changeColor(incomingSignal);
        incomingSignal.setPreferredSize(new Dimension(100, 15));
        incomingSignal.setMaximumSize(new Dimension(300, 15));
        constraint.weightx = 1;
        constraint.gridx = constraint.gridx + 1;
        this.add(incomingSignal, constraint);

        /**********Preamplifier Label**********/
        JLabel preampLabel = new JLabel("Preamp :");
        changeColor(preampLabel);
        constraint.weightx = 0;
        constraint.gridx = constraint.gridx + 1;
        this.add(preampLabel, constraint);
        /**********Preamplifier Slider**********/
        preamplifierSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 300, 100);
        preamplifierSlider.setValue(this.preamplifierIntValue);
        changeColor(preamplifierSlider);
        constraint.gridx = constraint.gridx + 1;
        this.add(preamplifierSlider, constraint);

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


        /**********Preamplifier manual value**********/
        preamplifierValue = new JTextField(String.valueOf(this.preamplifierIntValue));
        changeColor(preamplifierValue);
        preamplifierValue.setPreferredSize(new Dimension(30, 18));
        constraint.gridx = constraint.gridx + 1;
        this.add(preamplifierValue, constraint);

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

        /**********Minimum Label**********/
        JLabel minLabel = new JLabel("%  Min :");
        changeColor(minLabel);
        constraint.gridx = constraint.gridx + 1;
        this.add(minLabel, constraint);
        /**minimum output value**/
        minOutValue = new JTextField(String.valueOf(this.minOutVal));
        changeColor(minOutValue);
        minOutValue.setPreferredSize(new Dimension(30, 18));
        constraint.gridx = constraint.gridx + 1;
        this.add(minOutValue, constraint);

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

        /**********MaximumLabel**********/
        JLabel maxLabel = new JLabel("Max :");
        changeColor(maxLabel);
        constraint.gridx = constraint.gridx + 1;
        this.add(maxLabel, constraint);
        /*maximum output value*/
        maxOutValue = new JTextField(String.valueOf(this.maxOutVal));
        changeColor(maxOutValue);
        maxOutValue.setPreferredSize(new Dimension(30, 18));
        constraint.gridx = constraint.gridx + 1;
        this.add(maxOutValue, constraint);

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

        /**********Output Label**********/
        JLabel outLabel = new JLabel("Out :");
        changeColor(outLabel);
        constraint.gridx = constraint.gridx + 1;
        this.add(outLabel, constraint);
        /**********Output Value**********/
        outputValue = new JLabel("000");
        outputValue.setPreferredSize(new Dimension(21, 18));
        changeColor(outputValue);
        constraint.gridx = constraint.gridx + 1;
        this.add(outputValue, constraint);
        /**********Mute Button**********/
        muteButton = new JButton("Mute");
        muteButton.setBackground(BUTTON_COLOR);
        muteButton.setForeground(FOREGROUND_COLOR);
        //muteButton.setPreferredSize(new Dimension(70,25));
        constraint.gridx = constraint.gridx + 1;
        constraint.weightx = 1;
        this.add(muteButton, constraint);

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
                                    muteButton.setBackground(BUTTON_COLOR);
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

        /**********SoloButton**********/
        soloButton = new JButton("Solo");
        soloButton.setBackground(BUTTON_COLOR);
        soloButton.setForeground(FOREGROUND_COLOR);
        constraint.gridx = constraint.gridx + 1;
        this.add(soloButton, constraint);

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
                                    soloButton.setBackground(BUTTON_COLOR);
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

        /**********Impulse Button**********/
        impulseButton = new JButton("Impulsion");
        impulseButton.setBackground(BUTTON_COLOR);
        impulseButton.setForeground(FOREGROUND_COLOR);
        constraint.gridx = constraint.gridx + 1;
        this.add(impulseButton, constraint);

        impulseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                impulseButton.setBackground(IMPULSE_COLOR);
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
                                impulseButton.setBackground(BUTTON_COLOR);
                            }
                        });

                    }
                }).start();
            }
        });



    }

    public SensorRow(String name, int arduChan, int midiPort){
        this(name, arduChan, midiPort, 0, 127, 100);
    }
    public SensorRow(Sensor s){
        this(s.getName(), s.getArduinoIn(), s.getMidiPort(), s.getMinRange(), s.getMaxRange(), s.getPreamplifier());
    }



    /**
     * Adapt the color of a swing Component
     * @param comp the swing component to adapt
     */
    private static void changeColor(JComponent comp){
        comp.setBackground(BACKGROUND_COLOR);
        comp.setForeground(FOREGROUND_COLOR);
    }

    public int getMidiPort() {
        return midiPort;
    }

    public int getArduinoChannel() {
        return arduinoChannel;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the value of the vu-meter
     * @param data the value to set
     */
    public void setIncomingSignal(int data){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                incomingSignal.setValue(data);
                SensorRow.this.repaint();
            }
        });
    }

    public void setOutputValue(int outValue) {
        SwingUtilities.invokeLater(() -> {
            outputValue.setText(String.valueOf(outValue));
            SensorRow.this.repaint();
        });
    }

    public static void main (String [] args){
        JFrame frame = new JFrame("Hello World");
        SensorRow sensorRow = new SensorRow("foo", 12, 42);
        frame.add(sensorRow);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
