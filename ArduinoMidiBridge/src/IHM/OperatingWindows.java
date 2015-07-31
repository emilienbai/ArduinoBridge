package IHM;

import IHM.Addition.KeyChooser;
import IHM.Addition.NewMidiSensor;
import IHM.Addition.NewOscSensor;
import IHM.Row.MidiSensorRow;
import IHM.Row.OscSensorRow;
import IHM.Row.SensorRow;
import IHM.Row.VuMeter;
import IHM.Settings.ConnexionInfo;
import IHM.Settings.MidiDeviceChoice;
import IHM.Settings.OscSettings;
import IHM.Settings.ServerSettings;
import Metier.ArduinoInData;
import Metier.InputManager;
import Metier.MidiManager;
import Metier.Services;
import Sensor.ArduinoChan;
import Sensor.MidiSensor;
import Sensor.OSCSensor;
import Sensor.Sensor;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class OperatingWindows extends JFrame {
    /*********************************************************************/
    /******************************COLORS*********************************/
    /*********************************************************************/
    public static final Color BACKGROUND_COLOR = new Color(31, 31, 38);
    public static final Color BUTTON_COLOR = new Color(25, 25, 30);
    public static final Color FOREGROUND_COLOR = new Color(92, 141, 246);
    public static final Color MUTE_COLOR = new Color(174, 36, 33);
    public static final Color SOLO_COLOR = new Color(169, 162, 0);
    public static final Color IMPULSE_COLOR = new Color(45, 121, 36);
    public static final Color NAME_COLOR = new Color(237, 99, 0);
    public static final Color DISABLED_COLOR = new Color(41, 23, 59);
    public static final Color TOGGLE_COLOR = new Color(122, 66, 132);
    public static final Color FADER_COLOR = new Color(42, 55, 167);
    public static final Color MOMENTARY_COLOR = new Color(23, 122, 32);
    public static final Color ALTERNATE_COLOR = new Color(116, 61, 33);

    /*****************************
     * BORDERS
     *********************************/
    public static final Border RAISED_BORDER = BorderFactory.createBevelBorder(BevelBorder.RAISED);
    public static final Border LOWERED_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    public static final Border ETCHED_BORDER = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    /***********/
    private static final String SAVE_EXTENSION = ".xml";
    private static final int DEFAULT_THRESHOLD = 100;
    private static final int MIN_THRESHOLD = 1;
    private static final int MIN_DEBOUNCE = 1;
    private static final int DEFAULT_DEBOUNCE = 200;
    private static final int MIDI_INDEX = 0;
    private static final int OSC_INDEX = 1;

    /*************************
     * Attributes
     *********************************/
    private static JPanel centerMidiPanel;
    private static JPanel centerOscPanel;
    private static JMenuBar menuBar;
    private static Vector<Integer> availableMidiPort = new Vector<>();
    private static boolean isMutedAll;
    private static java.util.List<MidiSensorRow> sensorRowList = new ArrayList<>();
    private static java.util.List<OscSensorRow> oscSensorRowList = new ArrayList<>();
    private static VuMeter selectedSensorVuMeter;
    private static JTextArea logsArea;
    private static int selectedSensor = 0;
    private static boolean built = false;
    private static boolean shortcutEnable = true;
    private static JMenuItem clientSettingItem;
    private static GridBagConstraints centerMidiConstraint;
    private static GridBagConstraints centerOscConstraint;
    private static JTabbedPane centerTabbedPanel;
    private JTextField debounceOneText;
    private JTextField thresholdOneTextArea;
    private JLabel sensorStatus;
    private JButton muteAllButton;
    private JComboBox<Integer> sensorNumberCb;
    private ServerSettings serverSettings = null;
    private boolean isServer;
    private ConnexionInfo connexionInfo = null;
    private File saveFile = null;

    /**
     * Main Windows of the application
     */
    public OperatingWindows(boolean isServer) {
        super("ArduinoBrigde");
        this.isServer = isServer;
        //setPreferredSize(new Dimension(800, 400));
        setMinimumSize(new Dimension(200, 100));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Services.closeApplication();
            }
        });
        this.setIconImage(new ImageIcon("logo.png").getImage());
        this.setMenuBar();
        this.setJMenuBar(menuBar);

        initMidiPort();
        setVisible(true);
        GridBagLayout mainLayout = new GridBagLayout();
        GridBagConstraints mainConstraint = new GridBagConstraints();
        JPanel mainPanel = new JPanel(mainLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        /******************************************/
        /****************Top Panel*****************/
        /******************************************/
        mainConstraint.fill = GridBagConstraints.BOTH;
        mainConstraint.weighty = 8;
        mainConstraint.weightx = 1;
        mainConstraint.gridy = mainConstraint.gridy + 1;
        mainConstraint.gridx = 0;

        GridBagLayout topLayout = new GridBagLayout();
        GridBagConstraints topConstraint = new GridBagConstraints();
        topConstraint.fill = GridBagConstraints.HORIZONTAL;
        topConstraint.insets = new Insets(3, 5, 3, 5);

        JPanel topPanel = new JPanel(topLayout);
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(LOWERED_BORDER);
        topPanel.add(new JSeparator(JSeparator.VERTICAL));
        if (this.isServer) {
            mainPanel.add(topPanel, mainConstraint);
        }


        /***********Selected sensor Vu Meter*******/
        selectedSensorVuMeter = new VuMeter(SwingConstants.VERTICAL, 0, 1024);
        selectedSensorVuMeter.setPreferredSize(new Dimension(15, 120));
        selectedSensorVuMeter.setMinimumSize(new Dimension(15, 80));
        selectedSensorVuMeter.setBackground(BACKGROUND_COLOR);
        topConstraint.gridx = 0;
        topConstraint.weighty = 1;
        topConstraint.weightx = 0;
        topConstraint.gridy = 0;
        ++topConstraint.gridx;
        topConstraint.gridheight = 3;
        topPanel.add(selectedSensorVuMeter, topConstraint);

        /********1st Column Sensor Choice**********/
        JLabel sensorNumber = new JLabel("<html><center>Numéro de<br>Capteur</html></center>");
        sensorNumber.setBackground(BACKGROUND_COLOR);
        sensorNumber.setForeground(FOREGROUND_COLOR);
        sensorNumber.setHorizontalAlignment(JLabel.CENTER);
        topConstraint.gridx = topConstraint.gridx + 1;
        topConstraint.gridheight = 1;
        topConstraint.weightx = 1;
        topPanel.add(sensorNumber, topConstraint);

        /*******1st Column SensorCombo***********/
        JComboBox<Integer> sensorCombo = new JComboBox<>();
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
        debounceOneText = new JTextField(String.valueOf(DEFAULT_DEBOUNCE));
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
                if (newDebounce >= MIN_DEBOUNCE) {
                    Services.setDebounceOne(selectedSensor, newDebounce);
                } else {
                    Services.setDebounceOne(selectedSensor, MIN_DEBOUNCE);
                    SwingUtilities.invokeLater(() -> debounceOneLabel.setText(String.valueOf(MIN_DEBOUNCE)));
                }
            } catch (NumberFormatException e1) {
                numberFormatWarning();
            }
        }).start());

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
        thresholdOneTextArea = new JTextField(String.valueOf(DEFAULT_THRESHOLD));
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
                if (newThreshold >= MIN_THRESHOLD) {
                    Services.setThresholdOne(selectedSensor, newThreshold);
                } else {
                    Services.setThresholdOne(selectedSensor, MIN_THRESHOLD);
                    SwingUtilities.invokeLater(() -> thresholdOneLb.setText(String.valueOf(MIN_THRESHOLD)));
                }
            } catch (NumberFormatException e1) {
                numberFormatWarning();
            }
        }).start());

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
        topConstraint.weightx = 0;

        calibrateOne.addActionListener(e -> new Thread(() -> {
            Services.calibrate(selectedSensor);
        }).start());

        /*******5th Column, Separator**************/
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 130));
        topConstraint.gridx++;
        topConstraint.gridy = 0;
        topConstraint.gridheight = 3;
        topPanel.add(sep, topConstraint);
        topConstraint.gridheight = 1;
        topConstraint.weightx = 1;

        /*******6th Column, DebounceAllLabel*******/
        JLabel debounceAllLb = new JLabel("<html><center>Stabilisation<br>général (ms)</center></html>");
        debounceAllLb.setBackground(BACKGROUND_COLOR);
        debounceAllLb.setForeground(FOREGROUND_COLOR);
        debounceAllLb.setHorizontalAlignment(SwingConstants.CENTER);
        ++topConstraint.gridx;
        topPanel.add(debounceAllLb, topConstraint);

        /******6th Column, DebounceAllTextArea*****/
        JTextField debounceAllTextArea = new JTextField(String.valueOf(DEFAULT_DEBOUNCE));
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
                if (newDebounce >= MIN_DEBOUNCE) {
                    Services.setDebounceAll(newDebounce);
                } else {
                    Services.setDebounceAll(MIN_DEBOUNCE);
                    SwingUtilities.invokeLater(() -> debounceAllLb.setText(String.valueOf(MIN_DEBOUNCE)));
                }
                SwingUtilities.invokeLater(() -> debounceOneText.setText(String.valueOf(newDebounce)));
            } catch (NumberFormatException e1) {
                numberFormatWarning();
            }
        }).start());

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
        JTextField thresholdAllTextArea = new JTextField(String.valueOf(DEFAULT_THRESHOLD));
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
                if (newThreshold >= MIN_THRESHOLD) {
                    Services.setThresholdAll(newThreshold);
                } else {
                    Services.setThresholdAll(MIN_THRESHOLD);
                    SwingUtilities.invokeLater(() -> thresholdAllLb.setText(String.valueOf(MIN_THRESHOLD)));
                }
                SwingUtilities.invokeLater(() -> thresholdOneTextArea.setText(String.valueOf(newThreshold)));
            } catch (NumberFormatException e1) {
                numberFormatWarning();
            }
        }).start());

        /*******8th Column SensorNumberLb*********/
        JLabel sensorNumberLabel = new JLabel("<html><center>Nombre de<br>Capteurs</center></html>");
        sensorNumberLabel.setBackground(BACKGROUND_COLOR);
        sensorNumberLabel.setForeground(FOREGROUND_COLOR);
        sensorNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ++topConstraint.gridx;
        topConstraint.gridy = 0;
        topConstraint.weightx = 1;
        topPanel.add(sensorNumberLabel, topConstraint);

        /*******8th Column SensorNumberCb**********/
        sensorNumberCb = new JComboBox<>();
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

        calibrateAllButton.addActionListener(e -> new Thread(Services::calibrateAll).start());

        /*******9th Column CalTime Label************/
        JLabel calibrationTimeLabel = new JLabel("<html><center>Temps de<br>Calibration (s)</center></html>");
        calibrationTimeLabel.setBackground(BACKGROUND_COLOR);
        calibrationTimeLabel.setForeground(FOREGROUND_COLOR);
        calibrationTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topConstraint.weightx = 1;
        ++topConstraint.gridx;
        topConstraint.gridy = 0;
        topPanel.add(calibrationTimeLabel, topConstraint);

        /*****9th Column, calTimeTextArea**********/
        JTextField calibrationTimeTextArea = new JTextField("1");
        calibrationTimeTextArea.setBackground(BACKGROUND_COLOR);
        calibrationTimeTextArea.setForeground(FOREGROUND_COLOR);
        calibrationTimeTextArea.setBorder(LOWERED_BORDER);
        topConstraint.gridy = 1;
        topPanel.add(calibrationTimeTextArea, topConstraint);

        /*****9th Column, calTimeValidation*********/
        JButton calTimeOK = new JButton("OK");
        calTimeOK.setBackground(BUTTON_COLOR);
        calTimeOK.setForeground(FOREGROUND_COLOR);
        calTimeOK.setBorder(RAISED_BORDER);
        calTimeOK.setPreferredSize(new Dimension(30, 30));
        topConstraint.gridy = 2;
        topPanel.add(calTimeOK, topConstraint);

        calTimeOK.addActionListener(e -> new Thread(() -> {
            try {
                int newTime = Integer.parseInt(calibrationTimeTextArea.getText());
                Services.setCalibrationTime(newTime);
            } catch (NumberFormatException e1) {
                e1.printStackTrace();
            }
        }).start());

        /********10th Column, LogArea***************/
        logsArea = new JTextArea(10, 20);
        logsArea.setBackground(Color.BLACK);
        logsArea.setForeground(Color.WHITE);
        logsArea.setEditable(false);
        JScrollPane scrollLogs = new JScrollPane(logsArea);
        scrollLogs.setMinimumSize(new Dimension(180, 120));
        topConstraint.weightx = 1;
        topConstraint.weighty = 1;
        topConstraint.gridy = 0;
        ++topConstraint.gridx;
        topConstraint.gridheight = 2;
        topConstraint.gridwidth = 3;
        topPanel.add(scrollLogs, topConstraint);

        /*******10th Column, reset button***********/
        JButton resetButton = new JButton("Reset Arduino");
        resetButton.setBackground(BUTTON_COLOR);
        resetButton.setForeground(FOREGROUND_COLOR);
        resetButton.setBorder(RAISED_BORDER);
        resetButton.setPreferredSize(new Dimension(30, 30));
        topConstraint.gridy = 2;
        topPanel.add(resetButton, topConstraint);

        resetButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(OperatingWindows.this, "<html><center>Êtes vous sur de vouloir " +
                            "réinitialiser l'arduino?" +
                            "<br>Cette action peut prendre du temps pendant lequel aucun message ne sera transmis",
                    "Question", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                new Thread(Services::resetArduino).start();
            }
        });

        /******************************************/
        /**************Center Panel****************/
        /******************************************/
        centerTabbedPanel = new JTabbedPane();
        centerTabbedPanel.setBackground(BACKGROUND_COLOR);
        centerTabbedPanel.setForeground(NAME_COLOR);

        mainConstraint.fill = GridBagConstraints.BOTH;
        mainConstraint.weighty = 50;
        mainConstraint.weightx = 1;
        mainConstraint.gridy = mainConstraint.gridy + 1;
        mainConstraint.gridx = 0;

        /**MIDI Panel**/
        centerMidiConstraint = new GridBagConstraints();
        centerMidiConstraint.fill = GridBagConstraints.HORIZONTAL;
        centerMidiConstraint.weightx = 1;
        centerMidiConstraint.gridx = 0;
        centerMidiConstraint.gridy = 0;
        centerMidiPanel = new JPanel(new GridBagLayout());
        centerMidiPanel.setBackground(BACKGROUND_COLOR);
        centerMidiPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        /**ScrollPane Midi**/
        JScrollPane centerMidiPanelScroll = new JScrollPane(centerMidiPanel);
        centerMidiPanelScroll.setBackground(BACKGROUND_COLOR);
        centerMidiPanelScroll.setForeground(FOREGROUND_COLOR);

        /**OSC Panel**/
        centerOscConstraint = new GridBagConstraints();
        centerOscConstraint.fill = GridBagConstraints.HORIZONTAL;
        centerOscConstraint.weightx = 1;
        centerOscConstraint.gridx = 0;
        centerMidiConstraint.gridy = 0;
        centerOscPanel = new JPanel(new GridBagLayout());
        centerOscPanel.setBackground(BACKGROUND_COLOR);
        centerOscPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        /**ScrollPane OSC**/
        JScrollPane centerOscPanelScroll = new JScrollPane(centerOscPanel);
        centerOscPanelScroll.setBackground(BACKGROUND_COLOR);
        centerOscPanelScroll.setForeground(FOREGROUND_COLOR);

        /**Tabbed Pane**/
        centerTabbedPanel.add(centerMidiPanelScroll, MIDI_INDEX);
        centerTabbedPanel.setTitleAt(MIDI_INDEX, "Midi");
        centerTabbedPanel.add(centerOscPanelScroll, OSC_INDEX);
        centerTabbedPanel.setTitleAt(OSC_INDEX, "Osc");
        centerTabbedPanel.setEnabledAt(OSC_INDEX, false);

        mainPanel.add(centerTabbedPanel, mainConstraint);

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

        /*****************Disable Shortcut*********/
        JButton disableShortcut = new JButton("<html><center>Désactiver les<br>raccourcis clavier</center></html>");
        disableShortcut.setBackground(BUTTON_COLOR);
        disableShortcut.setForeground(FOREGROUND_COLOR);
        disableShortcut.setBorder(RAISED_BORDER);
        disableShortcut.setPreferredSize(new Dimension(160, 40));
        bottomPanel.add(disableShortcut);

        disableShortcut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (shortcutEnable) {
                    shortcutEnable = false;
                    SwingUtilities.invokeLater(() -> {
                        disableShortcut.setBorder(LOWERED_BORDER);
                        disableShortcut.setBackground(DISABLED_COLOR);
                    });
                } else {
                    shortcutEnable = true;
                    SwingUtilities.invokeLater(() -> {
                        disableShortcut.setBorder((RAISED_BORDER));
                        disableShortcut.setBackground(BUTTON_COLOR);
                    });
                }
            }
        });

        /*****************Mute All*****************/
        isMutedAll = false;
        muteAllButton = new JButton(("Mute All"));
        muteAllButton.setBackground(BUTTON_COLOR);
        muteAllButton.setForeground(FOREGROUND_COLOR);
        muteAllButton.setBorder(RAISED_BORDER);
        muteAllButton.setPreferredSize(new Dimension(90, 40));
        bottomPanel.add(muteAllButton);

        muteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    if (!isMutedAll) {
                        Services.muteAll();
                        isMutedAll = true;
                        SwingUtilities.invokeLater(() -> muteAllButton.setBackground(MUTE_COLOR));
                    } else {
                        Services.unMuteAll();
                        isMutedAll = false;
                        SwingUtilities.invokeLater(() -> muteAllButton.setBackground(BUTTON_COLOR));
                    }
                }).start();
            }
        });

        /*****************AddSensor****************/
        JButton addSensorButton = new JButton("Ajouter un capteur");
        addSensorButton.setBackground(BUTTON_COLOR);
        addSensorButton.setForeground(FOREGROUND_COLOR);
        addSensorButton.setBorder(RAISED_BORDER);
        addSensorButton.setPreferredSize(new Dimension(150, 40));
        bottomPanel.add(addSensorButton);

        addSensorButton.addActionListener(e -> new Thread(() -> {
            switch (centerTabbedPanel.getSelectedIndex()) {
                case MIDI_INDEX:
                    if (!NewMidiSensor.isOpen()) {
                        new NewMidiSensor(availableMidiPort, this);
                    }
                    break;
                case OSC_INDEX:
                    if (!NewOscSensor.isOpen()) {
                        new NewOscSensor(this);
                    }
                    break;
            }
        }).start());

        /**Listen to the keyboard for shortcut**/
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());

        setContentPane(mainPanel);
        InputManager.init();
        built = true;
    }

    public static void addMidiSensor(String newName, int newArduChan, int newMidiPort) {
        //Check if every needed information is here
        KeyChooser keyChooser = new KeyChooser();
        while (keyChooser.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        KeyEvent k = keyChooser.getKeyEvent();
        if (k != null) {
            char shortcut = k.getKeyChar();
            boolean used = false;
            for (MidiSensorRow s : sensorRowList) {
                if (s.getShortcut() == shortcut) {
                    used = true;
                }
            }
            if (!used) {
                //we create the sensor in the services
                Services.addMidiSensor(newName, newArduChan, newMidiPort, shortcut);
                //we create the matching sensorRow
                MidiSensorRow sensorRow = new MidiSensorRow(newName, newArduChan, newMidiPort, shortcut);
                availableMidiPort.removeElement(newMidiPort);
                sensorRowList.add(sensorRow);

                //constraints for the grid bag layout
                centerMidiConstraint.gridy = centerMidiConstraint.gridy + 1;


                SwingUtilities.invokeLater(() -> centerMidiPanel.add(sensorRow, centerMidiConstraint));
            } else {
                JOptionPane.showMessageDialog(null, "<html><center>Raccourci déjà" +
                                " utilisé</center></html>", "Avertissement",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public static void addOscSensor(String name, int arduIn, String address, String addressBis, int mode) {
        OscSensorRow oscSensorRow;
        if (mode == Sensor.ALTERNATE) {
            oscSensorRow = new OscSensorRow(name, arduIn, address, addressBis, mode);
            Services.addOscSensor(name, arduIn, address, addressBis, mode);
            oscSensorRowList.add(oscSensorRow);
        } else {
            oscSensorRow = new OscSensorRow(name, arduIn, address, mode);
            Services.addOscSensor(name, arduIn, address, mode);
            oscSensorRowList.add(oscSensorRow);
        }
        centerOscConstraint.gridy++;
        centerMidiConstraint.gridx = 0;

        SwingUtilities.invokeLater(() -> centerOscPanel.add(oscSensorRow, centerOscConstraint));
    }

    /**
     * Remove a Sensor Row from the list
     *
     * @param midiSensorRow the midiport of this sensorRow
     */
    public static void removeFromMidiSensorList(SensorRow midiSensorRow) {
        availableMidiPort.add((Integer) midiSensorRow.getKey());    //on le remet dans les dispos
        sensorRowList.remove(midiSensorRow);
        Services.deleteMidiSensor((Integer) midiSensorRow.getKey());
        SwingUtilities.invokeLater(() -> {
            centerMidiPanel.remove(midiSensorRow);
            centerMidiPanel.repaint();
        });
    }

    public static void removeFromOscSensorList(SensorRow toDelete) {
        Services.deleteOscSensor((String) toDelete.getKey());
        oscSensorRowList.remove(toDelete);
        SwingUtilities.invokeLater(() -> {
            centerOscPanel.remove(toDelete);
            centerOscPanel.repaint();
        });
    }

    /**
     * Send an impulsion on the midiPort of a MidiSensorRow
     *
     * @param s The sensorRow to modify
     */
    public static void impulseShortCut(MidiSensorRow s) {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> s.setImpulseColor(IMPULSE_COLOR));
            Services.sendMidiImpulsion(s.getMidiPort());
            SwingUtilities.invokeLater(() -> s.setImpulseColor(BUTTON_COLOR));
        }).start();
    }

    /**
     * Update every Vu-Meter on the interface
     *
     * @param dataIn data sent by the arduino board
     */
    public static void refreshInterface(String dataIn) {
        if (built) { //begin only if the Frame is built
            SwingUtilities.invokeLater(() -> {
                String[] splitted = dataIn.split("-");
                //every instruction is separated by a -
                for (int i = 0; i < splitted.length; i += 2) {
                    try {
                        int sensorNumber = Integer.parseInt(splitted[i]);
                        if (sensorNumber == selectedSensor) {
                            selectedSensorVuMeter.setValue(Integer.parseInt(splitted[i + 1]));
                        }
                        if (centerTabbedPanel.getSelectedIndex() == MIDI_INDEX) {
                            for (MidiSensorRow s : sensorRowList) {
                                if (s.getArduinoChannel() == sensorNumber) {
                                    int input = Integer.parseInt(splitted[i + 1]);
                                    s.setIncomingSignal(input); //Setting the in value
                                    int output = Services.getMidiOutputValue(s.getMidiPort());
                                    s.setOutputValue(output);
                                }
                            }
                        } else if (centerTabbedPanel.getSelectedIndex() == OSC_INDEX) {
                            for (OscSensorRow s : oscSensorRowList) {
                                if (s.getArduinoChannel() == sensorNumber) {
                                    int input = Integer.parseInt(splitted[i + 1]);
                                    s.setIncomingSignal(input);
                                    float output = Services.getoscOutputValue(s.getAddress());
                                    s.setOutputValue(output);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        //e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Show logs from the arduino board
     *
     * @param logs The String to display
     */
    public static void refreshLogs(String logs) {
        SwingUtilities.invokeLater(() -> {
            logsArea.append(logs);
            logsArea.repaint();
        });
    }

    /**
     * getter for shortcutEnable
     *
     * @return true if shortcuts are activated
     */
    public static boolean isShortcutEnable() {
        return shortcutEnable;
    }

    /**
     * Used if the connection with the server is lost
     */
    public static void signalDisconnection() {
        JOptionPane.showMessageDialog(centerMidiPanel, "La connexion au serveur a été perdu, pour se reconnecter :" +
                " Edition -> Paramètres Client", "Attention", JOptionPane.ERROR_MESSAGE);
        clientSettingItem.setEnabled(true);
    }

    /**
     * Enable or disable the OSC tab
     *
     * @param status status of the OSC tab
     */
    public static void setOscStatus(boolean status) {
        centerTabbedPanel.setEnabledAt(OSC_INDEX, status);
        //centerTabbedPanel.setSelectedIndex(MIDI_INDEX);
    }

    public static void main(String[] args) {
        JFrame frame = new OperatingWindows(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /**
     * Display a warning about the content
     */
    private void numberFormatWarning() {
        JOptionPane.showMessageDialog(OperatingWindows.this, "Veuillez entrer un nombre",
                "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Create the menuBar of the window
     */
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
            Services.newSetup();
            initMidiPort();
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
                if (Services.loadSetup(saveFile)) {
                    initMidiPort();
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
                if (!Services.saveSetup(saveFile)) {
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

        saveItem.addActionListener(e -> Services.saveSetup(saveFile));

        /**************/
        /***QuitItem***/
        /**************/
        JMenuItem quitItem = new JMenuItem("Quitter");
        fileMenu.add(quitItem);
        quitItem.setBackground(BACKGROUND_COLOR);
        quitItem.setForeground(FOREGROUND_COLOR);

        quitItem.addActionListener(e -> new Thread(() -> {
            MidiManager.exit();
            ArduinoInData.close();
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

        /**OscSettings Item**/
        JMenuItem oscSettingItem = new JMenuItem("Paramètres OSC");
        editMenu.add(oscSettingItem);
        oscSettingItem.setBackground(BACKGROUND_COLOR);
        oscSettingItem.setForeground(FOREGROUND_COLOR);

        oscSettingItem.addActionListener(e -> {
            if (!OscSettings.isOpen()) {
                new OscSettings(Services.getOscStatus());
            }
        });

        /**ServerSetting Item**/
        JMenuItem serverSettingItem = new JMenuItem("Paramètres serveur");
        if (this.isServer) {
            editMenu.add(serverSettingItem);
        }
        serverSettingItem.setBackground(BACKGROUND_COLOR);
        serverSettingItem.setForeground(FOREGROUND_COLOR);

        serverSettingItem.addActionListener(e -> {
            if (serverSettings != null) {
                serverSettings.setVisible(true);
            } else {
                serverSettings = new ServerSettings();
            }
        });

        /**ClientSetting Item**/
        clientSettingItem = new JMenuItem("Paramètres client");
        if (!this.isServer) {
            editMenu.add(clientSettingItem);
            clientSettingItem.setEnabled(false);
        }
        clientSettingItem.setBackground(BACKGROUND_COLOR);
        clientSettingItem.setForeground(FOREGROUND_COLOR);

        clientSettingItem.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            if (connexionInfo == null) {
                connexionInfo = new ConnexionInfo();
            } else {
                connexionInfo.setVisible(true);
            }
        }));

        /**Aide**/
        JMenu helpMenu = new JMenu("Aide");
        menuBar.add(helpMenu);
        helpMenu.setBackground(BACKGROUND_COLOR);
        helpMenu.setForeground(FOREGROUND_COLOR);

        /**getHelp Item**/
        JMenuItem getHelpItem = new JMenuItem("Obtenir de l'aide");
        helpMenu.add(getHelpItem);
        getHelpItem.setBackground(BACKGROUND_COLOR);
        getHelpItem.setForeground(FOREGROUND_COLOR);

        getHelpItem.addActionListener(e -> new HelpWindow());

    }

    /**
     * initialize the midiPort Vector
     */
    private void initMidiPort() {
        availableMidiPort.clear();
        for (int i = 0; i < 128; i++) {
            availableMidiPort.add(i);
        }
    }

    /**
     * Load a setup in the GUI
     */
    private void loadSetup() {
        new Thread(() -> {
            SwingUtilities.invokeLater(OperatingWindows.this::cleanAction);
            java.util.Hashtable<Integer, MidiSensor> sensorList = Services.getMidiTable();
            Hashtable<String, OSCSensor> oscSensorHashtable = Services.getOSCTable();
            Vector<ArduinoChan> arduinoChanVector = Services.getArduinoChanVector();
            MidiSensor ms;
            for (Map.Entry<Integer, MidiSensor> e : sensorList.entrySet()) {
                ms = e.getValue();
                MidiSensorRow sr = new MidiSensorRow(ms);
                sensorRowList.add(sr);
                availableMidiPort.removeElement(ms.getMidiPort());
                System.out.println("Removing : " + ms.getMidiPort());

                SwingUtilities.invokeLater(() ->{
                    //constraints for the grid bag layout
                    centerMidiConstraint.gridy ++;
                    centerMidiPanel.add(sr, centerMidiConstraint);
                });
            }

            OSCSensor os;
            for (Map.Entry<String, OSCSensor> e : oscSensorHashtable.entrySet()) {
                os = e.getValue();
                OscSensorRow sr = new OscSensorRow(os);
                oscSensorRowList.add(sr);
                SwingUtilities.invokeLater(() -> {
                    centerOscConstraint.gridy++;
                    centerOscPanel.add(sr, centerOscConstraint);
                });
            }

            String debounce = String.valueOf(arduinoChanVector.get(selectedSensor).getDebounce());
            String threshold = String.valueOf(arduinoChanVector.get(selectedSensor).getThreshold());
            int activeNumber = Services.getActiveNumber();

            SwingUtilities.invokeLater(() -> {
                debounceOneText.setText(debounce);
                thresholdOneTextArea.setText(threshold);
                sensorNumberCb.setSelectedIndex(activeNumber - 1);
                repaint();
                this.cleanPack();
            });
        }).start();
    }

    /**
     * Clean the GUI for a new session
     */
    private void cleanAction() {
        sensorRowList.forEach(centerMidiPanel::remove);
        sensorRowList.clear();
        centerMidiConstraint.gridy = 0;

        oscSensorRowList.forEach(centerOscPanel::remove);
        oscSensorRowList.clear();
        centerOscConstraint.gridy = 0;

        initMidiPort();

        repaint();
        cleanPack();
    }

    public void cleanPack(){
        Dimension tempSize = this.getSize();
        Point tempLocation = this.getLocation();
        this.pack();
        this.setSize(tempSize);
        this.setLocation(tempLocation);
    }

    /**
     * Keyboard Listener
     */
    private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (OperatingWindows.isShortcutEnable() && !NewMidiSensor.isOpen() && !NewOscSensor.isOpen()) {
                if (e.getID() == KeyEvent.KEY_TYPED) {
                    char charTyped = e.getKeyChar();
                    OperatingWindows.sensorRowList.stream().filter(s -> charTyped == s.getShortcut()).forEach(OperatingWindows::impulseShortCut);
                }
                return false;
            }
            return false;
        }
    }
}
