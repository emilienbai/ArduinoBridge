package IHM;

import Metier.SensorManagement;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */


public class DeleteButton extends JButton {
    private SensorRow toDelete;

    public DeleteButton(SensorRow toDelete, JPanel from, JFrame ancestorFrom, JLabel sensorNumber ) {
        super("Supprimer");
        this.toDelete = toDelete;
        Color BUTTON_COLOR = OperatingWindows.BUTTON_COLOR;
        this.setBackground(BUTTON_COLOR);
        Color FOREGROUND_COLOR = OperatingWindows.FOREGROUND_COLOR;
        this.setForeground(FOREGROUND_COLOR);
        Border ETCHED_BORDER = OperatingWindows.ETCHED_BORDER;
        this.setBorder(ETCHED_BORDER);


        this.addActionListener(e -> new Thread(new Runnable() {
            int nb;
            @Override
            public void run() {
                SensorManagement.deleteSensor(toDelete.getMidiPort());
                nb = Integer.parseInt(sensorNumber.getText());
                nb--;
                SwingUtilities.invokeLater(() -> {
                    OperatingWindows.removeFromSensorList(toDelete.getMidiPort());
                    OperatingWindows.resetMidiCombo();
                    OperatingWindows.removeFromDBList(DeleteButton.this);
                    from.remove(toDelete);
                    from.remove(DeleteButton.this);
                    sensorNumber.setText(String.valueOf(nb));
                    ancestorFrom.repaint();
                    ancestorFrom.pack();
                });
            }
        }).start());
    }

    public String toString(){
        return "Bouton supprimer de la ligne " + this.toDelete.getName();
           }

}
