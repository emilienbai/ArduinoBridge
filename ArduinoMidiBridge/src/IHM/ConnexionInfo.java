package IHM;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 07/2015.
 */


public class ConnexionInfo extends JFrame {

    private final Color BACKGROUND_COLOR = OperatingWindows.BACKGROUND_COLOR;
    private final Color FOREGROUND_COLOR = OperatingWindows.FOREGROUND_COLOR;
    private final Color BUTTON_COLOR = OperatingWindows.BUTTON_COLOR;
    private final Border RAISED_BORDER = OperatingWindows.RAISED_BORDER;
    private final Border LOWERED_BORDER = OperatingWindows.LOWERED_BORDER;

    public ConnexionInfo() {
        super("Connexion au serveur");
        //this.setUndecorated(true);
        this.setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setForeground(FOREGROUND_COLOR);
        mainPanel.setBorder(new MatteBorder(3, 3, 3, 3, OperatingWindows.NAME_COLOR));
        mainPanel.setPreferredSize(new Dimension(400, 120));

        JLabel addressLabel = new JLabel("Adresse : ");
        addressLabel.setForeground(FOREGROUND_COLOR);
        mainPanel.add(addressLabel);


        JTextArea addressArea = new JTextArea();
        addressArea.setBackground(BACKGROUND_COLOR);
        addressArea.setForeground(FOREGROUND_COLOR);
        addressArea.setBorder(LOWERED_BORDER);
        addressArea.setPreferredSize(new Dimension(100, 15));
        mainPanel.add(addressArea);

        JLabel portLabel = new JLabel("Port : ");
        portLabel.setBackground(BACKGROUND_COLOR);
        portLabel.setForeground(FOREGROUND_COLOR);
        mainPanel.add(portLabel);

        JTextArea portArea = new JTextArea();
        portArea.setBackground(BACKGROUND_COLOR);
        portArea.setForeground(FOREGROUND_COLOR);
        portArea.setBorder(LOWERED_BORDER);
        portArea.setPreferredSize(new Dimension(100, 15));
        mainPanel.add(portArea);

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setBackground(BUTTON_COLOR);
        cancelButton.setForeground(FOREGROUND_COLOR);
        cancelButton.setBorder(RAISED_BORDER);
        mainPanel.add(cancelButton);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConnexionInfo.this.setVisible(false);
                ConnexionInfo.this.dispose();
            }
        });

        JButton okButton = new JButton("OK");
        okButton.setBackground(BUTTON_COLOR);
        okButton.setForeground(FOREGROUND_COLOR);
        okButton.setBorder(RAISED_BORDER);
        mainPanel.add(okButton);

        //todo add actionlistener


        this.add(mainPanel);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        /*JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JButton button = new JButton("ClickMe");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConnexionInfo();
            }
        });

        panel.add(button);
        frame.add(panel);
        frame.setVisible(true);
        frame.pack();*/
        new ConnexionInfo();

    }
}
