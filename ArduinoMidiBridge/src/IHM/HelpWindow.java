package IHM;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */
public class HelpWindow extends JFrame{
    /**
     * Display a Frame Containing the documentation
     */
    public HelpWindow(){
        super("Aide");

        JEditorPane helpText = new JEditorPane();

        URL url1 = this.getClass().getResource("/pages/help.html");

        try {
            helpText.setPage(url1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        helpText.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(helpText);
        this.add(scrollPane);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(600, 800));
        this.setVisible(true);
        this.repaint();
        this.pack();
    }

    public static void main (String [] args){
        HelpWindow hw = new HelpWindow();
        hw.setVisible(true);
        hw.pack();
    }

}
