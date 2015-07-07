package Network;

import java.io.IOException;
import java.net.UnknownHostException;

/***
 * EchoClient
 * Minimal TCP Client
 * Date: 19/12/14
 * Authors: ebai & mhaidara
 */
public class EchoClient {

    static SocOutTh socoutth;


    /**
     * main method, start a new Thread.
     *
     * @throws IOException
     * @throws UnknownHostException
     **/
    public static void main(String[] args) throws UnknownHostException, IOException {
        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
            System.exit(1);
        }
        socoutth = new SocOutTh(args[0], Integer.parseInt(args[1]));
        socoutth.start();
    }
}

