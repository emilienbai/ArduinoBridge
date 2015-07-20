package IHM;

import Sensor.Sensor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 */
public abstract class SensorRow extends JPanel {
    protected static final int MIDI = 0;
    protected static final int OSC = 1;

    protected static GridBagConstraints constraint;
    protected VuMeter incomingSignal;
    protected JSlider preamplifierSlider;
    protected JTextField preamplifierValue;
    protected JTextField minOutValue;
    protected JTextField maxOutValue;
    protected VuMeter outputValue;
    protected JButton muteButton;
    protected JButton soloButton;
    protected JButton impulseButton;
    protected JButton toggleButton;
    protected DeleteButton deleteButton;
    protected JLabel maxLabel;
    protected JLabel minLabel;
    protected JLabel keyLabel;
    protected JLabel shortcutLabel;

    protected int arduinoChannel;
    protected int minOutVal;
    protected int maxOutVal;
    protected boolean muteState;
    protected boolean soloState;
    protected String name;
    protected Object key;
    protected int mode;
    protected int noiseThreshold;
    protected int debounceTime;

    public SensorRow(String name, int arduChan, Object key, int minRange, int maxRange, int preamplifier, int mode,
                     int noiseThreshold, int debounceTime) {
        super(new GridBagLayout());
        constraint = new GridBagConstraints();
        this.minOutVal = minRange;
        this.maxOutVal = maxRange;
        muteState = false;
        soloState = false;
        this.arduinoChannel = arduChan;
        this.mode = mode;
        this.noiseThreshold = noiseThreshold;
        this.debounceTime = debounceTime;
        this.key = key;
        changeColor(this);
        this.setBorder(OperatingWindows.ETCHED_BORDER);


        /********************Name************/

        JLabel nameLabel = new JLabel(name);
        nameLabel.setMaximumSize(new Dimension(180, 50));
        nameLabel.setMinimumSize(new Dimension(80, 10));
        nameLabel.setPreferredSize(new Dimension(110, 20));
        nameLabel.setForeground(OperatingWindows.NAME_COLOR);

        constraint.gridx = 0;
        constraint.gridy = 0;
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weightx = 1;
        constraint.weighty = 0;
        constraint.gridheight = 1;
        constraint.insets = new Insets(3, 3, 3, 3);
        this.add(nameLabel, constraint);

        /*******ShortCutLabel***********/
        shortcutLabel = new JLabel("Shortcut");
        shortcutLabel.setForeground(OperatingWindows.FOREGROUND_COLOR);
        shortcutLabel.setMaximumSize(new Dimension(110, 50));
        shortcutLabel.setMinimumSize(new Dimension(80, 10));
        shortcutLabel.setPreferredSize(new Dimension(110, 20));
        constraint.gridx = 0;
        constraint.gridy = 1;
        this.add(shortcutLabel, constraint);

        /*********Arduino input Chanel*******/
        JLabel arduinoChannelLabel = new JLabel("Arduino : " + String.valueOf(arduChan));
        changeColor(arduinoChannelLabel);
        arduinoChannelLabel.setPreferredSize(new Dimension(85, 20));
        constraint.gridx = 1;
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.weightx = 0;
        this.add(arduinoChannelLabel, constraint);

        /**********Key Label**********/
        keyLabel = new JLabel(String.valueOf("Here comes the key"));
        changeColor(keyLabel);
        keyLabel.setPreferredSize(new Dimension(70, 20));
        constraint.gridy = 1;
        this.add(keyLabel, constraint);

        /**********Label for Input Signal**********/
        JLabel inputLabel = new JLabel("In :");
        changeColor(inputLabel);
        constraint.gridx++;
        constraint.gridy = 0;
        constraint.gridheight = 2;
        this.add(inputLabel, constraint);

        /**********Incoming signal "vu-meter"*********/
        incomingSignal = new VuMeter(SwingConstants.HORIZONTAL, 0, 1024);
        changeColor(incomingSignal);
        incomingSignal.setPreferredSize(new Dimension(80, 13));
        incomingSignal.setMaximumSize(new Dimension(300, 15));
        incomingSignal.setBorder(OperatingWindows.ETCHED_BORDER);
        constraint.weightx = 1;
        constraint.gridx++;
        this.add(incomingSignal, constraint);

        /**********Preamplifier Label**********/
        JLabel preampLabel = new JLabel("Preamp :");
        changeColor(preampLabel);
        constraint.weightx = 0;
        constraint.gridheight = 2;
        constraint.gridx++;
        this.add(preampLabel, constraint);

        /**********Preamplifier Slider**********/
        preamplifierSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 500, 100);
        preamplifierSlider.setValue(preamplifier);
        changeColor(preamplifierSlider);
        constraint.gridx++;
        this.add(preamplifierSlider, constraint);

        /**********Preamplifier manual value**********/
        preamplifierValue = new JTextField(String.valueOf(preamplifier));
        preamplifierValue.setBorder(OperatingWindows.LOWERED_BORDER);
        changeColor(preamplifierValue);
        preamplifierValue.setPreferredSize(new Dimension(35, 18));
        constraint.gridx++;
        this.add(preamplifierValue, constraint);

        /***********Percent Label*********/
        JLabel percentLabel = new JLabel("%");
        changeColor(percentLabel);
        ++constraint.gridx;
        this.add(percentLabel, constraint);

        /**********Maximum-Threshold - Label**********/
        if (this.mode == Sensor.FADER) {
            maxLabel = new JLabel("Max :");
            maxOutValue = new JTextField(String.valueOf(this.maxOutVal));
        } else {
            maxLabel = new JLabel("Seuil :");
            maxOutValue = new JTextField(String.valueOf(this.noiseThreshold));
        }
        changeColor(maxLabel);
        ++constraint.gridx;
        constraint.gridheight = 1;
        this.add(maxLabel, constraint);


        /*maximum output value*/

        changeColor(maxOutValue);
        maxOutValue.setPreferredSize(new Dimension(35, 18));
        maxOutValue.setBorder(OperatingWindows.LOWERED_BORDER);
        constraint.gridx = constraint.gridx + 1;
        this.add(maxOutValue, constraint);

        maxOutValue.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                new Thread(() -> {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_ENTER) {
                        try {
                            int newValue = Integer.parseInt(maxOutValue.getText());
                            maxThreshModification(newValue);
                        } catch (NumberFormatException e1) {
                            numberFormatWarning();
                        }


                    }
                }).start();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        maxOutValue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    int newValue = Integer.parseInt(maxOutValue.getText());
                    maxThreshModification(newValue);
                } catch (NumberFormatException e1) {
                    numberFormatWarning();
                }

            }
        });

        /**********Minimum-Debounce Label**********/
        if (this.mode == Sensor.FADER) {
            minLabel = new JLabel("Min :");
            minOutValue = new JTextField(String.valueOf(this.minOutVal));
        } else {
            minLabel = new JLabel("Debounce :");
            minOutValue = new JTextField(String.valueOf(this.debounceTime));
        }
        changeColor(minLabel);
        --constraint.gridx;
        constraint.gridy = 1;
        this.add(minLabel, constraint);
        /**minimum output value**/


        changeColor(minOutValue);
        minOutValue.setPreferredSize(new Dimension(35, 18));
        minOutValue.setBorder(OperatingWindows.LOWERED_BORDER);
        constraint.gridx = constraint.gridx + 1;
        this.add(minOutValue, constraint);

        minOutValue.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                new Thread(() -> {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_ENTER) {
                        try {
                            int newValue = Integer.parseInt(minOutValue.getText());
                            minDebModification(newValue);
                        } catch (NumberFormatException e1) {
                            numberFormatWarning();
                        }
                    }
                }).start();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        minOutValue.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    int newValue = Integer.parseInt(minOutValue.getText());
                    minDebModification(newValue);
                } catch (NumberFormatException e1) {
                    numberFormatWarning();
                }
            }
        });


        /**********Output Label**********/
        JLabel outLabel = new JLabel("Out :");
        changeColor(outLabel);
        constraint.gridy = 0;
        constraint.gridheight = 2;
        ++constraint.gridx;
        this.add(outLabel, constraint);


        /**********Output Value**********/
        outputValue = new VuMeter(SwingConstants.HORIZONTAL, 0, 127);
        outputValue.setPreferredSize(new Dimension(80, 13));
        outputValue.setMaximumSize(new Dimension(300, 15));
        outputValue.setBorder(OperatingWindows.ETCHED_BORDER);
        changeColor(outputValue);
        constraint.weightx = 1;
        constraint.gridx = constraint.gridx + 1;
        this.add(outputValue, constraint);

        /**********Mute Button**********/
        muteButton = new JButton("Mute");
        muteButton.setBackground(OperatingWindows.BUTTON_COLOR);
        muteButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        muteButton.setBorder(OperatingWindows.RAISED_BORDER);
        muteButton.setPreferredSize(new Dimension(70, 35));
        constraint.gridx = constraint.gridx + 1;
        this.add(muteButton, constraint);

        muteButton.addActionListener(e -> new Thread(() -> {
            mute();
        }).start());

        /**********SoloButton**********/
        soloButton = new JButton("Solo");
        soloButton.setBackground(OperatingWindows.BUTTON_COLOR);
        soloButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        soloButton.setBorder(OperatingWindows.RAISED_BORDER);
        soloButton.setPreferredSize(new Dimension(70, 35));
        constraint.gridx++;
        this.add(soloButton, constraint);

        soloButton.addActionListener(e -> new Thread(() -> {
            solo();
        }).start());

        /**********Impulse Button**********/
        impulseButton = new JButton("Test");
        impulseButton.setBackground(OperatingWindows.BUTTON_COLOR);
        impulseButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        impulseButton.setBorder(OperatingWindows.RAISED_BORDER);
        impulseButton.setPreferredSize(new Dimension(70, 35));
        constraint.gridx = constraint.gridx + 1;
        this.add(impulseButton, constraint);

        /**********Toggle Button**********/
        toggleButton = new JButton();
        if (this.mode == Sensor.TOGGLE) {
            toggleButton.setText("Toggle");
            toggleButton.setBackground(OperatingWindows.TOGGLE_COLOR);
        } else if (this.mode == Sensor.FADER) {
            toggleButton.setText("Fader");
            toggleButton.setBackground(OperatingWindows.FADER_COLOR);
        } else if (this.mode == Sensor.MOMENTARY) {
            toggleButton.setText("Momentary");
            toggleButton.setBackground(OperatingWindows.MOMENTARY_COLOR);
        } else if (this.mode == Sensor.ALTERNATE) {
            toggleButton.setText("Alternate");
            toggleButton.setBackground(OperatingWindows.ALTERNATE_COLOR);
        }
        toggleButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        toggleButton.setBorder(OperatingWindows.RAISED_BORDER);
        toggleButton.setPreferredSize(new Dimension(70, 35));

        constraint.gridx++;
        constraint.weighty = 2;
        this.add(toggleButton, constraint);

        /*******Delete Button******/
        deleteButton = new DeleteButton(this);
        ++constraint.gridx;
        constraint.gridy = 0;
        constraint.gridheight = 2;
        this.add(deleteButton, constraint);


    }


    /**
     * Adapt the color of a swing Component
     *
     * @param comp the swing component to adapt
     */
    protected static void changeColor(JComponent comp) {
        comp.setBackground(OperatingWindows.BACKGROUND_COLOR);
        comp.setForeground(OperatingWindows.FOREGROUND_COLOR);
    }

    protected void numberFormatWarning() {
        JOptionPane.showMessageDialog(SensorRow.this, "Veuillez entrer un nombre", "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Getter for the arduino Channel of the sensorRow
     *
     * @return the arduinoChannel of the sensorRow
     */
    public int getArduinoChannel() {
        return arduinoChannel;
    }

    /**
     * Getter for the name of the channel
     *
     * @return the name of the channel
     */
    @Override
    public String getName() {
        return name;
    }

    public void setDeleteButton(DeleteButton db) {
        deleteButton = db;
        ++constraint.gridx;
        constraint.gridy = 0;
        constraint.gridheight = 2;
        this.add(deleteButton, constraint);
    }

    /**
     * Set the value of the input vu-meter
     *
     * @param data the value to set
     */
    public void setIncomingSignal(int data) {
        SwingUtilities.invokeLater(() -> {
            incomingSignal.setValue(data);
            SensorRow.this.repaint();
        });
    }

    /**
     * Setter for the color of the impulse Button
     *
     * @param c The color to set
     */
    public void setImpulseColor(Color c) {
        impulseButton.setBackground(c);
    }

    protected abstract void maxThreshModification(int newValue);

    protected abstract void minDebModification(int newValue);

    protected abstract int getType();

    protected void mute() {
        if (muteState) {
            muteState = false;
            SwingUtilities.invokeLater(() -> {
                muteButton.setBackground(OperatingWindows.BUTTON_COLOR);
                muteButton.setBorder(OperatingWindows.RAISED_BORDER);
            });
        } else {
            muteState = true;
            SwingUtilities.invokeLater(() -> {
                muteButton.setBackground(OperatingWindows.MUTE_COLOR);
                muteButton.setBorder(OperatingWindows.LOWERED_BORDER);
            });
        }
    }

    protected void solo() {
        if (soloState) {
            soloState = false;
            SwingUtilities.invokeLater(() -> {
                soloButton.setBackground(OperatingWindows.BUTTON_COLOR);
                soloButton.setBorder(OperatingWindows.RAISED_BORDER);
            });
        } else {
            soloState = true;
            SwingUtilities.invokeLater(() -> {
                soloButton.setBackground(OperatingWindows.SOLO_COLOR);
                soloButton.setBorder(OperatingWindows.LOWERED_BORDER);
            });
        }
    }

    public Object getKey() {
        return key;
    }

}
