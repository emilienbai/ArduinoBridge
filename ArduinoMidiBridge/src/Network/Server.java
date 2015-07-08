package Network;

import Metier.Services;

import java.io.IOException;
import java.io.PrintStream;
import java.net.*;
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
    private static List<PrintStream> connectedClient = new ArrayList<>();
    private static List<ClientThread> clientThreadsList = new ArrayList<>();
    // Prinstream list where message can be send


    private static ServerSocket listenSocket;
    ;
    private static boolean running = false;
    private static String logs = "";


    public static void addClient(PrintStream toAdd) {
        connectedClient.add(toAdd);
    }

    public static void removeClient(PrintStream toRemove) {
        connectedClient.remove(toRemove);
        System.out.println("Un client est parti : " + toRemove.toString());

    }

    public static boolean connect(int portNumber) {
        try {
            listenSocket = new ServerSocket(portNumber); //port
            System.out.println("Server ready...");
            addLogs("Server Ready ...");
            running = true;
            return true;
        } catch (IOException e) {
            System.err.println("Error in Server:" + e);
            e.printStackTrace();
            return false;
        }
    }

    public static void close() {
        running = false;
        try {
            for (PrintStream p : connectedClient) {
                p.close();
            }
            for (ClientThread ct : clientThreadsList) {
                ct.interrupt();
            }
            listenSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void run() {
        Socket clientSocket;
        while (running) {
            try {
                clientSocket = listenSocket.accept();
                System.out.println("Connexion from:" + clientSocket.getInetAddress());
                addLogs("Connexion from:" + clientSocket.getInetAddress());
                ClientThread ct = new ClientThread(clientSocket);
                ct.start();
                clientThreadsList.add(ct);
            } catch (IOException e) {
                e.printStackTrace();
                addLogs("Server closed \n");
            }
        }
    }

    public static void clientDisconnection(ClientThread ct, InetAddress ip, PrintStream socOut) {
        clientThreadsList.remove(ct);
        connectedClient.remove(socOut);
        System.out.println("Disconnection from ip :" + ip);
        addLogs("Disconnection from ip :" + ip);
    }

    public static boolean isRunning() {
        return running;
    }

    public static String getIP() {
        Enumeration en = null;
        try {
            en = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (en != null) {
            while (en.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) en.nextElement();
                Enumeration ee = ni.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress ia = (InetAddress) ee.nextElement();
                    String host = ia.getHostAddress();
                    if ((host.startsWith("1") || host.startsWith("2")) /*|| host.split(".")[0].length() == 2)*/ && (!host.startsWith("127"))) {
                        // 192. ... like,       255. ... like,          85. ... like                            not 127.0.0.1
                        return host;
                    }
                }
            }
        }
        return "Adress not found";
    }

    protected static void addLogs(String toAdd) {
        logs = logs + toAdd + "\n";
        Services.fillServerSettings(logs);
    }

    public static void sendData(String data) {
        if (!connectedClient.isEmpty()) {
            for (PrintStream p : connectedClient) {
                p.println(data);
            }
        }
    }
}

  
