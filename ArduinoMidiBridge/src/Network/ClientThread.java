package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * ClientThread
 * Receive and transform message from client
 * Date: 19/12/14
 * Authors: ebai & mhaidara
 */
public class ClientThread
        extends Thread {
    /**
     * the communication socket with the client
     */
    private Socket clientSocket;
    /**
     * The printstream to send message to the client
     */
    private PrintStream socOut;

    private boolean running;


    /**
     * ClientThread constructor
     *
     * @param s the communication socket.
     * @throws IOException
     */
    ClientThread(Socket s) throws IOException {
        this.clientSocket = s;
        socOut = new PrintStream(clientSocket.getOutputStream());
        Server.addClient(socOut); // ajout du prinstream correspondant à la liste
        running = true;
    }

    /**
     * receives a command from client then sends the corresponding message
     * to all the connected clients.
     **/
    public void run() {
        try {
            BufferedReader socIn;
            socIn = new BufferedReader(new InputStreamReader
                    (clientSocket.getInputStream()));
            //Initialise the adapted BufferedReader
            while (running) {
                String line = socIn.readLine();
                if (line.equals("QUIT")) {
                    Server.removeClient(socOut);
                    break;
                } else {
                    System.out.println("Server receive : " + line);
                    Server.addLogs("Server receive : " + line);
                }
            }
            socIn.close();
        } catch (Exception e) {
            System.err.println("Error in ClientThread:" + e);
            Server.clientDisconnection(this, clientSocket.getInetAddress(), socOut);
            running = false;
        }
    }
}