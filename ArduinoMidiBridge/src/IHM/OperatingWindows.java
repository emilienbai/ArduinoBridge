package IHM;

import Arduino.arduinoInData;
import Metier.MidiManager;
import Metier.SensorManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class OperatingWindows extends JFrame {
    private JPanel mainPanel;
    private JButton addSensorButton;
    private JButton muteAllButton;
    private JPanel bottomPanel;
    private JScrollPane centerPanelScroll;
    private static JPanel centerPanel;
    private JLabel activeSensors;
    private static JLabel sensorNumberLb;
    private static JMenuBar menuBar;
    public static final Color BACKGROUND_COLOR = new Color(70,73,75);
    public static final Color FOREGROUND_COLOR = new Color(191,201,239);
    private static Vector<Integer> availableMidiPort = new Vector<Integer>();
    private static boolean addSensorOpen;
    private static java.util.List<SensorRow> sensorRowList = new ArrayList<SensorRow>();
    private JTextField newSensorName;
    private JComboBox arduinoPort;
    private static JComboBox availableMidiCombo;
    private String newName = null;
    private int newArduChan = -1;
    private int newMidiPort = -1;



    private static void setMenuBar(){
        menuBar = new JMenuBar();
        menuBar.setBackground(BACKGROUND_COLOR);

        //Fichier
        JMenu fileMenu = new JMenu("Fichier");
        menuBar.add(fileMenu);
        fileMenu.setBackground(BACKGROUND_COLOR);
        fileMenu.setForeground(FOREGROUND_COLOR);

        //newItem
        JMenuItem newItem = new JMenuItem("Nouveau");
        fileMenu.add(newItem);
        newItem.setBackground(BACKGROUND_COLOR);
        newItem.setForeground(FOREGROUND_COLOR);

        //openItem
        JMenuItem openItem = new JMenuItem("Ouvrir");
        fileMenu.add(openItem);
        openItem.setBackground(BACKGROUND_COLOR);
        openItem.setForeground(FOREGROUND_COLOR);

        //SaveAsItem
        JMenuItem saveAsItem = new JMenuItem("Enregistrer sous");
        fileMenu.add(saveAsItem);
        saveAsItem.setBackground(BACKGROUND_COLOR);
        saveAsItem.setForeground(FOREGROUND_COLOR);

        //SaveItem
        JMenuItem saveItem = new JMenuItem("Enregistrer");
        fileMenu.add(saveItem);
        saveItem.setBackground(BACKGROUND_COLOR);
        saveItem.setForeground(FOREGROUND_COLOR);

        JMenuItem quitItem = new JMenuItem("Quitter");
        fileMenu.add(quitItem);
        quitItem.setBackground(BACKGROUND_COLOR);
        quitItem.setForeground(FOREGROUND_COLOR);


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

    public static void setAddSensorOpen(boolean addSensorOpen) {
        OperatingWindows.addSensorOpen = addSensorOpen;
    }


    public OperatingWindows(){
        super("ArduinoBrigde");
        GridBagLayout mainLayout = new GridBagLayout();
        GridBagConstraints mainConstraint = new GridBagConstraints();
        mainPanel = new JPanel(mainLayout);
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
        GridBagConstraints centerConstraint = new GridBagConstraints();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setBackground(BACKGROUND_COLOR);

        centerPanelScroll = new JScrollPane(centerPanel);
        centerPanelScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerPanelScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanelScroll.setBackground(BACKGROUND_COLOR);
        centerPanelScroll.setForeground(FOREGROUND_COLOR);


        /*************Active sensor Label**********/
        centerConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
        centerConstraint.gridx = 0;
        centerConstraint.gridy = 0;
        centerConstraint.weightx = 1;
        activeSensors = new JLabel("Capteurs actifs : ");
        activeSensors.setBackground(BACKGROUND_COLOR);
        activeSensors.setForeground(FOREGROUND_COLOR);
        centerPanel.add(activeSensors, centerConstraint);
        mainPanel.add(centerPanelScroll, mainConstraint);


        /*********Sensor Number Label*******/
        centerConstraint.anchor = GridBagConstraints.EAST;
        centerConstraint.gridx=1;
        JLabel sensorNumberLb = new JLabel("0");
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
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(bottomPanel, mainConstraint);

        /*****************Mute All*****************/
        muteAllButton = new JButton(("Mute All"));
        muteAllButton.setBackground(BACKGROUND_COLOR);
        muteAllButton.setForeground(FOREGROUND_COLOR);
        bottomPanel.add(muteAllButton);

        //Todo la vraie méthode
        muteAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.add(new SensorRow("Does it Work ?", 2, 1));
                repaint();
                pack();
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newName = newSensorName.getText();
                    }
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

        availableMidiCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newMidiPort = (int) availableMidiCombo.getSelectedItem();
                        System.out.println("Port sélectionné : " + newMidiPort);
                    }
                }).start();
            }
        });

        /*****************AddSensor****************/
        addSensorButton = new JButton("Ajouter un capteur");
        addSensorButton.setBackground(BACKGROUND_COLOR);
        addSensorButton.setForeground(FOREGROUND_COLOR);
        bottomPanel.add(addSensorButton);

        addSensorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newArduChan = (int) arduinoPort.getSelectedItem();
                        //newMidiPort = (int) availableMidiCombo.getSelectedItem();
                        System.out.println("Canal Arduino : " +newArduChan+" Port Midi : "+newMidiPort);

                        if(newName != null && newArduChan != -1 && newMidiPort != -1){
                            SensorManagement.addSensor(newName, newArduChan, newMidiPort);
                            SensorRow sensorRow = new SensorRow(newName, newArduChan, newMidiPort);
                            availableMidiPort.removeElement(newMidiPort);
                            sensorRowList.add(sensorRow);
                            DeleteButton db = new DeleteButton(sensorRow, centerPanel, OperatingWindows.this, sensorNumberLb);
                            centerConstraint.gridy = centerConstraint.gridy + 1;
                            centerConstraint.gridx = 0;
                            centerConstraint.weightx = 10;
                            newName=null;
                            newArduChan = -1;
                            newMidiPort = -1;
                            resetMidiCombo();

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    centerPanel.add(sensorRow, centerConstraint);
                                    centerConstraint.gridx = 1;
                                    centerPanel.add(db, centerConstraint);
                                    newSensorName.setText("");
                                    int nb = Integer.parseInt(sensorNumberLb.getText());
                                    sensorNumberLb.setText(String.valueOf(++nb));
                                    repaint();
                                    pack();
                                }
                            });

                        }
                        else{
                            JOptionPane.showMessageDialog(OperatingWindows.this,
                                    "<html><center>Veuillez entrer un nom pour ce capteur " +
                                            "</center></html>",
                                    " Erreur ",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }).start();
            }

        });



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
            }
        });
        setMenuBar();
        this.setJMenuBar(menuBar);

        setVisible(true);

    }

    public static void resetMidiCombo(){
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
}

    class sortVectors implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            // TODO Auto-generated method stub
            if(o1<02){
                return -1;
            }else if(o1>o2)
                return 1;
            return 0;
        }
    }
