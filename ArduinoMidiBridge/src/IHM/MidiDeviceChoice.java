package IHM;

import Metier.MidiManager;

import javax.sound.midi.MidiDevice;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import static java.lang.System.exit;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class MidiDeviceChoice extends JFrame{
    private JPanel choiceWindow;
    private JButton okButton;
    private JButton reloadButton;
    private JButton quitButton;
    private JList deviceList;
    private JProgressBar reloadProgress;
    private JPanel deviceDetails;
    private JLabel deviceDescription;
    private MidiDevice.Info choosenDevice = null;
    private boolean readyToClose = false;

    public MidiDeviceChoice(){
        super("Choix du périphérique midi");

        /* TODO Change that, the list need to be generated before and given as a parameter */
        /*Filling the List*/
        Vector<MidiDevice.Info> availableDevice = MidiManager.getAvailableMidiDevices();
        deviceList.setListData(availableDevice);


        setContentPane(choiceWindow);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        /*
        There come every action Listener
         */
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (readyToClose) {
                    dispose();
                    OperatingWindows operatingWindows= new OperatingWindows();
                } else {
                    JOptionPane.showMessageDialog(MidiDeviceChoice.this,
                            "<html><center>Impossible de se connecter au " +
                                    "périphérique midi sélectionné<br> " +
                                    "Veuillez réessayer</center></html>",
                            " Avertissement ",
                            JOptionPane.WARNING_MESSAGE);
                }
                }
            });

            quitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e){
                    exit(0);
                }
            }

            );

            reloadButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed (ActionEvent e){
                    new Thread(new Runnable() {
                        public void run() {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    reloadProgress.setVisible(true);
                                }
                            });
                            Vector<MidiDevice.Info> availableDevice = MidiManager.getAvailableMidiDevices();
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    deviceList.setListData(availableDevice);
                                    reloadProgress.setVisible(false);
                                }
                            });
                        }
                    }).start();
                }
            }

            );

            deviceList.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged (ListSelectionEvent e){
                    new Thread(new Runnable() {
                        public void run() {
                            choosenDevice = (MidiDevice.Info) deviceList.getSelectedValue();
                            String description = choosenDevice.getDescription();
                            String vendor = choosenDevice.getVendor();
                            readyToClose = MidiManager.chooseMidiDevice(choosenDevice);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    deviceDescription.setText("<html>Description : " + description + "<br>"
                                            + "Vendeur : " + vendor + "</html>");
                                }
                            });
                        }
                    }).start();
                }
            }

            );

            setVisible(true);

        }


    }
