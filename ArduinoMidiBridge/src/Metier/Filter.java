package Metier;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr)on 06/2015.
 */

public class Filter {

    public static void main(String[] args) {

        Filter f = new Filter();
        File[] filetab = f.finder("/dev/");
        for (File file : filetab) {
            System.out.println(file.getAbsolutePath());
        }

    }

    public File[] finder(String dirName) {
        File dir = new File(dirName);
        return (dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return (filename.startsWith("ttyACM") ||
                        filename.startsWith("ttyUSB") ||
                        filename.startsWith("tty.usbmodem") ||
                        filename.startsWith("tty.usbserial"));
            }
        }));

    }

}

