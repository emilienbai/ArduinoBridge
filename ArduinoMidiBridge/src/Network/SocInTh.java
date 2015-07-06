
package Network;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * SocInTh
 * Thread which receive and display incoming message from the server.
 * Date: 13/12/14
 *
 * @author mhaidara & ebai
 */
public class SocInTh extends Thread {
    /**
     * where messages are received from the server
     */
    private BufferedReader socIn;
    private boolean finished;


    /**
     * SocInTh constructor
     *
     * @param br sockets's bufferedreader.
     **/
    public SocInTh(BufferedReader br) {
        this.socIn = br;
        finished = false;
        //SocInTh is running
    }

    /**
     * receives messages from the server and display them.
     **/
    public void run() {
        try {
            String line = "";
            String endofline = "";
            String[] command;

            while (!finished) {
                line = "";
                line = socIn.readLine(); //receive message from server
                System.out.println(line);
                // we print the corresponding message on terminal.
                //todo uncomment when ready
                //Services.sendMidiMessage(line);
            }
        } catch (IOException e) {
            System.err.println("Error in SocInTh");
            e.printStackTrace();
        }

    }

    /**
     * Close the BufferedReader
     *
     * @throws IOException
     **/
    public void StopInTh() throws IOException {
        socIn.close();
    }

    /**
     * getter for the finished boolean
     *
     * @return finished, which tell if the Thread is still running.
     */
    public boolean GetFinished() {
        return finished;
    }


}
