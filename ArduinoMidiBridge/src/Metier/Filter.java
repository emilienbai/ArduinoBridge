package Metier;

import java.io.File;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */

public class Filter {

    /**
     * Filter for the serial peripherical on linux and MacOS
     *
     * @param dirName directory where to look for (/dev/)
     * @return All the filtered files in a tab
     */
    public File[] finder(String dirName) {
        File dir = new File(dirName);
        return (dir.listFiles((dir1, filename) -> (filename.startsWith("ttyACM") ||
                filename.startsWith("ttyUSB") ||
                filename.startsWith("tty.usbmodem") ||
                filename.startsWith("tty.usbserial"))));

    }

}