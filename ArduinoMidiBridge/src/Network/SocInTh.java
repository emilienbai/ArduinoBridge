package Network;

import Metier.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
    private int port;
    private String hostname;


    public SocInTh(String hostname, int portNumber) {
        this.port = portNumber;
        this.hostname = hostname;
        finished = false;
    }

    public boolean connect() {
        try {
            Socket echoSocket = new Socket(this.hostname, this.port);
            socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            this.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * receives messages from the server and display them.
     **/
    public void run() {
        try {
            String line = "";

            while (!finished && line != null) {
                line = socIn.readLine(); //receive message from server
                Services.sendMessage(line);
            }
        } catch (IOException e) {
            System.err.println("Error in SocInTh");
        } catch (NullPointerException e) {
            Services.signalDisconnection();
        }
    }
}
