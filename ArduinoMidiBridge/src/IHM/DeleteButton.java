package IHM;

import Metier.SensorManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */


public class DeleteButton extends JButton {
    private SensorRow toDelete;
    private JPanel from;
    private JFrame ancestorFrom;

    private final Color BACKGROUND_COLOR = OperatingWindows.BACKGROUND_COLOR;
    private final Color FOREGROUND_COLOR = OperatingWindows.FOREGROUND_COLOR;
    private final Color BUTTON_COLOR = OperatingWindows.BUTTON_COLOR;

    public DeleteButton(SensorRow toDelete, JPanel from, JFrame ancestorFrom, JLabel sensorNumber ) {
        super("Supprimer");
        this.toDelete = toDelete;
        this.from = from;
        this.ancestorFrom = ancestorFrom;
        this.setBackground(BUTTON_COLOR);
        this.setForeground(FOREGROUND_COLOR);


        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    int nb;
                    @Override
                    public void run() {
                        SensorManagement.deleteSensor(toDelete.getMidiPort());
                        nb = Integer.parseInt(sensorNumber.getText());
                        nb--;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                OperatingWindows.removeFromSensorList(toDelete.getMidiPort());
                                OperatingWindows.resetMidiCombo();
                                OperatingWindows.removeFromDBList(DeleteButton.this);
                                from.remove(toDelete);
                                from.remove(DeleteButton.this);
                                sensorNumber.setText(String.valueOf(nb));
                                ancestorFrom.repaint();
                                ancestorFrom.pack();
                            }
                        });
                    }
                }).start();

            }

        });
    }

    public String toString(){
        String toReturn = "Bouton supprimer de la ligne " + this.toDelete.getName();
        return toReturn;
           }

}
