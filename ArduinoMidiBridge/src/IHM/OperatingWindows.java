package IHM;

import Arduino.arduinoInData;
import Metier.MidiManager;
import Metier.SensorManagement;
import Sensor.Sensor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class OperatingWindows extends JFrame {
    private JButton muteAllButton;
    private static JPanel centerPanel;
    private static JMenuBar menuBar;


    private static Vector<Integer> availableMidiPort = new Vector<>();
    private static boolean isMutedAll;
    private static java.util.List<SensorRow> sensorRowList = new ArrayList<>();
    private static java.util.List<DeleteButton> deleteButtonList = new ArrayList<>();
    private JTextField newSensorName;
    private JComboBox arduinoPort;
    private static JComboBox availableMidiCombo;
    private String newName = null;
    private int newArduChan = -1;
    private int newMidiPort = -1;
    private GridBagConstraints centerConstraint;
    private File saveFile = null;
    private JLabel sensorNumberLb;


    private static final String SAVE_EXTENSION = ".xml";

    /*********************************************************************/
    /******************************COLORS*********************************/
    /*********************************************************************/
    public static final Color BACKGROUND_COLOR = new Color(62, 65, 67);
    public static final Color BUTTON_COLOR = new Color(59, 62, 64);
    public static final Color FOREGROUND_COLOR = new Color(191,201,239);
    public static final Color MUTE_COLOR = new Color(174, 36, 33);
    public static final Color SOLO_COLOR = new Color(169, 162, 0);
    public static final Color IMPULSE_COLOR = new Color(45, 121, 36);
    public static final Color NAME_COLOR = new Color(221, 101, 4);

    private void setMenuBar(){
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

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser openChooser = new JFileChooser();
                openChooser.setDialogTitle("Ouvrir un fichier");

                int userSelection = openChooser.showOpenDialog(OperatingWindows.this);

                if(userSelection == JFileChooser.APPROVE_OPTION){
                    saveFile = openChooser.getSelectedFile();
                    SensorManagement.loadSetup(saveFile);
                    saveItem.setEnabled(true);
                    OperatingWindows.this.loadSetup(saveFile);

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

        saveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser saveChooser = new JFileChooser();
                saveChooser.setDialogTitle("Sauvegarder cette configuration");
                saveChooser.addChoosableFileFilter(new FileNameExtensionFilter("Fichiers de sauvegarde", ".xml"));

                int userSelection = saveChooser.showSaveDialog(OperatingWindows.this);

                if(userSelection == JFileChooser.APPROVE_OPTION){
                    saveFile = saveChooser.getSelectedFile();
                    if(!saveFile.getName().endsWith(SAVE_EXTENSION)){
                        saveFile = new File(saveFile+SAVE_EXTENSION);
                    }
                    saveItem.setEnabled(true);
                    SensorManagement.saveSetup(saveFile);
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

        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SensorManagement.saveSetup(saveFile);
            }
        });

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


        //Edition
        JMenu editMenu = new JMenu("Edition");
        menuBar.add(editMenu);
        editMenu.setBackground(BACKGROUND_COLOR);
        editMenu.setForeground(FOREGROUND_COLOR);


        //MidiSetting Item
        JMenuItem midiSettingItem = new JMenuItem("Paramètres midi");
        editMenu.add(midiSettingItem);
        midiSettingItem.setBackground(BACKGROUND_COLOR);
        midiSettingItem.setForeground(FOREGROUND_COLOR);

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

    }

    private void initMidiPort(){
        for(int i = 0; i<128; i++){
            availableMidiPort.add(i);
        }
    }


    public OperatingWindows(){
        super("ArduinoBrigde");
        GridBagLayout mainLayout = new GridBagLayout();
        GridBagConstraints mainConstraint = new GridBagConstraints();
        JPanel mainPanel = new JPanel(mainLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);


        /******************************************/
        /**************Center Panel****************/
        /******************************************/
        mainConstraint.fill = GridBagConstraints.BOTH;
        mainConstraint.weighty = 50;
        mainConstraint.weightx = 1;
        mainConstraint.gridy = 0;
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
        centerConstraint.anchor = GridBagConstraints.NORTHWEST;
        JLabel activeSensors = new JLabel("Capteurs actifs : ");
        activeSensors.setBackground(BACKGROUND_COLOR);
        activeSensors.setForeground(FOREGROUND_COLOR);
        centerPanel.add(activeSensors, centerConstraint);
        mainPanel.add(centerPanelScroll, mainConstraint);


        /*********Sensor Number Label*******/
        centerConstraint.anchor = GridBagConstraints.NORTHEAST;
        centerConstraint.gridx=1;
        sensorNumberLb = new JLabel("0");
        sensorNumberLb.setBackground(BACKGROUND_COLOR);
        sensorNumberLb.setForeground(FOREGROUND_COLOR);
        centerPanel.add(sensorNumberLb, centerConstraint);

        /******************************************/
        /**************Bottom Panel****************/
        /******************************************/
        mainConstraint.weighty = 1;
        mainConstraint.gridy = 1;
        mainConstraint.fill = GridBagConstraints.HORIZONTAL;
        mainConstraint.anchor = GridBagConstraints.LAST_LINE_END;
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(bottomPanel, mainConstraint);

        /*****************Mute All*****************/
        isMutedAll = false;
        muteAllButton = new JButton(("Mute All"));
        muteAllButton.setBackground(BACKGROUND_COLOR);
        muteAllButton.setForeground(FOREGROUND_COLOR);
        bottomPanel.add(muteAllButton);

        muteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(() -> {
                    if(!isMutedAll){
                        SensorManagement.muteAll();
                        isMutedAll = true;
                        SwingUtilities.invokeLater(() -> muteAllButton.setBackground(MUTE_COLOR));
                    }
                    else{
                        SensorManagement.unMuteAll();
                        isMutedAll = false;
                        SwingUtilities.invokeLater(() -> muteAllButton.setBackground(BACKGROUND_COLOR));
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
        JLabel newChan = new JLabel("Arduino Chanel : ");
        newChan.setBackground(BACKGROUND_COLOR);
        newChan.setForeground(FOREGROUND_COLOR);
        bottomPanel.add(newChan);

        /*****************ArduinoCh*****************/
        arduinoPort = new JComboBox();
        for (int i = 0; i<16 ; i++){
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
        addSensorButton.setBackground(BACKGROUND_COLOR);
        addSensorButton.setForeground(FOREGROUND_COLOR);
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
                resetMidiCombo();

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
                    repaint();
                    pack();
                });

            }
            else {
                String message;
                if (newName == null) {
                    message = "<html><center>Veuillez entrer un nom pour ce capteur " +
                            "</center></html>";
                }
                else if (newArduChan == -1){
                    message = "<html><center>Veuillez sélectionner un canal arduino " +
                            "</center></html>";
                }
                else if (newMidiPort == -1){
                    message = "<html><center>Veuillez sélectionner un port midi " +
                            "</center></html>";
                }
                else{
                    message = "Ce que j'fais là moi, je sais pas...";
                }

                JOptionPane.showMessageDialog(OperatingWindows.this,
                        message,
                        " Erreur ",
                        JOptionPane.ERROR_MESSAGE);
            }
        }).start());



        setContentPane(mainPanel);
        pack();
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

    }

    private void loadSetup(File toLoad){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        OperatingWindows.this.cleanAction();
                        //TODO add a jProgress bar
                    }
                });
                java.util.List<Sensor> sensorList = SensorManagement.getSensorList();
                for (Sensor s : sensorList){
                    SensorRow sr = new SensorRow(s);
                    sensorRowList.add(sr);
                    availableMidiPort.removeElement(s.getMidiPort());
                    DeleteButton db = new DeleteButton(sr, centerPanel, OperatingWindows.this, sensorNumberLb);
                    deleteButtonList.add(db);
                    //constraints for the grid bag layout
                    centerConstraint.gridy = centerConstraint.gridy + 1;
                    centerConstraint.gridx = 0;
                    centerConstraint.weightx = 1;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            centerPanel.add(sr, centerConstraint);
                            centerConstraint.gridx = 1;
                            centerConstraint.weightx = 0.5;
                            centerPanel.add(db, centerConstraint);
                        }
                    });
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        sensorNumberLb.setText(String.valueOf(sensorList.size()));
                        repaint();
                        pack();
                    }
                });
            }
        }).start();

    }

    private void cleanAction(){
        sensorRowList.forEach(centerPanel::remove);
        deleteButtonList.forEach(centerPanel::remove);
        sensorNumberLb.setText("0");
        newSensorName.setText("");
        arduinoPort.setSelectedIndex(0);
        availableMidiCombo.setSelectedIndex(0);
        sensorRowList.clear();
        deleteButtonList.clear();
        centerConstraint.gridy = 0;
        repaint();
    }
    public static void resetMidiCombo() {
        availableMidiPort.sort(new sortVectors());
    }

    public static void main (String[] args){
        JFrame frame = new OperatingWindows();
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void removeFromSensorList(int midiPort){

        availableMidiPort.add(midiPort);    //on le remet dans les dispos
        for(SensorRow s : sensorRowList){
            if (s.getMidiPort() == midiPort){
                sensorRowList.remove(s);
                break;
            }
        }

    }

    public static void removeFromDBList(DeleteButton db){
        deleteButtonList.remove(db);
    }

    public static void refreshInterface(String dataIn){
        String[] splitted = dataIn.split("-");
        //every instruction is separated by a -
        for (int i = 0; i<splitted.length; i+=2 ) {
            int sensorNumber = Integer.parseInt(splitted[i]);
            for (SensorRow s : sensorRowList) {
                if (s.getArduinoChannel() == sensorNumber) {
                    int input = Integer.parseInt(splitted[i + 1]);
                    s.setIncomingSignal(input); //Setting the in value
                    int output = SensorManagement.getOutputValue(s.getMidiPort());
                    s.setOutputValue(output);
                }
            }
        }
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
