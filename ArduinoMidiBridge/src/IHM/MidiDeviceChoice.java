package IHM;

import Metier.ArduinoInData;
import Metier.MidiManager;
import Metier.SensorManagement;
import Metier.Services;

import javax.sound.midi.MidiDevice;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import static java.lang.System.exit;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class MidiDeviceChoice extends JFrame{
    public static final int ARDUINO_CONNECTION = 0;
    public static final int NETWORK_CONNECTION = 1;
    private static JComboBox arduinoCom;
    private static JLabel arduinoLabel;
    private static JButton arduinoCheck;
    private static JButton clientConnexion;
    private static JSeparator sep;
    private static boolean arduinoConnected;     //The communication with the arduino have already been established
    private static boolean networkConnected = false;
    private final Color BACKGROUND_COLOR = OperatingWindows.BACKGROUND_COLOR;
    private final Color BUTTON_COLOR = OperatingWindows.BUTTON_COLOR;
    private final Color FOREGROUND_COLOR = OperatingWindows.FOREGROUND_COLOR;
    private final Color NAME_COLOR = OperatingWindows.NAME_COLOR;
    private final Border RAISED_BORDER = OperatingWindows.RAISED_BORDER;
    private final Border LOWERED_BORDER = OperatingWindows.LOWERED_BORDER;
    private final Border ETCHED_BORDER = OperatingWindows.ETCHED_BORDER;
    private JButton okButton;
    private JButton reloadButton;
    private JButton quitButton;
    private JList deviceList;
    private JProgressBar reloadProgress;
    private JLabel deviceDescription;
    private MidiDevice.Info choosenDevice = null;
    private boolean midiConnected = false; //a midiReceiver have been selected;
    private ConnexionInfo connexionInfo;

    /**
     * Constructor for the frame Midi Device Choice
     * @param connectionToSet true if a connection needs to be done
     */
    public MidiDeviceChoice(boolean connectionToSet) {
        super("Choix du périphérique midi");
        this.setPreferredSize(new Dimension(800, 600));
        this.setResizable(false);
        this.setUndecorated(true);
        setVisible(true);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("logo.png").getImage());
        this.setLocationRelativeTo(null);

        arduinoConnected = !connectionToSet; //the arduino is connected if connectionToSet is false (don't need to be set)

        /**Main Panel**/
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setForeground(FOREGROUND_COLOR);
        TitledBorder mainPanelTitle = BorderFactory.createTitledBorder(
                RAISED_BORDER,
                "Choisissez votre périphérique Midi" );
        mainPanelTitle.setTitleColor(FOREGROUND_COLOR);
        mainPanel.setBorder(mainPanelTitle);
        this.add(mainPanel);
        GridBagConstraints mainConstraint = new GridBagConstraints();
        mainConstraint.ipadx = 20;
        mainConstraint.ipady = 20;

        /**arduino Setting Panel**/
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setForeground(FOREGROUND_COLOR);
        TitledBorder topPanelTitle = BorderFactory.createTitledBorder(LOWERED_BORDER,
                "Choix du canal de communication Arduino");
        topPanelTitle.setTitleColor(FOREGROUND_COLOR);
        topPanel.setBorder(topPanelTitle);
        mainConstraint.gridx = 0;
        mainConstraint.gridy = 0;
        mainConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainConstraint.weightx = 1;
        mainConstraint.gridwidth = 1;
        mainPanel.add(topPanel, mainConstraint);
        topPanel.setVisible(connectionToSet);

        /******************/
        /***ArduinoLabel***/
        /******************/
        arduinoLabel = new JLabel("Port de communication : ");
        arduinoLabel.setBackground(BACKGROUND_COLOR);
        arduinoLabel.setForeground(FOREGROUND_COLOR);
        GridBagConstraints topConstraint = new GridBagConstraints();
        topConstraint.anchor = GridBagConstraints.LINE_START;
        topConstraint.fill = GridBagConstraints.BOTH;
        topConstraint.gridx = 0;
        topConstraint.gridy = 0;
        topPanel.add(arduinoLabel,topConstraint);

        /*******************/
        /***ComPort Combo***/
        /*******************/
        arduinoCom = new JComboBox(Services.findSerial());
        arduinoCom.setEditable(true);
        arduinoCom.setBackground(BACKGROUND_COLOR);
        arduinoCom.setForeground(FOREGROUND_COLOR);
        topConstraint.gridx = topConstraint.gridx + 1;
        topConstraint.weightx = 3;
        topPanel.add(arduinoCom, topConstraint);

        ++topConstraint.gridx;
        topConstraint.weightx = 0;
        topPanel.add(Box.createHorizontalStrut(10), topConstraint);
        topConstraint.weightx = 1;

        /******************/
        /***check Button***/
        /******************/
        arduinoCheck = new JButton("Valider");
        arduinoCheck.setBackground(BUTTON_COLOR);
        arduinoCheck.setForeground(FOREGROUND_COLOR);
        arduinoCheck.setBorder(RAISED_BORDER);
        topConstraint.gridx = topConstraint.gridx + 1;
        topConstraint.weightx = 0.5;
        topPanel.add(arduinoCheck, topConstraint);

        arduinoCheck.addActionListener(e -> {
            final String[] errorMessage = {null};
            new Thread(() -> {
                ArduinoInData aid = new ArduinoInData();
                String port = (String) arduinoCom.getSelectedItem();
                System.out.println(port);
                switch (aid.initialize(port)) {
                    case ArduinoInData.NO_ERR:
                        connect(ARDUINO_CONNECTION);
                        break;
                    case ArduinoInData.PORT_NOT_FOUND:
                        errorMessage[0] = "<html><center>Impossible de trouver le port spécifié " +
                                "<br> Veuillez réessayer</center><html>";
                        break;
                    case ArduinoInData.SERIAL_ERR:
                        errorMessage[0] = "<html><center>Erreur de configuration du port série" +
                                "</center><html>";
                        break;
                    case ArduinoInData.PORT_IN_USE:
                        errorMessage[0] = "<html><center>Ce port est utilisé" +
                                "<br> Veuillez déconnecter les autres applications utilisant l'arduino</center><html>";
                        break;
                    case ArduinoInData.TOO_MANY_LIST_ERR:
                        errorMessage[0] = "<html><center>Trop d'EventListener sur ce port" +
                                "</center><html>";
                        break;
                }
                if(errorMessage[0] !=null){
                    JOptionPane.showMessageDialog(MidiDeviceChoice.this, errorMessage[0], "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }).start();
        });

        ++topConstraint.gridx;
        topConstraint.weightx = 0;
        topPanel.add(Box.createHorizontalStrut(10), topConstraint);
        topConstraint.weightx = 1;

        /********************/
        /******Separator*****/
        /********************/
        sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 30));
        ++topConstraint.gridx;
        topConstraint.weightx = 0;
        topPanel.add(sep, topConstraint);

        ++topConstraint.gridx;
        topPanel.add(Box.createHorizontalStrut(10), topConstraint);
        topConstraint.weightx = 1;

        /********************/
        /***Network Button***/
        /********************/
        clientConnexion = new JButton("Connexion Client réseau");
        clientConnexion.setBackground(BUTTON_COLOR);
        clientConnexion.setForeground(FOREGROUND_COLOR);
        clientConnexion.setBorder(RAISED_BORDER);

        ++topConstraint.gridx;
        topConstraint.weightx = 1;
        topPanel.add(clientConnexion, topConstraint);

        clientConnexion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        connexionInfo = new ConnexionInfo();
                    }
                });
            }
        });


        /**********************************************************/
        /*********************JScroll Pane List********************/
        deviceList = new JList();
        deviceList.setBackground(BACKGROUND_COLOR);
        deviceList.setForeground(FOREGROUND_COLOR);
        JScrollPane scrollList = new JScrollPane(deviceList);
        scrollList.setBackground(BACKGROUND_COLOR);
        scrollList.setForeground(FOREGROUND_COLOR);
        scrollList.setBorder(LOWERED_BORDER);
        mainConstraint.gridy = 1;
        mainConstraint.gridx = 0;
        mainConstraint.weighty =1;
        mainConstraint.fill = GridBagConstraints.BOTH;
        mainPanel.add(scrollList, mainConstraint);

        deviceList.addListSelectionListener(e -> new Thread(() -> {
            choosenDevice = (MidiDevice.Info) deviceList.getSelectedValue();
            String description = choosenDevice.getDescription();
            String vendor = choosenDevice.getVendor();
                    midiConnected = MidiManager.chooseMidiDevice(choosenDevice);
            SwingUtilities.invokeLater(() -> deviceDescription.setText("<html>Description : " + description + "<br>"
                                        + "Vendeur : " + vendor + "</html>"));
        }).start()

        );


        /*****Details Panel*****/
        JPanel deviceDetails = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deviceDetails.setBackground(BACKGROUND_COLOR);
        deviceDetails.setForeground(FOREGROUND_COLOR);
        deviceDetails.setBorder(ETCHED_BORDER);
        mainConstraint.gridy = 2;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainConstraint.weighty = 0;
        mainPanel.add(deviceDetails, mainConstraint);

        /**Details Label**/
        deviceDescription = new JLabel("<html>Description : <br>Vendeur : </html> ");
        deviceDescription.setBackground(BACKGROUND_COLOR);
        deviceDescription.setForeground(FOREGROUND_COLOR);
        deviceDetails.add(deviceDescription);


        /*****Bottom Pannel*****/
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setForeground(FOREGROUND_COLOR);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainConstraint.gridy = 3;
        mainPanel.add(bottomPanel, mainConstraint);

        GridBagConstraints bottomConstraint = new GridBagConstraints();
        bottomConstraint.fill  =GridBagConstraints.BOTH;
        bottomConstraint.ipadx = 10;

        /**Reload Button**/
        reloadButton = new JButton("Recharger");
        reloadButton.setBackground(BUTTON_COLOR);
        reloadButton.setForeground(FOREGROUND_COLOR);
        reloadButton.setBorder(RAISED_BORDER);

        bottomConstraint.gridy = 0;
        bottomConstraint.gridx = 0;
        bottomConstraint.weightx = 1;
        bottomConstraint.weighty = 1;
        bottomPanel.add(reloadButton, bottomConstraint);

        reloadButton.addActionListener(e -> new Thread(() -> {
        SwingUtilities.invokeLater(() -> {
            reloadProgress.setVisible(true);
            reloadButton.setVisible(false);
            quitButton.setVisible(false);
            okButton.setVisible(false);
        });
        Vector<MidiDevice.Info> availableDevice = MidiManager.getAvailableMidiDevices();
        choosenDevice = null;
        SwingUtilities.invokeLater(() -> {
            deviceList.setListData(availableDevice);
            reloadProgress.setVisible(false);
            reloadButton.setVisible(true);
            quitButton.setVisible(true);
            okButton.setVisible(true);
        });
    }).start()

        );

        /***Progress Bar for reloading***/
        reloadProgress = new JProgressBar(SwingConstants.HORIZONTAL);
        reloadProgress.setBackground(BACKGROUND_COLOR);
        reloadProgress.setForeground(FOREGROUND_COLOR);
        reloadProgress.setIndeterminate(true);
        reloadProgress.setBorderPainted(true);
        reloadProgress.setVisible(false);

        bottomConstraint.fill = GridBagConstraints.BOTH;
        bottomConstraint.gridx = 3;
        bottomConstraint.weighty = 1;
        bottomConstraint.weightx = 1;
        bottomConstraint.gridwidth = 5;
        bottomPanel.add(reloadProgress, bottomConstraint);


        bottomConstraint.gridx = 2;
        bottomConstraint.gridwidth = 1;
        bottomPanel.add(Box.createHorizontalStrut(5), bottomConstraint);

        /**QuitButton**/
        if (connectionToSet) {
            quitButton = new JButton("Quitter");
        }
        else{
            quitButton = new JButton("Annuler");
        }
        quitButton.setBackground(BUTTON_COLOR);
        quitButton.setForeground(FOREGROUND_COLOR);
        quitButton.setBorder(RAISED_BORDER);

        bottomConstraint.fill = GridBagConstraints.BOTH;
        ++bottomConstraint.gridx;
        bottomConstraint.weightx = 1;
        bottomConstraint.weighty = 1;
        bottomPanel.add(quitButton, bottomConstraint);

        quitButton.addActionListener(e -> {
            if (connectionToSet) {
                MidiManager.exit();
                ArduinoInData.close();
                exit(0);
            }
            else{
                dispose();
            }
        });

        ++bottomConstraint.gridx;
        bottomPanel.add(Box.createHorizontalStrut(5), bottomConstraint);

        /**OKButton**/
        okButton = new JButton("OK");
        okButton.setBackground(BUTTON_COLOR);
        okButton.setForeground(FOREGROUND_COLOR);
        okButton.setBorder(RAISED_BORDER);

        ++bottomConstraint.gridx;
        bottomPanel.add(okButton, bottomConstraint);

        okButton.addActionListener(e -> {
            if ((midiConnected && arduinoConnected) || (midiConnected && networkConnected)) {//If a valid midi device is 
                // choosen and arduino or network is ok -> we set midi receiver
                new Thread(SensorManagement::changeReceiver).start();
                dispose();
                if (connectionToSet) {
                    new OperatingWindows(!networkConnected); //if connected, the application is a client
                }
            } else if (!midiConnected) {
                JOptionPane.showMessageDialog(MidiDeviceChoice.this,
                        "<html><center>Impossible de se connecter au " +
                                "périphérique midi sélectionné<br> " +
                                "Veuillez réessayer</center></html>",
                        " Avertissement ",
                        JOptionPane.WARNING_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(MidiDeviceChoice.this,
                        "<html><center>Communication arduino ou réseau non établie" +
                                "</center></html>",
                        " Avertissement ",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        //Filling the List
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                reloadProgress.setVisible(true);
                reloadButton.setVisible(false);
                quitButton.setVisible(false);
                okButton.setVisible(false);
            });
            Vector<MidiDevice.Info> availableDevice = MidiManager.getAvailableMidiDevices();
            deviceList.setListData(availableDevice);
            SwingUtilities.invokeLater(() -> {
                reloadProgress.setVisible(false);
                reloadButton.setVisible(true);
                quitButton.setVisible(true);
                okButton.setVisible(true);
            });
        }).start();


    }

    public static void connect(int meanOfConnection) {
        SwingUtilities.invokeLater(() -> {
            arduinoCom.setVisible(false);
            arduinoCheck.setVisible(false);
            clientConnexion.setVisible(false);
            sep.setVisible(false);
            switch (meanOfConnection) {
                case ARDUINO_CONNECTION:
                    arduinoLabel.setText("Connexion à l'arduino effectuée");
                    arduinoConnected = true;
                    break;
                case NETWORK_CONNECTION:
                    arduinoLabel.setText("Connexion au serveur effectuée");
                    networkConnected = true;
            }

        });
    }


    public static void main (String[] args){
        JFrame frame = new MidiDeviceChoice(true);
        frame.setVisible(true);
        frame.pack();
    }
}
