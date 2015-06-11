package IHM;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class addSensor extends JFrame{
    private JPanel newSensorPanel;
    private JLabel nameLabel;
    private JLabel arduinoChanLabel;
    private JTextField nameField;
    private JComboBox arduinoChanCombo;
    private JComboBox midiPortCombo;
    private JButton addButton;
    private JButton cancelButton;
    private String name = null;
    private int arduinoChan = -1;
    private int midiPort = -1;

    public addSensor(Vector availableMidiPort){
        super("Ajouter un capteur");

        setContentPane(newSensorPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        initArduCombo();
        initMidiCombo(availableMidiPort);


        /*Action Listeners*/
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainWindows.setAddSensorOpen(false);
            }
        });
        //TODO ajouter actionlistener au bouton ajouter.

        setVisible(true);


    }

    private void initArduCombo(){
        int[] channels = new int[16];
        for (int i = 0; i<16 ; i++){
            arduinoChanCombo.addItem(i);
        }
    }

    private void initMidiCombo(Vector availableMidiPort){
        for(Object i : availableMidiPort){
            midiPortCombo.addItem(i);
        }
    }
}
