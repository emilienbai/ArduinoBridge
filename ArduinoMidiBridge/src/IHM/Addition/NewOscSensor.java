package IHM.Addition;

import IHM.OperatingWindows;
import Sensor.Sensor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 * Project : ArduinoMidiBridge
 */
public class NewOscSensor extends JFrame {
    private static boolean open;

    public NewOscSensor(JFrame toPack) {
        super("Ajout d'un nouveau capteur OSC");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        open = true;

        JPanel mainPanel = new JPanel(new GridBagLayout());
        changeColor(mainPanel);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(3, 5, 3, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        /**NameLabel**/
        JLabel nameLabel = new JLabel("Nom :");
        changeColor(nameLabel);

        mainPanel.add(nameLabel, constraints);

        /**NameField**/
        JTextField nameField = new JTextField();
        changeColor(nameField);
        nameField.setPreferredSize(new Dimension(250, 20));

        constraints.gridx++;
        constraints.gridwidth = 2;
        mainPanel.add(nameField, constraints);

        /**ArduinoInLabel**/
        JLabel arduinoInLabel = new JLabel("Entrée arduino : ");
        changeColor(arduinoInLabel);

        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy++;
        mainPanel.add(arduinoInLabel, constraints);

        /**ArduinoInCombo**/
        JComboBox<Integer> arduinoInCombo = new JComboBox<>();
        for (int i = 0; i < 16; i++) {
            arduinoInCombo.addItem(i);
        }
        arduinoInCombo.setSelectedIndex(0);
        changeColor(arduinoInCombo);

        constraints.gridwidth = 2;
        constraints.gridx = 2;
        mainPanel.add(arduinoInCombo, constraints);

        /**AddressLabel**/
        JLabel addressLabel = new JLabel("Adresse Osc : ");
        changeColor(addressLabel);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;

        mainPanel.add(addressLabel, constraints);

        /**AddressField**/
        JTextField addressField = new JTextField();
        changeColor(addressField);

        constraints.gridx = 1;
        constraints.gridwidth = 2;
        mainPanel.add(addressField, constraints);

        /**Alternate address**/
        JLabel addressBisLabel = new JLabel("Adresse Osc bis");
        changeColor(addressBisLabel);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        mainPanel.add(addressBisLabel, constraints);
        addressBisLabel.setVisible(false);

        /**Alternate address Field**/
        JTextField addressBisField = new JTextField();
        changeColor(addressBisField);

        constraints.gridx = 1;
        constraints.gridwidth = 2;
        mainPanel.add(addressBisField, constraints);
        addressBisField.setVisible(false);

        /**ModeLabel**/
        JLabel modeLabel = new JLabel("Mode : ");
        changeColor(modeLabel);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        mainPanel.add(modeLabel, constraints);

        /**ModeComboBox**/
        JComboBox<String> modeCombo = new JComboBox<>();
        changeColor(modeCombo);
        modeCombo.insertItemAt("Fader", Sensor.FADER);
        modeCombo.insertItemAt("Toggle", Sensor.TOGGLE);
        modeCombo.insertItemAt("Momentary", Sensor.MOMENTARY);
        modeCombo.insertItemAt("Alternate", Sensor.ALTERNATE);
        modeCombo.setSelectedIndex(0);

        constraints.gridx = 2;
        mainPanel.add(modeCombo, constraints);

        modeCombo.addActionListener(e -> {
            int mode = modeCombo.getSelectedIndex();
            if (mode == Sensor.ALTERNATE) {
                SwingUtilities.invokeLater(() -> {
                    addressBisField.setVisible(true);
                    addressBisLabel.setVisible(true);
                    repaint();
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    addressBisField.setVisible(false);
                    addressBisLabel.setVisible(false);
                    repaint();
                });
            }
        });

        /**CancelButton**/
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setBackground(OperatingWindows.BUTTON_COLOR);
        cancelButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        cancelButton.setBorder(OperatingWindows.RAISED_BORDER);
        cancelButton.setPreferredSize(new Dimension(70, 35));

        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy++;
        mainPanel.add(cancelButton, constraints);

        cancelButton.addActionListener(e -> {
            open = false;
            dispose();
        });

        /**OKButton**/
        JButton okButton = new JButton("OK");
        okButton.setBackground(OperatingWindows.BUTTON_COLOR);
        okButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        okButton.setBorder(OperatingWindows.RAISED_BORDER);
        okButton.setPreferredSize(new Dimension(70, 35));

        constraints.gridx = 2;
        mainPanel.add(okButton, constraints);

        okButton.addActionListener(e -> new Thread(() -> {
            String name = nameField.getText();
            int arduinoIn = (int) arduinoInCombo.getSelectedItem();
            String address = addressField.getText();
            String addressBis = addressBisField.getText();
            int mode = modeCombo.getSelectedIndex();
            if (!name.equals("") && !address.equals("")
                    && !(addressBis.equals("") && mode == Sensor.ALTERNATE)) {
                OperatingWindows.addOscSensor(name, arduinoIn, address, addressBis, mode);
                toPack.pack();
                open = false;
                dispose();
            } else {
                String message;
                if (name.equals("")) {
                    message = "Veuillez renseigner un nom pour cette ligne";
                } else if (address == null || address.equals("")) {
                    message = "veuillez renseigner une adresse osc pour cette ligne";
                } else {
                    message = "veuillez renseigner une adresse osc secondaire pour cette ligne";
                }
                JOptionPane.showMessageDialog(null, message, " Erreur ", JOptionPane.ERROR_MESSAGE);
            }

        }).start());

        this.add(mainPanel);
        this.pack();
        this.setVisible(true);
    }

    /**
     * Is an instance of this Frame already open
     *
     * @return true if already opened
     */
    public static boolean isOpen() {
        return open;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        new NewOscSensor(f);
    }

    /**
     * Change the color of the component
     *
     * @param comp the Jcomponent to modify
     */
    private void changeColor(JComponent comp) {
        comp.setBackground(OperatingWindows.BACKGROUND_COLOR);
        comp.setForeground(OperatingWindows.FOREGROUND_COLOR);
    }
}
