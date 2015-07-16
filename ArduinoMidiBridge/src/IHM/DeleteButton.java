package IHM;

import Metier.Services;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */


public class DeleteButton extends JButton {
    private SensorRow toDelete;

    /**
     * Constructor for a deletion Button, which remove a SensorRow
     *
     * @param toDelete     sensorRow to remove
     * @param from         Panel where the sensorRow is
     * @param ancestorFrom The frame to repaint after the deletion
     * @param sensorNumber Number of the Sensor to remove from the List
     */
    public DeleteButton(SensorRow toDelete, JPanel from, JFrame ancestorFrom, JLabel sensorNumber) {
        super("Supprimer");
        this.toDelete = toDelete;
        Color BUTTON_COLOR = OperatingWindows.BUTTON_COLOR;
        this.setBackground(BUTTON_COLOR);
        Color FOREGROUND_COLOR = OperatingWindows.FOREGROUND_COLOR;
        this.setForeground(FOREGROUND_COLOR);
        Border RAISED_BORDER = OperatingWindows.RAISED_BORDER;
        this.setBorder(RAISED_BORDER);
        this.setPreferredSize(new Dimension(70, 35));

        this.addActionListener(e -> new Thread(new Runnable() {
            int nb;

            @Override
            public void run() {
                Services.deleteSensor(toDelete.getMidiPort());
                nb = Integer.parseInt(sensorNumber.getText());
                nb--;
                SwingUtilities.invokeLater(() -> {
                    OperatingWindows.removeFromSensorList(toDelete.getMidiPort(), DeleteButton.this);
                    OperatingWindows.resetMidiCombo();
                    from.remove(toDelete);
                    from.remove(DeleteButton.this);
                    sensorNumber.setText(String.valueOf(nb));
                    ancestorFrom.repaint();
                    ancestorFrom.pack();
                });
            }
        }).start());
    }

    /**
     * Convert the deleteButton's information into a String
     *
     * @return The deleteButton's information.
     */
    public String toString() {
        return "Bouton supprimer de la ligne " + this.toDelete.getName();
    }
}
