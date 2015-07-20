package IHM;

import Metier.Services;
import Sensor.MidiSensor;
import Sensor.Sensor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
class MidiSensorRow extends SensorRow {

    private int midiPort;
    private char shortcut;


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
    public MidiSensorRow(String name, int arduChan, int midiPort, int minRange, int maxRange, int preamplifier,
                         char shortcut, int mode, int noiseThreshold, int debounceTime) {
        super(name, arduChan, midiPort, minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime);
        this.shortcut = shortcut;
        this.midiPort = (Integer) this.key;

        /*******ShortCutLabel***********/
        shortcutLabel.setText("Clavier : (" + shortcut + ")");

        /**********Key Label**********/
        keyLabel.setText("Midi : " + midiPort);

        /**********Preamplifier Slider**********/
        preamplifierSlider.addChangeListener(e -> new Thread(() -> {
            int newValue = preamplifierSlider.getValue();
            Services.changeMidiPreamplifier(midiPort, newValue);
            SwingUtilities.invokeLater(() -> preamplifierValue.setText(String.valueOf(newValue)));
        }).start());

        /**********Preamplifier manual value**********/
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
                            Services.changeMidiPreamplifier(midiPort, newValue);
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

        /**********Impulse Button**********/
        impulseButton.addActionListener(e -> new Thread(() -> {
            SwingUtilities.invokeLater(() -> impulseButton.setBackground(OperatingWindows.IMPULSE_COLOR));
            Services.sendMidiImpulsion(midiPort);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> impulseButton.setBackground(OperatingWindows.BUTTON_COLOR));

        }).start());

        /**********Toggle Button**********/
        toggleButton.addActionListener(e -> new Thread(() -> {
            if (MidiSensorRow.this.mode == Sensor.MOMENTARY) {
                changeMode(Sensor.FADER);
            } else if (MidiSensorRow.this.mode == Sensor.FADER) {
                changeMode(Sensor.TOGGLE);
            } else if (MidiSensorRow.this.mode == Sensor.TOGGLE) {
                changeMode(Sensor.MOMENTARY);
            }
        }).start());

    }

    /**
     * Simplified constructor with defalut parameters
     *
     * @param name     Name of the channel
     * @param arduChan arduino channel for the row
     * @param midiPort midiPort for the row
     * @param shortcut keyboard shortcut for this row
     */
    public MidiSensorRow(String name, int arduChan, int midiPort, char shortcut) {
        this(name, arduChan, midiPort, 0, 127, 100, shortcut, Sensor.FADER, 0, 0);
    }

    public MidiSensorRow(MidiSensor s) {
        this(s.getName(), s.getArduinoIn(), s.getMidiPort(), s.getMinRange(), s.getMaxRange(), ((int) s.getPreamplifier()), s.getShortcut(), s.getMode(), s.getNoiseThreshold(), s.getDebounceTime());
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello World");
        JPanel panel = new JPanel();
        MidiSensorRow sensorRow = new MidiSensorRow("On peut essayer de mettre un titre super long ", 12, 42, 'a');
        DeleteButton db = new DeleteButton(sensorRow);
        sensorRow.setDeleteButton(db);
        panel.add(sensorRow);
        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    protected void mute() {
        super.mute();
        if (muteState) {
            Services.midiMute(midiPort);
        } else {
            Services.midiUnMute(midiPort);
        }
    }

    protected void solo() {
        super.solo();
        if (soloState) {
            Services.midiSolo(midiPort);
        } else {
            Services.midiUnSolo(midiPort);
        }
    }

    protected void maxThreshModification(int newValue) {
        if (MidiSensorRow.this.mode == Sensor.FADER) {
            if (newValue <= 127 && newValue >= minOutVal) {
                Services.changeMidiMaxRange(midiPort, newValue);
                maxOutVal = newValue;
            } else if (newValue > 127) {
                Services.changeMidiMinRange(midiPort, 127);
                minOutVal = 127;
                SwingUtilities.invokeLater(() -> maxOutValue.setText("127"));
            } else {
                Services.changeMidiMinRange(midiPort, minOutVal);
                maxOutVal = minOutVal;
                SwingUtilities.invokeLater(() -> maxOutValue.setText(String.valueOf(minOutVal)));
            }
        } else {
            if (newValue > 0) {
                Services.setMidiLineThreshold(midiPort, newValue);
                noiseThreshold = newValue;
            } else {
                Services.setMidiLineThreshold(midiPort, 0);
                noiseThreshold = 0;
                SwingUtilities.invokeLater(() -> maxOutValue.setText("0"));
            }
        }

    }

    protected void minDebModification(int newValue) {
        if (MidiSensorRow.this.mode == Sensor.FADER) {
            if (newValue >= 0 && newValue <= maxOutVal) {
                Services.changeMidiMinRange(midiPort, newValue);
                minOutVal = newValue;
            } else if (newValue < 0) {
                Services.changeMidiMinRange(midiPort, 0);
                minOutVal = 0;
                SwingUtilities.invokeLater(() -> minOutValue.setText("000"));
            } else {
                Services.changeMidiMinRange(midiPort, maxOutVal);
                minOutVal = maxOutVal;
                SwingUtilities.invokeLater(() -> minOutValue.setText(String.valueOf(maxOutVal)));
            }
        } else {
            if (newValue > 0) {
                Services.setMidiLineDebounce(midiPort, newValue);
                debounceTime = newValue;
            } else {
                Services.setMidiLineDebounce(midiPort, 0);
                debounceTime = 0;
                SwingUtilities.invokeLater(() -> minOutValue.setText("0"));
            }
        }

    }

    @Override
    protected int getType() {
        return MIDI;
    }


    private void changeMode(int mode) {
        this.mode = mode;
        if (mode == Sensor.FADER) {
            Services.setMidiMode(midiPort, Sensor.FADER);
            SwingUtilities.invokeLater(() -> {
                toggleButton.setText("Fader");
                toggleButton.setBackground(OperatingWindows.FADER_COLOR);
                maxLabel.setText("Max :");
                minLabel.setText("Min :");
                maxOutValue.setText(String.valueOf(Services.getMidiMaxRange(midiPort)));
                minOutValue.setText(String.valueOf(Services.getMidiMinRange(midiPort)));
            });
        } else if (mode == Sensor.MOMENTARY) {
            Services.setMidiMode(midiPort, Sensor.MOMENTARY);
            SwingUtilities.invokeLater(() -> {
                toggleButton.setText("Momentary");
                toggleButton.setBackground(OperatingWindows.MOMENTARY_COLOR);
                maxLabel.setText("Seuil :");
                minLabel.setText("Debounce :");
                maxOutValue.setText(String.valueOf(Services.getMidiLineThreshold(midiPort)));
                minOutValue.setText(String.valueOf(Services.getMidiLineDebounce(midiPort)));
            });
        } else if (mode == Sensor.TOGGLE) {
            Services.setMidiMode(midiPort, Sensor.TOGGLE);
            SwingUtilities.invokeLater(() -> {
                toggleButton.setText("Toggle");
                toggleButton.setBackground(OperatingWindows.TOGGLE_COLOR);
                maxLabel.setText("Seuil :");
                minLabel.setText("Debounce :");
                maxOutValue.setText(String.valueOf(Services.getMidiLineThreshold(midiPort)));
                minOutValue.setText(String.valueOf(Services.getMidiLineDebounce(midiPort)));
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
    }


    /**
     * Set the value of the output vu-meter
     *
     * @param outValue the value to set
     */
    public void setOutputValue(int outValue) {
        SwingUtilities.invokeLater(() -> {
            outputValue.setValue(outValue);
            MidiSensorRow.this.repaint();
        });
    }

    /**
     * getter for the midi port of the MidiSensorRow
     *
     * @return the midiPort of the sensorRow
     */
    public int getMidiPort() {
        return midiPort;
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

}