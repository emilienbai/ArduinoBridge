package IHM.Addition;

import IHM.OperatingWindows;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 * Project : ArduinoMidiBridge
 */
public class NewMidiSensor extends JFrame {
    private static boolean open = false;

    public NewMidiSensor(Vector<Integer> availaibleMidiPort, OperatingWindows toPack) {
        super("Ajout d'un nouveau capteur midi");
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
        nameField.setPreferredSize(new Dimension(150, 20));

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

        /**MidiPortLabel**/
        JLabel midiPortLabel = new JLabel("Port midi : ");
        changeColor(midiPortLabel);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        mainPanel.add(midiPortLabel, constraints);

        /**MidiPortCombo**/
        JComboBox<Integer> midiPortCombo = new JComboBox<>(availaibleMidiPort);
        midiPortCombo.setSelectedIndex(0);
        changeColor(midiPortCombo);

        constraints.gridwidth = 2;
        constraints.gridx = 2;
        mainPanel.add(midiPortCombo, constraints);

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

        okButton.addActionListener(e -> {
            String newName = nameField.getText();
            int arduIn = (int) arduinoInCombo.getSelectedItem();
            int midiPort = (int) midiPortCombo.getSelectedItem();
            if (newName != null && !newName.equals("")) {
                new Thread(() -> {
                    OperatingWindows.addMidiSensor(newName, arduIn, midiPort);
                    toPack.cleanPack();
                    open = false;
                }).start();
                dispose();
            } else {
                String message = "<html><center>Veuillez entrer un nom pour ce capteur " +
                        "</center></html>";
                JOptionPane.showMessageDialog(null,
                        message,
                        " Erreur ",
                        JOptionPane.ERROR_MESSAGE);
            }

        });

        this.add(mainPanel);
        this.pack();
        this.setVisible(true);

    }

    public static boolean isOpen() {
        return open;
    }

    public static void main(String[] args) {
        Vector<Integer> v = new Vector<>();
        for (int i = 0; i < 128; i++) {
            v.addElement(i);
        }
        OperatingWindows f = new OperatingWindows(true);
        new NewMidiSensor(v, f);
    }

    private void changeColor(JComponent comp) {
        comp.setBackground(OperatingWindows.BACKGROUND_COLOR);
        comp.setForeground(OperatingWindows.FOREGROUND_COLOR);
    }
}

