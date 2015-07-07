package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/***
 * SocOutTh
 * Read message from client and send it to server
 * Date: 13/12/14
 *
 * @author mhaidara & ebai
 */
public class SocOutTh extends Thread {
    private static PrintStream socOut;
    private static BufferedReader stdIn;
    private static SocInTh SIT;
    private static int port;
    private static String hostname;

    /**
     * SocOutTh's construtor.
     * It opens the socket between the client and the server.
     *
     * @param hostname and portnumber,formatted as Strings.
     * @throws IOException
     * @throws UnknownHostException
     **/
    public SocOutTh(String hostname, int portnb) {
        this.port = portnb;
        this.hostname = hostname;


    }

    public static boolean connect() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        /*
      the communication socket with the server
	 */
        Socket echoSocket = null;
        try {
            echoSocket = new Socket(hostname, port);
            BufferedReader socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            socOut = new PrintStream(echoSocket.getOutputStream());
            SIT = new SocInTh(socIn);
            SIT.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * getter for the printstream used for sending messages to the server
     *
     * @return socOut the prinstream to the server
     */
    public static PrintStream getSocOut() {
        return socOut;
    }

    public static void disconnect() {
        //closing procedure
        System.out.println("Fin de la connexion");
        try {
            SIT.StopInTh();
            socOut.close();
            stdIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * receives textmessage from the standard input and send it to the server
     **/
    public void run() {
        try {
            String line = "a";
            while (true) {
                line = stdIn.readLine();
                //read the standard input
                socOut.println(line);
                //send it to server addind the client name in first place
                if (SIT.GetFinished()) {
                    break;
                }
            }


        } catch (IOException e) {
            System.err.println("Error in SocOutTh");
        }
    }


}
