package Network;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * EchoServer
 * A multithreaded TCP Server
 * Date: 19/12/14
 *
 * @author ebai & mhaidara
 */
public class Server {
    /**
     * a global list of all the Prinstream where the server can print messages
     */
    private static List<PrintStream> connectedClient = new ArrayList<PrintStream>();
    // Prinstream list where message can be send

    /**
     * main method
     *
     * @param args which is the Server port to open
     **/
    public static void main(String args[]) {
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
            Enumeration en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) en.nextElement();
                Enumeration ee = ni.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress ia = (InetAddress) ee.nextElement();
                    if (ia.getHostAddress().startsWith("192")) {
                        System.out.println(ia.getHostAddress());
                    }
                }
            }
            System.out.println("Server ready...");
            while (true) {
                //waiting for new connexions requests
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                ClientThread ct = new ClientThread(clientSocket);
                ct.start();
                sendData("Bienvenu au nouveau");
            }

        } catch (Exception e) {
            System.err.println("Error in EchoServerMultiThread:" + e);
            e.printStackTrace();
        }
    }

    public static void addClient(PrintStream toAdd) {
        connectedClient.add(toAdd);
    }

    public static void removeClient(PrintStream toRemove) {
        connectedClient.remove(toRemove);
        System.out.println("Un client est parti : " + toRemove.toString());
    }

    public static void sendData(String data) {
        if (!connectedClient.isEmpty()) {
            for (PrintStream p : connectedClient) {
                p.println(data);
            }
        }
    }
}

  
