
package Network;

import Metier.Services;

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
    }

    /**
     * receives messages from the server and display them.
     **/
    public void run() {
        try {
            String line = "";

            while (!finished && line != null) {
                line = socIn.readLine(); //receive message from server
                Services.sendMidiMessage(line);
            }
        } catch (IOException e) {
            System.err.println("Error in SocInTh");
            finished = true;
        } catch (NullPointerException e) {
            System.out.println("On a perdu le serveur biatch");
            Services.signalDisconnection();
        }

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
