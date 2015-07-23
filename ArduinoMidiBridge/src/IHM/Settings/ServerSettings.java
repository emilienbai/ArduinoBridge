package IHM.Settings;

import IHM.OperatingWindows;
import Metier.Services;
import Network.Server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 07/2015.
 */
public class ServerSettings extends JFrame {

    private static JTextArea logsArea;

    /**
     * Constructor for a ServerSettings Frame
     */
    public ServerSettings() {
        super("Paramètres serveurs");

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainConstraints = new GridBagConstraints();
        mainConstraints.fill = GridBagConstraints.BOTH;
        mainConstraints.weightx = 1;
        mainConstraints.weighty = 1;
        mainConstraints.insets = new Insets(5, 5, 5, 5);
        mainPanel.setBackground(OperatingWindows.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(mainPanel);

        /**PortLabel**/
        JLabel portLabel = new JLabel("Port n° :");
        portLabel.setForeground(OperatingWindows.FOREGROUND_COLOR);
        mainConstraints.gridx = 0;
        mainConstraints.gridy = 0;

        mainPanel.add(portLabel, mainConstraints);

        /**PortTextField**/
        JTextField portTextField = new JTextField("5000");
        portTextField.setBackground(OperatingWindows.BACKGROUND_COLOR);
        portTextField.setForeground(OperatingWindows.FOREGROUND_COLOR);
        portTextField.setBorder(OperatingWindows.LOWERED_BORDER);
        ++mainConstraints.gridx;

        mainPanel.add(portTextField, mainConstraints);

        /**LaunchButton**/
        JButton launchButton = new JButton("Lancer le serveur");
        launchButton.setBackground(OperatingWindows.BUTTON_COLOR);
        launchButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        launchButton.setBorder(OperatingWindows.RAISED_BORDER);
        launchButton.setPreferredSize(new Dimension(80, 30));
        ++mainConstraints.gridx;

        mainPanel.add(launchButton, mainConstraints);

        launchButton.addActionListener(e -> new Thread(() -> {
            if (!Server.isRunning()) {
                try {
                    int port = Integer.parseInt(portTextField.getText());
                    if (Server.connect(port)) {
                        Services.enableServer();

                        SwingUtilities.invokeLater(() -> {
                            launchButton.setText("Stopper le serveur");
                            portTextField.setEnabled(false);
                            ServerSettings.this.repaint();
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ServerSettings.this, "Echec au démarrage du serveur, le port " +
                                "spécifié est peut être déjà utilisé", "Erreur", JOptionPane.ERROR_MESSAGE));
                    }
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(ServerSettings.this, "Le port spécifié doit être un nombre", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Services.disableServer();
                SwingUtilities.invokeLater(() -> {
                    launchButton.setText("Lancer le serveur");
                    portTextField.setEnabled(true);
                });
            }
        }).start());

        /**LogsLabel**/
        JLabel logsLabel = new JLabel("Logs : ");
        logsLabel.setForeground(OperatingWindows.FOREGROUND_COLOR);
        ++mainConstraints.gridy;
        mainConstraints.gridx = 0;
        mainConstraints.gridwidth = 2;
        mainPanel.add(logsLabel, mainConstraints);

        /**LogsArea**/
        logsArea = new JTextArea();
        logsArea.setBackground(Color.BLACK);
        logsArea.setForeground(Color.WHITE);
        logsArea.setEditable(false);
        mainConstraints.gridx = 0;
        mainConstraints.gridwidth = 3;
        ++mainConstraints.gridy;

        JScrollPane scrollLogs = new JScrollPane(logsArea);
        scrollLogs.setPreferredSize(new Dimension(400, 250));

        mainPanel.add(scrollLogs, mainConstraints);

        /**IPLabel**/
        JLabel ipLabel = new JLabel("Mon IP : " + Server.getIP());
        ipLabel.setForeground(OperatingWindows.FOREGROUND_COLOR);
        mainConstraints.gridy++;
        mainConstraints.gridx = 0;

        mainPanel.add(ipLabel, mainConstraints);

        /**CloseButton**/
        JButton closeButton = new JButton("Fermer");
        closeButton.setBackground(OperatingWindows.BUTTON_COLOR);
        closeButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        closeButton.setBorder(OperatingWindows.RAISED_BORDER);
        closeButton.setPreferredSize(new Dimension(120, 30));
        mainConstraints.gridx = 2;

        mainPanel.add(closeButton, mainConstraints);

        closeButton.addActionListener(e -> dispose());

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
        this.pack();

    }

    /**
     * Method called to refresh the server logs area
     *
     * @param logs the logs to set in the logs Area
     */
    public static void fillInfo(String logs) {
        logsArea.setText(logs);
        logsArea.repaint();
    }

    public static void main(String[] args) {
        new ServerSettings();
    }
}
