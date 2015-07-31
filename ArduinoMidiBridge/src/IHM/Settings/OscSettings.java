package IHM.Settings;

import IHM.OperatingWindows;
import Metier.Services;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 * Project : ArduinoMidiBridge
 */
public class OscSettings extends JFrame {
    private static boolean open;
    private boolean launched;

    public OscSettings(boolean launched) {
        super("Paramètres OSC");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.launched = launched;
        open = true;

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                open = false;
                dispose();
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
        changeColor(mainPanel);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(3, 5, 3, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        /**IP address**/
        JLabel ipLabel = new JLabel("Ip destinataire : ");
        changeColor(ipLabel);

        mainPanel.add(ipLabel, constraints);

        /**IPField**/
        JTextField ipField = new JTextField(Services.getOscAddress());
        changeColor(ipField);
        ipField.setPreferredSize(new Dimension(90, 20));
        if (launched) {
            ipField.setEnabled(false);
        }

        constraints.gridx = 1;
        constraints.gridwidth = 2;
        mainPanel.add(ipField, constraints);

        /**PortLabel**/
        JLabel portLabel = new JLabel("Port : ");
        changeColor(portLabel);

        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy++;

        mainPanel.add(portLabel, constraints);

        /**PortField**/
        JTextField portField = new JTextField(String.valueOf(Services.getOscPort()));
        changeColor(portField);
        if (launched) {
            portField.setEnabled(false);
        }
        constraints.gridx = 1;
        constraints.gridwidth = 2;

        mainPanel.add(portField, constraints);

        /**CancelButton**/
        JButton cancelButton = new JButton("Fermer");
        cancelButton.setBackground(OperatingWindows.BUTTON_COLOR);
        cancelButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        cancelButton.setBorder(OperatingWindows.RAISED_BORDER);
        cancelButton.setPreferredSize(new Dimension(70, 35));

        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy++;
        mainPanel.add(cancelButton, constraints);

        cancelButton.addActionListener(e -> {
            dispose();
            open = false;
        });

        /**OKButton**/
        JButton okButton;
        if (!launched) {
            okButton = new JButton("Lancer le serveur OSC");
        } else {
            okButton = new JButton("Stopper le serveur OSC");
        }
        okButton.setBackground(OperatingWindows.BUTTON_COLOR);
        okButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        okButton.setBorder(OperatingWindows.RAISED_BORDER);
        okButton.setPreferredSize(new Dimension(180, 35));

        constraints.gridx = 2;
        mainPanel.add(okButton, constraints);

        this.add(mainPanel);
        this.pack();
        this.setVisible(true);

        okButton.addActionListener(e -> new Thread(() -> {
            if (!this.launched) {
                String address = ipField.getText();
                try {
                    int port = Integer.parseInt(portField.getText());
                    if (Services.launchOscServer(address, port)) {
                        this.launched = true;
                        Services.changeOscPortOut();
                        SwingUtilities.invokeLater(() -> {
                            okButton.setText("Stopper le serveur OSC");
                            ipField.setEnabled(false);
                            portField.setEnabled(false);
                        });

                    } else {
                        JOptionPane.showMessageDialog(OscSettings.this, "Echec au lancement du serveur OSC",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(OscSettings.this, "Le port doit être un nombre entier",
                            "Avertissement", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                Services.closeOscServer();
                this.launched = false;
                SwingUtilities.invokeLater(() -> {
                    okButton.setText("Lancer le serveur OSC");
                    ipField.setEnabled(true);
                    portField.setEnabled(true);
                });
            }
            OperatingWindows.setOscStatus(this.launched);

        }).start());
    }

    /**
     * Getter for the status of this instance of Frame
     *
     * @return true if an OscSettings frame is already open
     */
    public static boolean isOpen() {
        return open;
    }

    public static void main(String[] args) {
        new OscSettings(true);
    }

    /**
     * Change the color of a component
     *
     * @param comp the JComponent to modify
     */
    private void changeColor(JComponent comp) {
        comp.setBackground(OperatingWindows.BACKGROUND_COLOR);
        comp.setForeground(OperatingWindows.FOREGROUND_COLOR);
    }
}