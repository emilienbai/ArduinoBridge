package IHM;

import Metier.Services;
import Sensor.OSCSensor;
import Sensor.Sensor;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 */
public class OscSensorRow extends SensorRow {

    private String address;

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

    public OscSensorRow(String name, int arduChan, String address, int mode) {
        this(name, arduChan, address, 0, 100, 100, mode, 0, 0);
    }

    public OscSensorRow(OSCSensor s) {
        this(s.getName(), s.getArduinoIn(), s.getOscAddress(), s.getMinRange(), s.getMaxRange(), ((int) s.getPreamplifier()),
                s.getMode(), s.getNoiseThreshold(), s.getDebounceTime());
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello World");
        JPanel panel = new JPanel();
        OscSensorRow sensorRow = new OscSensorRow("On peut essayer de mettre un titre super long ", 12, "/test/play", Sensor.ALTERNATE);
        DeleteButton db = new DeleteButton(sensorRow);
        sensorRow.setDeleteButton(db);
        panel.add(sensorRow);
        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Services.addOscSensor("On peut essayer de mettre un titre super long ", 12, "/test/play", Sensor.ALTERNATE);
    }

    @Override
    protected void maxThreshModification(int newValue) {
        //todo fillMe
    }

    @Override
    protected void minDebModification(int newValue) {
        //todo fillMe
    }

    @Override
    protected int getType() {
        return OSC;
    }

    protected void mute() {

    }

    protected void solo() {

    }


}



