package IHM.Row;

import IHM.OperatingWindows;
import Metier.Services;
import Sensor.OSCSensor;
import Sensor.Sensor;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 * Project : ArduinoMidiBridge
 */
public class OscSensorRow extends SensorRow {

    private String address;
    private JLabel outputValue;

    /**
     * Contructor for an OscSensorRow
     *
     * @param name           Name of the channel
     * @param arduChan       Arduino Input concerned
     * @param address        Osc Address used for the communication
     * @param minRange       Value of the minimum osc Message
     * @param maxRange       Value of the maximum osc Message
     * @param preamplifier   Factor of multiplication
     * @param mode           The mode of the sensor
     * @param noiseThreshold a noise threshold for toggle, alternate and momentary mode
     * @param debounceTime   a time of debounce for toggle, alternate and momentary mode
     */
    public OscSensorRow(String name, int arduChan, String address, int minRange, int maxRange, int preamplifier,
                        int mode, int noiseThreshold, int debounceTime) {
        super(name, arduChan, address, minRange, maxRange, preamplifier, mode, noiseThreshold, debounceTime);
        this.address = address;

        /*******ShortCutLabel***********/
        shortcutLabel.setText("");

        /**********Key Label**********/
        keyLabel.setText(address);

        /**********Preamplifier Slider**********/
        preamplifierSlider.setMinimum(-300);
        preamplifierSlider.setMaximum(300);
        preamplifierSlider.addChangeListener(e -> new Thread(() -> {
            int newValue = preamplifierSlider.getValue();
            Services.changeOscPreamplifier(address, newValue);
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
                            Services.changeOscPreamplifier(address, newValue);
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

        /**********Output Value**********/
        outputValue = new JLabel("000");
        changeColor(outputValue);
        constraint.weightx = 1;
        constraint.gridx = 11;
        this.add(outputValue, constraint);

        /**********Impulse Button**********/
        impulseButton.addActionListener(e -> new Thread(() -> {
            SwingUtilities.invokeLater(() -> impulseButton.setBackground(OperatingWindows.IMPULSE_COLOR));
            Services.sendOscTestMessage(address);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> impulseButton.setBackground(OperatingWindows.BUTTON_COLOR));

        }).start());

        /**********Toggle Button**********/
        toggleButton.setEnabled(false);
        toggleButton.setBorder(OperatingWindows.ETCHED_BORDER);

    }

    /**
     * Constructor for an alternate OscSensorRow
     *
     * @param name       Name of the channel
     * @param arduChan   Arduino Input concerned
     * @param address    Osc Address used for the communication
     * @param addressBis Osc Second address to alternate with
     * @param mode       The mode of the sensor
     */
    public OscSensorRow(String name, int arduChan, String address, String addressBis, int mode) {
        this(name, arduChan, address, mode);
        shortcutLabel.setText(addressBis);
    }

    /**
     * Constructor for a sensorRow
     *
     * @param name     Name of the channel
     * @param arduChan Arduino Input concerned
     * @param address  Osc Address used for the communication
     * @param mode     The mode of the sensor
     */
    public OscSensorRow(String name, int arduChan, String address, int mode) {
        this(name, arduChan, address, 0, 100, 100, mode, 0, 0);
    }

    /**
     * Constructor for a sensorRow from an existing Sensor
     *
     * @param s the existing Sensor
     */
    public OscSensorRow(OSCSensor s) {
        this(s.getName(), s.getArduinoIn(), s.getOscAddress(), s.getMinRange(), s.getMaxRange(), s.getPreamplifier(),
                s.getMode(), s.getNoiseThreshold(), s.getDebounceTime());
        shortcutLabel.setText(s.getOscAddressBis());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello World");
        JPanel panel = new JPanel();
        OscSensorRow sensorRow = new OscSensorRow("On peut essayer de mettre un titre super long ", 12, "/test/play", Sensor.ALTERNATE);
        panel.add(sensorRow);
        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Services.addOscSensor("On peut essayer de mettre un titre super long ", 12, "/test/play", Sensor.ALTERNATE);
    }

    /**
     * Method called when there is a modification in the maximum field
     *
     * @param newValue the modified value
     */
    @Override
    protected void maxThreshModification(int newValue) {
        if (this.mode == Sensor.FADER) {
            if (minOutVal <= newValue) {
                Services.changeOscMaxRange(address, newValue);
                maxOutVal = newValue;
            } else {
                Services.changeOscMaxRange(address, minOutVal);
                maxOutVal = minOutVal;
                SwingUtilities.invokeLater(() -> maxOutValue.setText(String.valueOf(maxOutVal)));
            }
        } else {
            if (newValue > 0) {
                Services.setOscLineThreshold(address, newValue);
                noiseThreshold = newValue;
            } else {
                Services.setOscLineThreshold(address, 0);
                noiseThreshold = 0;
                SwingUtilities.invokeLater(() -> maxOutValue.setText("0"));
            }
        }
    }

    /**
     * Method called when there is a modification in the minimum field
     *
     * @param newValue the modified value
     */
    @Override
    protected void minDebModification(int newValue) {
        if (this.mode == Sensor.FADER) {
            if (maxOutVal >= newValue) {
                Services.changeOscMinRange(address, newValue);
                minOutVal = newValue;
            } else {
                Services.changeOscMinRange(address, maxOutVal);
                minOutVal = maxOutVal;
                SwingUtilities.invokeLater(() -> minOutValue.setText(String.valueOf(minOutVal)));
            }
        } else {
            if (newValue > 0) {
                Services.setOscLineDebounce(address, newValue);
                debounceTime = newValue;
            } else {
                Services.setOscLineDebounce(address, 0);
                debounceTime = 0;
                SwingUtilities.invokeLater(() -> minOutValue.setText("0"));
            }
        }
    }

    /**
     * Set the value of the output Label
     *
     * @param outValue the value to set
     */
    @Override
    public void setOutputValue(int outValue) {
        outputValue.setText(String.valueOf(outValue));
        repaint();
    }

    /**
     * Getter for the type of the sensorRow
     *
     * @return the type of SensorRow
     */
    @Override
    protected int getType() {
        return OSC;
    }

    /**
     * Action done when the mute button is clicked
     */
    @Override
    protected void mute() {
        super.mute();
        if (muteState) {
            Services.oscMute(address);
        } else {
            Services.oscUnMute(address);
        }
    }

    /**
     * Action done when the solo button is clicked
     */
    @Override
    protected void solo() {
        super.solo();
        if (soloState) {
            Services.oscSolo(address);
        } else {
            Services.oscUnSolo(address);
        }
    }

    /**
     * Getter for the main address of the SensorRow
     *
     * @return the main Osc address of the row
     */
    public String getAddress() {
        return address;
    }
}