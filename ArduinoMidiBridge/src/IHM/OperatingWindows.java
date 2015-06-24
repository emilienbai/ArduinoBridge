package IHM;

import Metier.*;
import Sensor.Sensor;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class OperatingWindows extends JFrame {
    /*********************************************************************/
    /******************************COLORS*********************************/
    /*********************************************************************/
    public static final Color BACKGROUND_COLOR = new Color(21, 21, 35);
    public static final Color BUTTON_COLOR = new Color(0, 0, 64);
    public static final Color FOREGROUND_COLOR = new Color(126, 145, 185);
    public static final Color MUTE_COLOR = new Color(174, 36, 33);
    public static final Color SOLO_COLOR = new Color(169, 162, 0);
    public static final Color IMPULSE_COLOR = new Color(45, 121, 36);
    public static final Color NAME_COLOR = new Color(221, 101, 4);
    /************/
    public static final Border RAISED_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    public static final Border LOWERED_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    public static final Border ETCHED_BORDER = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    /***********/
    private static final String SAVE_EXTENSION = ".xml";
    private static final int DEFAULT_THRESHOLD = 100;
    private static final int DEFAULT_DEBOUNCE = 200;

    /***********/
    private static JPanel centerPanel;
    private static JMenuBar menuBar;
    private static Vector<Integer> availableMidiPort = new Vector<>();
    private static boolean isMutedAll;
    private static java.util.List<SensorRow> sensorRowList = new ArrayList<>();
    private static java.util.List<DeleteButton> deleteButtonList = new ArrayList<>();
    private static JComboBox availableMidiCombo;
    private static JPanel topPanel;
    private static VuMeter selectedSensorVuMeter;
    private static GridBagConstraints topConstraint;
    private static JTextArea logsArea;
    private static int selectedSensor = 0;
    private static boolean built = false; //is the window built?
    private JTextArea debounceOneText;
    private JTextArea thresholdOneTextArea;
    private JLabel sensorStatus;
    private JButton muteAllButton;
    private JTextField newSensorName;
    private JComboBox arduinoPort;
    private String newName = null;
    private int newArduChan = -1;
    private int newMidiPort = -1;

    /************/
    /**BORDERS***/
    private GridBagConstraints centerConstraint;
    private File saveFile = null;
    private JLabel sensorNumberLb;

    public OperatingWindows() {
        super("ArduinoBrigde");
        pack();
        setPreferredSize(new Dimension(800, 400));
        setMinimumSize(new Dimension(200, 100));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                MidiManager.exit();
                arduinoInData.close();
                dispose();
                System.exit(0);
            }
        });
        this.setMenuBar();
        this.setJMenuBar(menuBar);

        setVisible(true);
        GridBagLayout mainLayout = new GridBagLayout();
        GridBagConstraints mainConstraint = new GridBagConstraints();
        JPanel mainPanel = new JPanel(mainLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);


        /******************************************/
        /****************Top Panel*****************/
        /******************************************/
        mainConstraint.fill = GridBagConstraints.BOTH;
        mainConstraint.weighty = 1;
        mainConstraint.weightx = 1;
        mainConstraint.gridy = mainConstraint.gridy + 1;
        mainConstraint.gridx = 0;

        GridBagLayout topLayout = new GridBagLayout();
        topConstraint = new GridBagConstraints();
        topConstraint.fill = GridBagConstraints.HORIZONTAL;

        topPanel = new JPanel(topLayout);
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(LOWERED_BORDER);
        topPanel.add(new JSeparator(JSeparator.VERTICAL));
        mainPanel.add(topPanel, mainConstraint);


        /***********Selected sensor Vu Meter*******/
        selectedSensorVuMeter = new VuMeter(SwingConstants.VERTICAL, 0, 1024);
        selectedSensorVuMeter.setPreferredSize(new Dimension(15, 120));
        topConstraint.gridx = 0;
        topConstraint.weighty = 1;
        topConstraint.weightx = 0;

        addVerticalSeparation(10);
        ++topConstraint.gridx;
        topConstraint.gridheight = 3;
        topPanel.add(selectedSensorVuMeter, topConstraint);


        addVerticalSeparation(10);
        /********1st Column Sensor Choice**********/
        JLabel sensorNumber = new JLabel("<html><center>Numéro de<br>Capteur</html></center>");
        sensorNumber.setBackground(BACKGROUND_COLOR);
        sensorNumber.setForeground(FOREGROUND_COLOR);
        sensorNumber.setHorizontalAlignment(JLabel.CENTER);
        topConstraint.gridx = topConstraint.gridx + 1;
        topConstraint.gridy = 0;
        topConstraint.gridheight = 1;
        topConstraint.weightx = 1;
        topPanel.add(sensorNumber, topConstraint);

        /*******1st Column SensorCombo***********/
        JComboBox sensorCombo = new JComboBox();
        for (int i = 0; i < 16; i++) {
            sensorCombo.addItem(i);
        }
        sensorCombo.setBackground(BACKGROUND_COLOR);
        sensorCombo.setForeground(FOREGROUND_COLOR);
        topConstraint.gridy = 1;
        topConstraint.weightx = 0;
        topPanel.add(sensorCombo, topConstraint);

        sensorCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    selectedSensor = (int) sensorCombo.getSelectedItem();
                    String[] channelInfo = Services.getChannelInfo(selectedSensor);
                    SwingUtilities.invokeLater(() -> {
                        debounceOneText.setText(channelInfo[0]);
                        thresholdOneTextArea.setText(channelInfo[1]);
                        if (channelInfo[2].equals("true")) {
                            sensorStatus.setText("Actif");
                            sensorStatus.setForeground(IMPULSE_COLOR);
                        } else {
                            sensorStatus.setText("Inactif");
                            sensorStatus.setForeground(MUTE_COLOR);
                        }
                    });

                }).start();

            }
        });

        /*******1st Column active Label********/
        sensorStatus = new JLabel("Actif");
        sensorStatus.setBackground(BACKGROUND_COLOR);
        sensorStatus.setForeground(IMPULSE_COLOR);
        sensorStatus.setHorizontalAlignment(JLabel.CENTER);
        topConstraint.gridy = 2;
        topPanel.add(sensorStatus, topConstraint);


        addVerticalSeparation(5);
        /********2nd Column Debounce Label*********/
        JLabel debounceOneLabel = new JLabel("<html><center>Stabilisation<br>(ms)</center></html>");
        debounceOneLabel.setHorizontalAlignment(JLabel.CENTER);
        debounceOneLabel.setBackground(BACKGROUND_COLOR);
        debounceOneLabel.setForeground(FOREGROUND_COLOR);
        topConstraint.gridx = topConstraint.gridx + 1;
        topConstraint.gridy = 0;
        topConstraint.weightx = 1;
        topPanel.add(debounceOneLabel, topConstraint);

        /*******2nd Column, DebounceTextArea*******/
        debounceOneText = new JTextArea(String.valueOf(DEFAULT_DEBOUNCE));
        debounceOneText.setBackground(BACKGROUND_COLOR);
        debounceOneText.setForeground(FOREGROUND_COLOR);
        debounceOneText.setBorder(LOWERED_BORDER);
        topConstraint.gridy = 1;
        topConstraint.weightx = 0;
        topPanel.add(debounceOneText, topConstraint);

        /*******2nd Column, DebounceOneOK**********/
        JButton debounceOneOK = new JButton("OK");
        debounceOneOK.setBackground(BUTTON_COLOR);
        debounceOneOK.setForeground(FOREGROUND_COLOR);
        debounceOneOK.setBorder(RAISED_BORDER);
        debounceOneOK.setPreferredSize(new Dimension(30, 30));
        topConstraint.gridy = 2;
        topPanel.add(debounceOneOK, topConstraint);

        debounceOneOK.addActionListener(e -> new Thread(() -> {
            try {
                int newDebounce = Integer.parseInt(debounceOneText.getText());
                Services.setDebounceOne(selectedSensor, newDebounce);
            } catch (NumberFormatException e1) {
                numberFormatWarning();
            }
        }).start());

        addVerticalSeparation(5);
        /*******3rd Column, ThresholdOneLb*********/
        JLabel thresholdOneLb = new JLabel("Seuil");
        thresholdOneLb.setBackground(BACKGROUND_COLOR);
        thresholdOneLb.setForeground(FOREGROUND_COLOR);
        thresholdOneLb.setHorizontalAlignment(JLabel.CENTER);
        topConstraint.gridx = topConstraint.gridx + 1;
        topConstraint.gridy = 0;
        topConstraint.weightx = 1;
        topPanel.add(thresholdOneLb, topConstraint);

        /*******3rd Column, thresholdOneTextArea***/
        thresholdOneTextArea = new JTextArea(String.valueOf(DEFAULT_THRESHOLD));
        thresholdOneTextArea.setBackground(BACKGROUND_COLOR);
        thresholdOneTextArea.setForeground(FOREGROUND_COLOR);
        thresholdOneTextArea.setBorder(LOWERED_BORDER);
        topConstraint.gridy = 1;
        topConstraint.weightx = 0;
        topPanel.add(thresholdOneTextArea, topConstraint);

        /******3rd Column, ThresholdOneOk**********/
        JButton thresholdOneOK = new JButton("OK");
        thresholdOneOK.setBackground(BUTTON_COLOR);
        thresholdOneOK.setForeground(FOREGROUND_COLOR);
        thresholdOneOK.setBorder(RAISED_BORDER);
        thresholdOneOK.setPreferredSize(new Dimension(30, 30));
        topConstraint.gridy = 2;
        topPanel.add(thresholdOneOK, topConstraint);

        thresholdOneOK.addActionListener(e -> new Thread(() -> {
            try {
                int newThreshold = Integer.parseInt(thresholdOneTextArea.getText());
                Services.setThresholdOne(selectedSensor, newThreshold);
            } catch (NumberFormatException e1) {
                numberFormatWarning();
            }
        }).start());


        addVerticalSeparation(5);
        /*******4th Column, CalibrateOne***********/
        JButton calibrateOne = new JButton("Calibrer");
        calibrateOne.setBackground(BUTTON_COLOR);
        calibrateOne.setForeground(FOREGROUND_COLOR);
        calibrateOne.setBorder(RAISED_BORDER);
        calibrateOne.setPreferredSize(new Dimension(30, 30));
        topConstraint.gridx = topConstraint.gridx + 1;
        topConstraint.gridy = 1;
        topConstraint.weightx = 1;
        topPanel.add(calibrateOne, topConstraint);

        calibrateOne.addActionListener(e -> new Thread(() -> {
            Services.calibrate(selectedSensor);
        }).start());

        topConstraint.weightx = 0;
        addVerticalSeparation(5);
        /*******5th Column, Separator**************/
        addVerticalSeparation(5);
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 80));
        topPanel.add(sep, topConstraint);
        addVerticalSeparation(5);
        topConstraint.weightx = 1;

        /*******6th Column, DebounceAllLabel*******/
        JLabel debounceAllLb = new JLabel("<html><center>Stabilisation<br>général (ms)</center></html>");
        debounceAllLb.setBackground(BACKGROUND_COLOR);
        debounceAllLb.setForeground(FOREGROUND_COLOR);
        debounceAllLb.setHorizontalAlignment(SwingConstants.CENTER);
        topConstraint.gridy = 0;
        ++topConstraint.gridx;
        topPanel.add(debounceAllLb, topConstraint);

        /******6th Column, DebounceAllTextArea*****/
        JTextArea debounceAllTextArea = new JTextArea(String.valueOf(DEFAULT_DEBOUNCE));
        debounceAllTextArea.setBackground(BACKGROUND_COLOR);
        debounceAllTextArea.setForeground(FOREGROUND_COLOR);
        debounceAllTextArea.setBorder(LOWERED_BORDER);
        topConstraint.gridy = 1;
        topConstraint.weightx = 0;
        topPanel.add(debounceAllTextArea, topConstraint);

        /******6th Column, DebounceAllOK***********/
        JButton debounceAllOK = new JButton("OK");
        debounceAllOK.setBackground(BUTTON_COLOR);
        debounceAllOK.setForeground(FOREGROUND_COLOR);
        debounceAllOK.setBorder(RAISED_BORDER);
        debounceAllOK.setPreferredSize(new Dimension(30, 30));
        topConstraint.gridy = 2;
        topPanel.add(debounceAllOK, topConstraint);

        debounceAllOK.addActionListener(e -> new Thread(() -> {
            try {
                int newDebounce = Integer.parseInt(debounceAllTextArea.getText());
                Services.setDebounceAll(newDebounce);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        debounceOneText.setText(String.valueOf(newDebounce));
                    }
                });
            } catch (NumberFormatException e1) {
                numberFormatWarning();
            }
        }).start());

        addVerticalSeparation(5);
        /******7th Column, thresholdAllLb**********/
        JLabel thresholdAllLb = new JLabel("<html><center>Seuil<br>général</center></html>");
        thresholdAllLb.setBackground(BACKGROUND_COLOR);
        thresholdAllLb.setForeground(FOREGROUND_COLOR);
        thresholdAllLb.setHorizontalAlignment(SwingConstants.CENTER);
        topConstraint.weightx = 1;
        ++topConstraint.gridx;
        topConstraint.gridy = 0;
        topPanel.add(thresholdAllLb, topConstraint);

        /******7th Column, thresholdAllTextArea****/
        JTextArea thresholdAllTextArea = new JTextArea(String.valueOf(DEFAULT_THRESHOLD));
        thresholdAllTextArea.setBackground(BACKGROUND_COLOR);
        thresholdAllTextArea.setForeground(FOREGROUND_COLOR);
        thresholdAllTextArea.setBorder(LOWERED_BORDER);
        topConstraint.gridy = 1;
        topConstraint.weightx = 0;
        topPanel.add(thresholdAllTextArea, topConstraint);

        /*****7th Column, thresholdAllOK***********/
        JButton thresholdAllOK = new JButton("OK");
        thresholdAllOK.setBackground(BUTTON_COLOR);
        thresholdAllOK.setForeground(FOREGROUND_COLOR);
        thresholdAllOK.setBorder(RAISED_BORDER);
        thresholdAllOK.setPreferredSize(new Dimension(30, 30));
        topConstraint.gridy = 2;
        topPanel.add(thresholdAllOK, topConstraint);

        thresholdAllOK.addActionListener(e -> new Thread(() -> {
            try {
                int newThreshold = Integer.parseInt(thresholdAllTextArea.getText());
                Services.setThresholdAll(newThreshold);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        thresholdOneTextArea.setText(String.valueOf(newThreshold));
                    }
                });
            } catch (NumberFormatException e1) {
                numberFormatWarning();
            }
        }).start());

        addVerticalSeparation(5);
        /*******8th Column SensorNumberLb*********/
        JLabel sensorNumberLabel = new JLabel("Nombre de Capteurs");
        sensorNumberLabel.setBackground(BACKGROUND_COLOR);
        sensorNumberLabel.setForeground(FOREGROUND_COLOR);
        sensorNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ++topConstraint.gridx;
        topConstraint.gridy = 0;
        topConstraint.weightx = 1;
        topPanel.add(sensorNumberLabel, topConstraint);

        /*******8th Column SensorNumberCb**********/
        JComboBox sensorNumberCb = new JComboBox();
        for (int i = 1; i <= 16; i++) {
            sensorNumberCb.addItem(i);
        }
        sensorNumberCb.setBackground(BACKGROUND_COLOR);
        sensorNumberCb.setForeground(FOREGROUND_COLOR);
        sensorNumberCb.setSelectedIndex(sensorNumberCb.getItemCount() - 1);
        topConstraint.gridy = 1;
        topConstraint.weightx = 0;
        topPanel.add(sensorNumberCb, topConstraint);

        sensorNumberCb.addActionListener(e -> new Thread(() -> {
            int newNumber = (int) sensorNumberCb.getSelectedItem();
            Services.setSensorNumber(newNumber);
        }).start());

        /******8th Column, CalibrateAllButton******/
        JButton calibrateAllButton = new JButton("Calibrer tout");
        calibrateAllButton.setBackground(BUTTON_COLOR);
        calibrateAllButton.setForeground(FOREGROUND_COLOR);
        calibrateAllButton.setBorder(RAISED_BORDER);
        calibrateAllButton.setPreferredSize(new Dimension(30, 30));
        topConstraint.gridy = 2;
        topPanel.add(calibrateAllButton, topConstraint);

        calibrateAllButton.addActionListener(e -> new Thread(() -> {
            Services.calibrateAll();
        }).start());

        addVerticalSeparation(5);
        /********9th Column, LogArea***************/
        logsArea = new JTextArea(10, 20);
        logsArea.setBackground(Color.BLACK);
        logsArea.setForeground(Color.WHITE);
        //logsArea.setEditable(false);
        JScrollPane scrollLogs = new JScrollPane(logsArea);
        topConstraint.weightx = 1;
        topConstraint.weighty = 1;
        topConstraint.gridy = 0;
        ++topConstraint.gridx;
        topConstraint.gridheight = 3;
        topConstraint.gridwidth = 3;
        topPanel.add(scrollLogs, topConstraint);

        /******************************************/
        /**************Center Panel****************/
        /******************************************/
        mainConstraint.fill = GridBagConstraints.BOTH;
        mainConstraint.weighty = 50;
        mainConstraint.weightx = 1;
        mainConstraint.gridy = mainConstraint.gridy + 1;
        mainConstraint.gridx = 0;


        GridBagLayout centerLayout = new GridBagLayout();
        centerConstraint = new GridBagConstraints();
        centerConstraint.fill = GridBagConstraints.HORIZONTAL;
        centerPanel = new JPanel(centerLayout);
        centerPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane centerPanelScroll = new JScrollPane(centerPanel);
        centerPanelScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerPanelScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanelScroll.setBackground(BACKGROUND_COLOR);
        centerPanelScroll.setForeground(FOREGROUND_COLOR);


        /*************Active sensor Label**********/
        centerConstraint.gridx = 0;
        centerConstraint.gridy = 0;
        centerConstraint.weightx = 1;
        centerConstraint.weighty = 1;
        centerConstraint.anchor = GridBagConstraints.NORTHWEST;
        JLabel activeSensors = new JLabel("Capteurs actifs : ");
        activeSensors.setBackground(BACKGROUND_COLOR);
        activeSensors.setForeground(FOREGROUND_COLOR);
        centerPanel.add(activeSensors, centerConstraint);
        mainPanel.add(centerPanelScroll, mainConstraint);


        /*********Sensor Number Label*******/
        centerConstraint.anchor = GridBagConstraints.NORTHEAST;
        centerConstraint.gridx = 1;
        sensorNumberLb = new JLabel("0");
        sensorNumberLb.setBackground(BACKGROUND_COLOR);
        sensorNumberLb.setForeground(FOREGROUND_COLOR);
        centerPanel.add(sensorNumberLb, centerConstraint);

        /******************************************/
        /**************Bottom Panel****************/
        /******************************************/
        mainConstraint.weighty = 1;
        mainConstraint.gridy = mainConstraint.gridy + 1;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainConstraint.anchor = GridBagConstraints.LAST_LINE_END;
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(bottomPanel, mainConstraint);

        /*****************Mute All*****************/
        isMutedAll = false;
        muteAllButton = new JButton(("Mute All"));
        muteAllButton.setBackground(BUTTON_COLOR);
        muteAllButton.setForeground(FOREGROUND_COLOR);
        muteAllButton.setBorder(ETCHED_BORDER);
        muteAllButton.setPreferredSize(new Dimension(90, 25));
        bottomPanel.add(muteAllButton);

        muteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    if (!isMutedAll) {
                        SensorManagement.muteAll();
                        isMutedAll = true;
                        SwingUtilities.invokeLater(() -> muteAllButton.setBackground(MUTE_COLOR));
                    } else {
                        SensorManagement.unMuteAll();
                        isMutedAll = false;
                        SwingUtilities.invokeLater(() -> muteAllButton.setBackground(BUTTON_COLOR));
                    }
                }).start();
            }
        });

        /*****************NameLabel****************/
        JLabel newNameLabel = new JLabel("Nom du nouveau capteur : ");
        newNameLabel.setBackground(BACKGROUND_COLOR);
        newNameLabel.setForeground(FOREGROUND_COLOR);
        bottomPanel.add(newNameLabel);

        /*****************New name*****************/
        newSensorName = new JTextField();
        newSensorName.setBackground(BACKGROUND_COLOR);
        newSensorName.setForeground(FOREGROUND_COLOR);
        newSensorName.setPreferredSize(new Dimension(115, 25));
        bottomPanel.add(newSensorName);

        newSensorName.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                new Thread(() -> {
                    newName = newSensorName.getText();
                }).start();
            }
        });

        /*****************ArduChLabel***************/
        JLabel newChan = new JLabel("Canal Arduino : ");
        newChan.setBackground(BACKGROUND_COLOR);
        newChan.setForeground(FOREGROUND_COLOR);
        bottomPanel.add(newChan);

        /*****************ArduinoCh*****************/
        arduinoPort = new JComboBox();
        for (int i = 0; i < 16; i++) {
            arduinoPort.addItem(i);
        }
        arduinoPort.setBackground(BACKGROUND_COLOR);
        arduinoPort.setForeground(FOREGROUND_COLOR);
        bottomPanel.add(arduinoPort);

        arduinoPort.addActionListener(e -> new Thread(() -> {
            newArduChan = (int) arduinoPort.getSelectedItem();
        }).start());

        /*****************MidiPoLb*****************/
        JLabel midiPortLabel = new JLabel("Port Midi");
        midiPortLabel.setBackground(BACKGROUND_COLOR);
        midiPortLabel.setForeground(FOREGROUND_COLOR);
        bottomPanel.add(midiPortLabel);

        /*****************MidiCombo****************/
        initMidiPort();
        availableMidiCombo = new JComboBox(availableMidiPort);
        availableMidiCombo.setBackground(BACKGROUND_COLOR);
        availableMidiCombo.setForeground(FOREGROUND_COLOR);
        bottomPanel.add(availableMidiCombo);

        availableMidiCombo.addActionListener(e -> new Thread(() -> {
            newMidiPort = (int) availableMidiCombo.getSelectedItem();
        }).start());

        /*****************AddSensor****************/
        JButton addSensorButton = new JButton("Ajouter un capteur");
        addSensorButton.setBackground(BUTTON_COLOR);
        addSensorButton.setForeground(FOREGROUND_COLOR);
        addSensorButton.setBorder(ETCHED_BORDER);
        addSensorButton.setPreferredSize(new Dimension(150, 25));
        bottomPanel.add(addSensorButton);
        addSensorButton.addActionListener(e -> new Thread(() -> {

            if (newName != null && newArduChan != -1 && newMidiPort != -1) {
                //we create the sensor in the services
                SensorManagement.addSensor(newName, newArduChan, newMidiPort);
                //we create the matching sensorRow
                SensorRow sensorRow = new SensorRow(newName, newArduChan, newMidiPort);
                availableMidiPort.removeElement(newMidiPort);
                sensorRowList.add(sensorRow);
                DeleteButton db = new DeleteButton(sensorRow, centerPanel, OperatingWindows.this, sensorNumberLb);
                deleteButtonList.add(db);
                //constraints for the grid bag layout
                centerConstraint.gridy = centerConstraint.gridy + 1;
                centerConstraint.gridx = 0;
                centerConstraint.weightx = 1;
                newName = null;
                newArduChan = -1;
                newMidiPort = -1;


                SwingUtilities.invokeLater(() -> {
                    centerPanel.add(sensorRow, centerConstraint);
                    centerConstraint.gridx = 1;
                    centerConstraint.weightx = 0.5;
                    centerPanel.add(db, centerConstraint);
                    newSensorName.setText("");
                    int nb = Integer.parseInt(sensorNumberLb.getText());
                    sensorNumberLb.setText(String.valueOf(++nb));
                    availableMidiCombo.setSelectedIndex(0);
                    arduinoPort.setSelectedIndex(0);
                    resetMidiCombo();
                    repaint();
                    pack();
                });

            } else {
                String message;
                if (newName == null) {
                    message = "<html><center>Veuillez entrer un nom pour ce capteur " +
                            "</center></html>";
                } else if (newArduChan == -1) {
                    message = "<html><center>Veuillez sélectionner un canal arduino " +
                            "</center></html>";
                } else {
                    message = "<html><center>Veuillez sélectionner un port midi " +
                            "</center></html>";
                }

                JOptionPane.showMessageDialog(OperatingWindows.this,
                        message,
                        " Erreur ",
                        JOptionPane.ERROR_MESSAGE);
            }
        }).start());

        setContentPane(mainPanel);
        InputManager.init();
        built = true;

    }

    public static void resetMidiCombo() {
        availableMidiPort.sort(new sortVectors());
    }

    public static void main(String[] args) {
        JFrame frame = new OperatingWindows();
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void removeFromSensorList(int midiPort) {

        availableMidiPort.add(midiPort);    //on le remet dans les dispos
        for (SensorRow s : sensorRowList) {
            if (s.getMidiPort() == midiPort) {
                sensorRowList.remove(s);
                break;
            }
        }

    }

    private static void addVerticalSeparation(int width) {
        topConstraint.gridx = topConstraint.gridx + 1;
        topConstraint.gridheight = 3;
        topPanel.add(Box.createHorizontalStrut(width), topConstraint);
        topConstraint.gridheight = 1;
    }

    public static void removeFromDBList(DeleteButton db) {
        deleteButtonList.remove(db);
    }

    public static void refreshInterface(String dataIn) {
        if (built) {
            String[] splitted = dataIn.split("-");
            //every instruction is separated by a -
            for (int i = 0; i < splitted.length; i += 2) {
                try {
                    int sensorNumber = Integer.parseInt(splitted[i]);
                    if (sensorNumber == selectedSensor) {
                        selectedSensorVuMeter.setValue(Integer.parseInt(splitted[i + 1]));
                    }
                    for (SensorRow s : sensorRowList) {
                        if (s.getArduinoChannel() == sensorNumber) {
                            int input = Integer.parseInt(splitted[i + 1]);
                            s.setIncomingSignal(input); //Setting the in value
                            int output = SensorManagement.getOutputValue(s.getMidiPort());
                            s.setOutputValue(output);
                        }
                    }
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }

            }
        }
    }

    public static void refreshLogs(String logs) {
        SwingUtilities.invokeLater(() -> {
            logsArea.setText(logs);
        });

    }

    private void numberFormatWarning() {
        JOptionPane.showMessageDialog(OperatingWindows.this, "Veuillez entrer un nombre", "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    private void setMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(BACKGROUND_COLOR);

        JMenuItem saveItem = new JMenuItem("Enregistrer");
        saveItem.setEnabled(false);

        /**Fichier**/
        JMenu fileMenu = new JMenu("Fichier");
        menuBar.add(fileMenu);
        fileMenu.setBackground(BACKGROUND_COLOR);
        fileMenu.setForeground(FOREGROUND_COLOR);

        /***********/
        /**newItem**/
        /***********/
        JMenuItem newItem = new JMenuItem("Nouveau");
        fileMenu.add(newItem);
        newItem.setBackground(BACKGROUND_COLOR);
        newItem.setForeground(FOREGROUND_COLOR);

        newItem.addActionListener(e -> new Thread(() -> {
            SensorManagement.newSetup();
            saveFile = null;
            SwingUtilities.invokeLater(() -> {
                cleanAction();
                saveItem.setEnabled(false);
            });
        }).start());

        /************/
        /**openItem**/
        /************/
        JMenuItem openItem = new JMenuItem("Ouvrir");
        fileMenu.add(openItem);
        openItem.setBackground(BACKGROUND_COLOR);
        openItem.setForeground(FOREGROUND_COLOR);

        openItem.addActionListener(e -> {
            JFileChooser openChooser = new JFileChooser();
            openChooser.setAcceptAllFileFilterUsed(false);
            openChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return (f.getName().endsWith(SAVE_EXTENSION) || f.isDirectory());
                }

                @Override
                public String getDescription() {
                    return "Fichiers de sauvegarde";
                }
            });
            openChooser.setDialogTitle("Ouvrir un fichier");

            int userSelection = openChooser.showOpenDialog(OperatingWindows.this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                saveFile = openChooser.getSelectedFile();
                if (SensorManagement.loadSetup(saveFile)) {
                    saveItem.setEnabled(true);
                    OperatingWindows.this.loadSetup();
                } else {
                    JOptionPane.showMessageDialog(OperatingWindows.this,
                            "<html><center>Erreur lors de l'ouverture du fichier</center></html>",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    cleanAction();
                }
            }
        });


        /**************/
        /**SaveAsItem**/
        /**************/
        JMenuItem saveAsItem = new JMenuItem("Enregistrer sous");
        fileMenu.add(saveAsItem);
        saveAsItem.setBackground(BACKGROUND_COLOR);
        saveAsItem.setForeground(FOREGROUND_COLOR);

        saveAsItem.addActionListener(e -> {
            JFileChooser saveChooser = new JFileChooser();
            saveChooser.setDialogTitle("Sauvegarder cette configuration");
            saveChooser.setAcceptAllFileFilterUsed(false);
            saveChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return (f.getName().endsWith(SAVE_EXTENSION) || f.isDirectory());
                }

                @Override
                public String getDescription() {
                    return "Fichiers de sauvegarde";
                }
            });

            int userSelection = saveChooser.showSaveDialog(OperatingWindows.this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                saveFile = saveChooser.getSelectedFile();
                if (!saveFile.getName().endsWith(SAVE_EXTENSION)) {
                    saveFile = new File(saveFile + SAVE_EXTENSION);
                }
                saveItem.setEnabled(true);
                if (!SensorManagement.saveSetup(saveFile)) {
                    JOptionPane.showMessageDialog(OperatingWindows.this,
                            "<html><center>Erreur lors de l'enregistrement du fichier</center></html>",
                            "Erreur", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

        /************/
        /**SaveItem**/
        /************/
        /*Declaration on Top*/
        fileMenu.add(saveItem);
        saveItem.setBackground(BACKGROUND_COLOR);
        saveItem.setForeground(FOREGROUND_COLOR);

        saveItem.addActionListener(e -> SensorManagement.saveSetup(saveFile));

        /**************/
        /***QuitItem***/
        /**************/
        JMenuItem quitItem = new JMenuItem("Quitter");
        fileMenu.add(quitItem);
        quitItem.setBackground(BACKGROUND_COLOR);
        quitItem.setForeground(FOREGROUND_COLOR);

        quitItem.addActionListener(e -> new Thread(() -> {
            MidiManager.exit();
            arduinoInData.close();
            dispose();
            System.exit(0);
        }).start());


        /**Edition**/
        JMenu editMenu = new JMenu("Edition");
        menuBar.add(editMenu);
        editMenu.setBackground(BACKGROUND_COLOR);
        editMenu.setForeground(FOREGROUND_COLOR);


        /**MidiSetting Item**/
        JMenuItem midiSettingItem = new JMenuItem("Paramètres midi");
        editMenu.add(midiSettingItem);
        midiSettingItem.setBackground(BACKGROUND_COLOR);
        midiSettingItem.setForeground(FOREGROUND_COLOR);

        midiSettingItem.addActionListener(e -> new MidiDeviceChoice(false));

        //Aide
        JMenu helpMenu = new JMenu("Aide");
        menuBar.add(helpMenu);
        helpMenu.setBackground(BACKGROUND_COLOR);
        helpMenu.setForeground(FOREGROUND_COLOR);


        //getHelp Item
        JMenuItem getHelpItem = new JMenuItem("Obtenir de l'aide");
        helpMenu.add(getHelpItem);
        getHelpItem.setBackground(BACKGROUND_COLOR);
        getHelpItem.setForeground(FOREGROUND_COLOR);

        getHelpItem.addActionListener(e -> new HelpWindow());

    }

    private void initMidiPort() {
        for (int i = 0; i < 128; i++) {
            availableMidiPort.add(i);
        }
    }

    private void loadSetup() {
        new Thread(() -> {
            SwingUtilities.invokeLater(OperatingWindows.this::cleanAction);
            java.util.List<Sensor> sensorList = SensorManagement.getSensorList();
            for (Sensor s : sensorList) {
                SensorRow sr = new SensorRow(s);
                sensorRowList.add(sr);
                availableMidiPort.removeElement(s.getMidiPort());
                DeleteButton db = new DeleteButton(sr, centerPanel, OperatingWindows.this, sensorNumberLb);
                deleteButtonList.add(db);
                //constraints for the grid bag layout
                centerConstraint.gridy = centerConstraint.gridy + 1;
                centerConstraint.gridx = 0;
                centerConstraint.weightx = 1;
                SwingUtilities.invokeLater(() -> {
                    centerPanel.add(sr, centerConstraint);
                    centerConstraint.gridx = 1;
                    centerConstraint.weightx = 0.5;
                    centerPanel.add(db, centerConstraint);
                });
            }
            SwingUtilities.invokeLater(() -> {
                sensorNumberLb.setText(String.valueOf(sensorList.size()));
                repaint();
                pack();
            });
        }).start();

    }

    private void cleanAction() {
        sensorRowList.forEach(centerPanel::remove);
        deleteButtonList.forEach(centerPanel::remove);
        sensorNumberLb.setText("0");
        newSensorName.setText("");
        arduinoPort.setSelectedIndex(0);
        availableMidiCombo.setSelectedIndex(0);
        sensorRowList.clear();
        deleteButtonList.clear();
        centerConstraint.gridy = 1;
        repaint();
    }

}

    class sortVectors implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            if(o1<o2){
                return -1;
            }else if(o1>o2)
                return 1;
            return 0;
        }
    }
