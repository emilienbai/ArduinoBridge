package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class SocOutTh extends Thread {
    private static PrintStream socOut;
    private static BufferedReader stdIn;
    private static SocInTh SIT;
    private static int port;
    private static String hostname;
    private static boolean running;

    public SocOutTh(String hostname, int portnb) {
        port = portnb;
        SocOutTh.hostname = hostname;
    }

    /**
     * Connect the Output Thread to the server
     *
     * @return true if connected
     */
    public static boolean connect() {
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        try {
            Socket echoSocket = new Socket(hostname, port);
            BufferedReader socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            socOut = new PrintStream(echoSocket.getOutputStream());
            SIT = new SocInTh(socIn);
            SIT.start();
            running = true;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
