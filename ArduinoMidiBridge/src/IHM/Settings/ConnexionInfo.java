package IHM.Settings;

import IHM.OperatingWindows;
import Metier.Services;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 07/2015.
 */

/**
 * Frame to fill with informations to connect a server
 */
public class ConnexionInfo extends JFrame {

    /**
     * Constructor for a client connexion information
     */
    public ConnexionInfo() {
        super("Connexion au serveur");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBackground(OperatingWindows.BACKGROUND_COLOR);
        mainPanel.setForeground(OperatingWindows.FOREGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setPreferredSize(new Dimension(400, 120));

        JLabel addressLabel = new JLabel("Adresse : ");
        addressLabel.setForeground(OperatingWindows.FOREGROUND_COLOR);
        mainPanel.add(addressLabel);

        /**Field for the server address**/
        JTextField addressField = new JTextField();
        addressField.setBackground(OperatingWindows.BACKGROUND_COLOR);
        addressField.setForeground(OperatingWindows.FOREGROUND_COLOR);
        addressField.setBorder(OperatingWindows.LOWERED_BORDER);
        addressField.setPreferredSize(new Dimension(100, 15));
        mainPanel.add(addressField);

        JLabel portLabel = new JLabel("Port : ");
        portLabel.setBackground(OperatingWindows.BACKGROUND_COLOR);
        portLabel.setForeground(OperatingWindows.FOREGROUND_COLOR);
        mainPanel.add(portLabel);

        /**Field for the port number**/
        JTextField portField = new JTextField("5000");
        portField.setBackground(OperatingWindows.BACKGROUND_COLOR);
        portField.setForeground(OperatingWindows.FOREGROUND_COLOR);
        portField.setBorder(OperatingWindows.LOWERED_BORDER);
        portField.setPreferredSize(new Dimension(100, 15));
        mainPanel.add(portField);

        /**Cancel the connexion procedure**/
        JButton cancelButton = new JButton("Annuler");
        cancelButton.setBackground(OperatingWindows.BUTTON_COLOR);
        cancelButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        cancelButton.setBorder(OperatingWindows.RAISED_BORDER);
        mainPanel.add(cancelButton);

        cancelButton.addActionListener(e -> ConnexionInfo.this.dispose());

        /**Validate the connexion tentative**/
        JButton okButton = new JButton("OK");
        okButton.setBackground(OperatingWindows.BUTTON_COLOR);
        okButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        okButton.setBorder(OperatingWindows.RAISED_BORDER);
        mainPanel.add(okButton);

        okButton.addActionListener(e -> {
            String hostname = addressField.getText();
            String port = portField.getText();
            if (hostname != null && port != null && !hostname.equals("") && !port.equals("") && !Services.isClient()) {
                try {
                    int portNumber = Integer.parseInt(port);
                    if (Services.connectClient(hostname, portNumber)) {
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ConnexionInfo.this, "Echec de la connexion au serveur Assurez vous que les paramètres renseignés sont corrects.");
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(ConnexionInfo.this, "Le port doit être un nombre");
                }
            } else if (Services.isClient()) {
                dispose();
            }
        });

        this.add(mainPanel);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new ConnexionInfo();
    }
}
