package IHM;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */


public class VuMeter extends JProgressBar{
    private final Color OK_COLOR = new Color(66, 174, 59);
    private final Color WARNING_COLOR = new Color(209, 96, 9);
    private final Color ALERT_COLOR = new Color(200, 34, 25);
    public VuMeter(int orient, int min, int max){
        super(orient, min, max);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel jp = new JPanel();
        VuMeter vm = new VuMeter(HORIZONTAL, 0, 1024);
        jp.add(vm);
        frame.add(jp);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        vm.setValue(1000);
    }

    public void setValue(int value, int factor){
        super.setValue(value);
        if (value < 0.65*this.getMaximum()){
            this.setForeground(OK_COLOR);
        }
        else if (value < 0.85*this.getMaximum()) {
            this.setForeground(WARNING_COLOR);
        }
        else {
            this.setForeground(ALERT_COLOR);
        }
    }

    @Override
    public void setValue(int value){
        setValue(value, 1);
    }
}


