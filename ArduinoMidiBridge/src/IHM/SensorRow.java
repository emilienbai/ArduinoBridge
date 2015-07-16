package IHM;

import Metier.SensorManagement;
import Metier.Services;
import Sensor.MidiSensor;
import Sensor.Sensor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
class SensorRow extends JPanel {
    private static Color BACKGROUND_COLOR = OperatingWindows.BACKGROUND_COLOR;
    private static Color BUTTON_COLOR = OperatingWindows.BUTTON_COLOR;
    private static Color FOREGROUND_COLOR = OperatingWindows.FOREGROUND_COLOR;
    private static Color MUTE_COLOR = OperatingWindows.MUTE_COLOR;
    private static Color SOLO_COLOR = OperatingWindows.SOLO_COLOR;
    private static Color IMPULSE_COLOR = OperatingWindows.IMPULSE_COLOR;
    private static Color NAME_COLOR = OperatingWindows.NAME_COLOR;
    private static Border RAISED_BORDER = OperatingWindows.RAISED_BORDER;
    private static Border LOWERED_BORDER = OperatingWindows.LOWERED_BORDER;
    private static Border ETCHED_BORDER = OperatingWindows.ETCHED_BORDER;


    private static GridBagConstraints constraint;
    private VuMeter incomingSignal;
    private JSlider preamplifierSlider;
    private JTextField preamplifierValue;
    private JTextField minOutValue;
    private JTextField maxOutValue;
    private VuMeter outputValue;
    private JButton muteButton;
    private JButton soloButton;
    private JButton impulseButton;
    private JButton toggleButton;
    private DeleteButton deleteButton;
    private JLabel maxLabel;
    private JLabel minLabel;

    private int arduinoChannel;
    private int midiPort;
    private int minOutVal;
    private int maxOutVal;
    private boolean muteState;
    private boolean soloState;
    private String name;
    private char shortcut;
    private int mode;
    private int noiseThreshold;
    private int debounceTime;


    /**
     * Create a JPanel containing all the element of a sensorRow
     *
     * @param name         Name of the channel
     * @param arduChan     Arduino Input concerned
     * @param midiPort     Midi port used for the communication
     * @param minRange     Value of the minimum midi Message
     * @param maxRange     Value of the maximum midi Message
     * @param preamplifier Factor of multiplication
     * @param shortcut     Keyboard Shortcut used to send an impulsion
     */
    public SensorRow(String name, int arduChan, int midiPort, int minRange, int maxRange, int preamplifier, char shortcut, int mode, int noiseThreshold, int debounceTime) {
        super(new GridBagLayout());
        constraint = new GridBagConstraints();
        this.name = name;
        this.minOutVal = minRange;
        this.maxOutVal = maxRange;
        muteState = false;
        soloState = false;
        this.arduinoChannel = arduChan;
        this.midiPort = midiPort;
        this.shortcut = shortcut;
        this.mode = mode;
        this.noiseThreshold = noiseThreshold;
        this.debounceTime = debounceTime;
        changeColor(this);
        this.setBorder(ETCHED_BORDER);


        /********************Name************/

        JLabel nameLabel = new JLabel(name);
        nameLabel.setMaximumSize(new Dimension(145, 50));
        nameLabel.setMinimumSize(new Dimension(80, 10));
        nameLabel.setPreferredSize(new Dimension(110, 20));
        nameLabel.setForeground(NAME_COLOR);
        addVerticalSeparation(5);
        constraint.gridx = 1;
        constraint.gridy = 0;
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.weightx = 1;
        constraint.weighty = 0;
        constraint.gridheight = 1;
        constraint.ipadx = 5;
        this.add(nameLabel, constraint);

        JLabel shortcutLabel = new JLabel("Clavier : (" + shortcut + ")");
        shortcutLabel.setForeground(FOREGROUND_COLOR);
        shortcutLabel.setMaximumSize(new Dimension(145, 50));
        shortcutLabel.setMinimumSize(new Dimension(80, 10));
        shortcutLabel.setPreferredSize(new Dimension(110, 20));

        constraint.gridy = 1;
        this.add(shortcutLabel, constraint);

        addVerticalSeparation(5);
        /*********Arduino input Chanel*******/
        JLabel arduinoChannelLabel = new JLabel("Arduino : " + String.valueOf(arduChan));
        changeColor(arduinoChannelLabel);
        arduinoChannelLabel.setPreferredSize(new Dimension(85, 20));
        constraint.gridy = 0;
        constraint.gridheight = 1;
        constraint.weightx = 0;
        constraint.gridx = constraint.gridx + 1;
        this.add(arduinoChannelLabel, constraint);

        /**********Midi Port**********/
        JLabel midiPortLabel = new JLabel(String.valueOf("Midi : " + midiPort));
        changeColor(midiPortLabel);
        midiPortLabel.setPreferredSize(new Dimension(70, 20));
        constraint.gridy = 1;
        this.add(midiPortLabel, constraint);


        addVerticalSeparation(10);
        /**********Label for Input Signal**********/
        JLabel inputLabel = new JLabel("In :");
        changeColor(inputLabel);
        ++constraint.gridx;
        constraint.gridheight = 2;
        this.add(inputLabel, constraint);

        /**********Incoming signal "vu-meter"*********/
        incomingSignal = new VuMeter(SwingConstants.HORIZONTAL, 0, 1024);
        changeColor(incomingSignal);
        incomingSignal.setPreferredSize(new Dimension(80, 13));
        incomingSignal.setMaximumSize(new Dimension(300, 15));
        incomingSignal.setBorder(ETCHED_BORDER);
        constraint.weightx = 1;
        constraint.gridx = constraint.gridx + 1;
        this.add(incomingSignal, constraint);

        addVerticalSeparation(10);
        /**********Preamplifier Label**********/
        JLabel preampLabel = new JLabel("Preamp :");
        changeColor(preampLabel);
        constraint.weightx = 0;
        constraint.gridheight = 2;
        ++constraint.gridx;
        this.add(preampLabel, constraint);
        /**********Preamplifier Slider**********/
        preamplifierSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 500, 100);
        preamplifierSlider.setValue(preamplifier);
        changeColor(preamplifierSlider);
        constraint.gridx = constraint.gridx + 1;
        this.add(preamplifierSlider, constraint);

        preamplifierSlider.addChangeListener(e -> new Thread(() -> {
            int newValue = preamplifierSlider.getValue();
            SensorManagement.changePreamplifier(midiPort, newValue);
            SwingUtilities.invokeLater(() -> preamplifierValue.setText(String.valueOf(newValue)));
        }).start());


        /**********Preamplifier manual value**********/
        preamplifierValue = new JTextField(String.valueOf(preamplifier));
        preamplifierValue.setBorder(LOWERED_BORDER);
        changeColor(preamplifierValue);
        preamplifierValue.setPreferredSize(new Dimension(35, 18));
        constraint.gridx = constraint.gridx + 1;
        this.add(preamplifierValue, constraint);

        preamplifierValue.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                new Thread(() -> {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_ENTER) {
                        int newValue = Integer.parseInt(preamplifierValue.getText());
                        if (newValue > 0) {
                            SensorManagement.changePreamplifier(midiPort, newValue);
                        }
                        SwingUtilities.invokeLater(() -> {
                            if (newValue <= preamplifierSlider.getMaximum()) {
                                preamplifierSlider.setValue(newValue);
                            } else if (newValue <= 0) {
                                preamplifierValue.setText(String.valueOf(preamplifierSlider.getValue()));
                            } else {
                                preamplifierSlider.setValue(preamplifierSlider.getMaximum());
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        /***********Percent Label*********/
        JLabel percentLabel = new JLabel("%");
        changeColor(percentLabel);
        ++constraint.gridx;
        this.add(percentLabel, constraint);

        addVerticalSeparation(5);

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
        maxOutValue.setBorder(LOWERED_BORDER);
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
        minOutValue.setBorder(LOWERED_BORDER);
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


        addVerticalSeparation(5);
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
        outputValue.setBorder(ETCHED_BORDER);
        changeColor(outputValue);
        constraint.weightx = 1;
        constraint.gridx = constraint.gridx + 1;
        this.add(outputValue, constraint);

        addVerticalSeparation(5);
        /**********Mute Button**********/
        muteButton = new JButton("Mute");
        muteButton.setBackground(BUTTON_COLOR);
        muteButton.setForeground(FOREGROUND_COLOR);
        muteButton.setBorder(RAISED_BORDER);
        muteButton.setPreferredSize(new Dimension(70, 35));
        constraint.gridx = constraint.gridx + 1;
        this.add(muteButton, constraint);

        muteButton.addActionListener(e -> new Thread(() -> {
            if (muteState) {
                SensorManagement.unmute(midiPort);
                muteState = false;
                SwingUtilities.invokeLater(() -> {
                    muteButton.setBackground(BUTTON_COLOR);
                    muteButton.setBorder(RAISED_BORDER);
                });
            } else {
                SensorManagement.mute(midiPort);
                muteState = true;
                SwingUtilities.invokeLater(() -> {
                    muteButton.setBackground(MUTE_COLOR);
                    muteButton.setBorder(LOWERED_BORDER);
                });
            }

        }).start());

        addVerticalSeparation(5);
        /**********SoloButton**********/
        soloButton = new JButton("Solo");
        soloButton.setBackground(BUTTON_COLOR);
        soloButton.setForeground(FOREGROUND_COLOR);
        soloButton.setBorder(RAISED_BORDER);
        soloButton.setPreferredSize(new Dimension(70, 35));
        constraint.gridx = constraint.gridx + 1;
        this.add(soloButton, constraint);

        soloButton.addActionListener(e -> new Thread(() -> {
            if (soloState) {
                SensorManagement.unSolo(midiPort);
                soloState = false;
                SwingUtilities.invokeLater(() -> {
                    soloButton.setBackground(BUTTON_COLOR);
                    soloButton.setBorder(RAISED_BORDER);
                });
            } else {
                SensorManagement.solo(midiPort);
                soloState = true;
                SwingUtilities.invokeLater(() -> {
                    soloButton.setBackground(SOLO_COLOR);
                    soloButton.setBorder(LOWERED_BORDER);
                });
            }

        }).start());

        addVerticalSeparation(5);
        /**********Impulse Button**********/
        impulseButton = new JButton("Impulsion");
        impulseButton.setBackground(BUTTON_COLOR);
        impulseButton.setForeground(FOREGROUND_COLOR);
        impulseButton.setBorder(RAISED_BORDER);
        impulseButton.setPreferredSize(new Dimension(70, 35));
        constraint.gridx = constraint.gridx + 1;
        this.add(impulseButton, constraint);

        impulseButton.addActionListener(e -> new Thread(() -> {
            SwingUtilities.invokeLater(() -> impulseButton.setBackground(IMPULSE_COLOR));
            Services.sendMidiImpulsion(midiPort);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> impulseButton.setBackground(BUTTON_COLOR));

        }).start());

        addVerticalSeparation(5);

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
        }
        toggleButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        toggleButton.setBorder(OperatingWindows.RAISED_BORDER);
        toggleButton.setPreferredSize(new Dimension(70, 35));

        constraint.gridx = constraint.gridx + 1;
        this.add(toggleButton, constraint);

        toggleButton.addActionListener(e -> new Thread(() -> {
            if (SensorRow.this.mode == Sensor.MOMENTARY) {
                changeMode(Sensor.FADER);
            } else if (SensorRow.this.mode == Sensor.FADER) {
                changeMode(Sensor.TOGGLE);
            } else {
                changeMode(Sensor.MOMENTARY);
            }


        }).start());
        addVerticalSeparation(5);

    }

    /**
     * Simplified constructor with defalut parameters
     *
     * @param name     Name of the channel
     * @param arduChan arduino channel for the row
     * @param midiPort midiPort for the row
     * @param shortcut keyboard shortcut for this row
     */
    public SensorRow(String name, int arduChan, int midiPort, char shortcut) {
        this(name, arduChan, midiPort, 0, 127, 100, shortcut, Sensor.FADER, 0, 0);
    }

    public SensorRow(MidiSensor s) {
        this(s.getName(), s.getArduinoIn(), s.getMidiPort(), s.getMinRange(), s.getMaxRange(), s.getPreamplifier(), s.getShortcut(), s.getMode(), s.getNoiseThreshold(), s.getDebounceTime());
    }


    /**
     * Adapt the color of a swing Component
     *
     * @param comp the swing component to adapt
     */
    private static void changeColor(JComponent comp) {
        comp.setBackground(BACKGROUND_COLOR);
        comp.setForeground(FOREGROUND_COLOR);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello World");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("1");
        SensorRow sensorRow = new SensorRow("On peut essayer de mettre un titre super long ", 12, 42, 'a');
        DeleteButton db = new DeleteButton(sensorRow, panel, frame, label);
        sensorRow.setDeleteButton(db);
        panel.add(sensorRow);
        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void maxThreshModification(int newValue) {
        if (SensorRow.this.mode == Sensor.FADER) {
            if (newValue <= 127 && newValue >= minOutVal) {
                Services.changeMaxRange(midiPort, newValue);
                maxOutVal = newValue;
            } else if (newValue > 127) {
                Services.changeMinRange(midiPort, 127);
                minOutVal = 127;
                SwingUtilities.invokeLater(() -> maxOutValue.setText("127"));
            } else {
                SensorManagement.changeMinRange(midiPort, minOutVal);
                maxOutVal = minOutVal;
                SwingUtilities.invokeLater(() -> maxOutValue.setText(String.valueOf(minOutVal)));
            }
        } else {
            if (newValue > 0) {
                Services.setLineThreshold(midiPort, newValue);
                noiseThreshold = newValue;
            } else {
                Services.setLineThreshold(midiPort, 0);
                noiseThreshold = 0;
                SwingUtilities.invokeLater(() -> maxOutValue.setText("0"));
            }
        }

    }

    public void minDebModification(int newValue) {
        if (SensorRow.this.mode == Sensor.FADER) {
            if (newValue >= 0 && newValue <= maxOutVal) {
                SensorManagement.changeMinRange(midiPort, newValue);
                minOutVal = newValue;
            } else if (newValue < 0) {
                SensorManagement.changeMinRange(midiPort, 0);
                minOutVal = 0;
                SwingUtilities.invokeLater(() -> minOutValue.setText("000"));
            } else {
                SensorManagement.changeMinRange(midiPort, maxOutVal);
                minOutVal = maxOutVal;
                SwingUtilities.invokeLater(() -> minOutValue.setText(String.valueOf(maxOutVal)));
            }
        } else {
            if (newValue > 0) {
                Services.setLineDebounce(midiPort, newValue);
                debounceTime = newValue;
            } else {
                Services.setLineDebounce(midiPort, 0);
                debounceTime = 0;
                SwingUtilities.invokeLater(() -> minOutValue.setText("0"));
            }
        }

    }

    private void numberFormatWarning() {
        JOptionPane.showMessageDialog(SensorRow.this, "Veuillez entrer un nombre", "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    private void changeMode(int mode) {
        this.mode = mode;
        if (mode == Sensor.FADER) {
            Services.setMode(midiPort, Sensor.FADER);
            SwingUtilities.invokeLater(() -> {
                toggleButton.setText("Fader");
                toggleButton.setBackground(OperatingWindows.FADER_COLOR);
                maxLabel.setText("Max :");
                minLabel.setText("Min :");
                maxOutValue.setText(String.valueOf(Services.getMaxRange(midiPort)));
                minOutValue.setText(String.valueOf(Services.getMinRange(midiPort)));
            });
        } else if (mode == Sensor.MOMENTARY) {
            Services.setMode(midiPort, Sensor.MOMENTARY);
            SwingUtilities.invokeLater(() -> {
                toggleButton.setText("Momentary");
                toggleButton.setBackground(OperatingWindows.MOMENTARY_COLOR);
                maxLabel.setText("Seuil :");
                minLabel.setText("Debounce :");
                maxOutValue.setText(String.valueOf(Services.getLineThreshold(midiPort)));
                minOutValue.setText(String.valueOf(Services.getLineDebounce(midiPort)));
            });
        } else if (mode == Sensor.TOGGLE) {
            Services.setMode(midiPort, Sensor.TOGGLE);
            SwingUtilities.invokeLater(() -> {
                toggleButton.setText("Toggle");
                toggleButton.setBackground(OperatingWindows.TOGGLE_COLOR);
                maxLabel.setText("Seuil :");
                minLabel.setText("Debounce :");
                maxOutValue.setText(String.valueOf(Services.getLineThreshold(midiPort)));
                minOutValue.setText(String.valueOf(Services.getLineDebounce(midiPort)));
            });
        }
        repaint();
    }

    public void setDeleteButton(DeleteButton db) {
        deleteButton = db;
        ++constraint.gridx;
        constraint.gridy = 0;
        constraint.gridheight = 2;
        this.add(deleteButton, constraint);
        addVerticalSeparation(10);
    }

    /**
     * getter for the midi port of the SensorRow
     *
     * @return the midiPort of the sensorRow
     */
    public int getMidiPort() {
        return midiPort;
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
     * Set the value of the output vu-meter
     *
     * @param outValue the value to set
     */
    public void setOutputValue(int outValue) {
        SwingUtilities.invokeLater(() -> {
            outputValue.setValue(outValue);
            SensorRow.this.repaint();
        });
    }

    /**
     * Getter for the shortcut of the sensorRow
     *
     * @return the shortcut to use to send an impulsion
     */
    public char getShortcut() {
        return shortcut;
    }

    /**
     * Setter for the color of the impulse Button
     *
     * @param c The color to set
     */
    public void setImpulseColor(Color c) {
        impulseButton.setBackground(c);
    }

    /**
     * Add a vertical separation between the element of the SensorRow
     *
     * @param width width of the separation
     */
    private void addVerticalSeparation(int width) {
        int temp = (int) constraint.weightx;
        constraint.weightx = 0;
        constraint.gridx = constraint.gridx + 1;
        constraint.gridy = 0;
        constraint.gridheight = 2;
        this.add(Box.createHorizontalStrut(width), constraint);
        constraint.weightx = temp;
    }
}
