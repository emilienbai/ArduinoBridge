package IHM;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class KeyChooser extends JFrame {

    private KeyEvent keyEvent = null;

    public KeyChooser() {
        super("Choix du raccourci");
        JPanel mainPanel = null;
        mainPanel = new JPanel();
        mainPanel.setBackground(OperatingWindows.BACKGROUND_COLOR);
        mainPanel.setBorder(new MatteBorder(3, 3, 3, 3, OperatingWindows.NAME_COLOR));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);
        this.setPreferredSize(new Dimension(400, 70));
        this.add(mainPanel);

        JLabel message = new JLabel("Appuyez sur la touche que vous souhaitez attribuer à cette piste");
        message.setForeground(OperatingWindows.FOREGROUND_COLOR);
        message.setHorizontalAlignment(SwingConstants.CENTER);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(message, constraints);

        ++constraints.gridy;
        mainPanel.add(Box.createVerticalStrut(10), constraints);

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setBackground(OperatingWindows.BUTTON_COLOR);
        cancelButton.setForeground(OperatingWindows.FOREGROUND_COLOR);
        cancelButton.setBorder(OperatingWindows.RAISED_BORDER);
        cancelButton.setPreferredSize(new Dimension(80, 25));

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        ++constraints.gridy;
        constraints.fill = GridBagConstraints.NONE;
        mainPanel.add(cancelButton, constraints);

        setVisible(true);
        this.pack();

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher(this));

    }

    public static void main(String[] args) {
        KeyChooser kc = new KeyChooser();
        while (kc.isVisible()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(kc.getKeyEvent().getKeyChar());
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }

    public void setKeyEvent(KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
    }

    private class MyDispatcher implements KeyEventDispatcher {
        KeyChooser keyChooser;

        public MyDispatcher(KeyChooser kc) {
            this.keyChooser = kc;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_TYPED) {
                keyChooser.setKeyEvent(e);
                keyChooser.setVisible(false);
                keyChooser.dispose();
            }
            return false;
        }
    }
}
