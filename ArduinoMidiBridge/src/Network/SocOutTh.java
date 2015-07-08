package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

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


    public SocOutTh(String hostname, int portnb) {
        port = portnb;
        SocOutTh.hostname = hostname;


    }

    public static boolean connect() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {
            Socket echoSocket = new Socket(hostname, port);
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
            String line;
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
