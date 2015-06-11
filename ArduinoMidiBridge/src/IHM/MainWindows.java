package IHM;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import static java.awt.Frame.MAXIMIZED_BOTH;
import static java.lang.System.exit;


/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class MainWindows extends JFrame{
    private JPanel mainPanel;
    private JLabel arduinoChan;
    private JLabel midiPort;
    private JLabel incomingSignal;
    private JLabel preamplifier;
    private JLabel preampliPerCent;
    private JLabel minRange;
    private JLabel maxRange;
    private JLabel outputValue;
    private JLabel nameLabel;
    private JLabel muteLabel;
    private JLabel soloLabel;
    private JLabel impulseLabel;
    private JLabel removeLabel;
    private JButton addSensorButton;
    private JButton muteAllButton;
    private static JMenuBar menuBar;
    private static Color backgroundColor = new Color(70,73,75);
    private static Color textColor = new Color(191,201,239);

    private static void setMenuBar(){
        menuBar = new JMenuBar();
        menuBar.setBackground(backgroundColor);

        //Fichier
        JMenu fileMenu = new JMenu("Fichier");
        menuBar.add(fileMenu);
        fileMenu.setBackground(backgroundColor);
        fileMenu.setForeground(textColor);

        //newItem
        JMenuItem newItem = new JMenuItem("Nouveau");
        fileMenu.add(newItem);
        newItem.setBackground(backgroundColor);
        newItem.setForeground(textColor);

        //openItem
        JMenuItem openItem = new JMenuItem("Ouvrir");
        fileMenu.add(openItem);
        openItem.setBackground(backgroundColor);
        openItem.setForeground(textColor);

        //SaveAsItem
        JMenuItem saveAsItem = new JMenuItem("Enregistrer sous");
        fileMenu.add(saveAsItem);
        saveAsItem.setBackground(backgroundColor);
        saveAsItem.setForeground(textColor);

        //SaveItem
        JMenuItem saveItem = new JMenuItem("Enregistrer");
        fileMenu.add(saveItem);
        saveItem.setBackground(backgroundColor);
        saveItem.setForeground(textColor);


        //Edition
        JMenu editMenu = new JMenu("Edition");
        menuBar.add(editMenu);
        editMenu.setBackground(backgroundColor);
        editMenu.setForeground(textColor);


        //MidiSetting Item
        JMenuItem midiSettingItem = new JMenuItem("Paramètres midi");
        editMenu.add(midiSettingItem);
        midiSettingItem.setBackground(backgroundColor);
        midiSettingItem.setForeground(textColor);

        //Aide
        JMenu helpMenu = new JMenu("Aide");
        menuBar.add(helpMenu);
        helpMenu.setBackground(backgroundColor);
        helpMenu.setForeground(textColor);


        //getHelp Item
        JMenuItem getHelpItem = new JMenuItem("Obtenir de l'aide");
        helpMenu.add(getHelpItem);
        getHelpItem.setBackground(backgroundColor);
        getHelpItem.setForeground(textColor);

    }

    public MainWindows(){
        super("ArduinoBrigde");

        setContentPane(mainPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);
        setMenuBar();
        this.setJMenuBar(menuBar);
        setVisible(true);



        addSensorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


    }


    /* TODO écrire la méthode dispose*/
    public void dispose(){
        exit(0);
    }
}
