package IHM;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */


public class DeleteButton extends JButton {
    private SensorRow toDelete;

    /**
     * Constructor for a deletion Button, which remove a MidiSensorRow
     *
     * @param toDelete     sensorRow to remove
     */
    public DeleteButton(SensorRow toDelete) {
        super("Supprimer");
        this.toDelete = toDelete;
        this.setBackground(OperatingWindows.BUTTON_COLOR);
        this.setForeground(OperatingWindows.FOREGROUND_COLOR);
        this.setBorder(OperatingWindows.RAISED_BORDER);
        this.setPreferredSize(new Dimension(70, 35));

        this.addActionListener(e -> new Thread(() -> {
            if (toDelete.getType() == SensorRow.MIDI) {
                OperatingWindows.removeFromMidiSensorList(toDelete);
            } else if (toDelete.getType() == SensorRow.OSC) {
                OperatingWindows.removeFromOscSensorList(toDelete);
            }
        }).start());
    }

}
