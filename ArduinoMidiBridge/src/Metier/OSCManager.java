package Metier;

import com.illposed.osc.OSCPortOut;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Emilien Bai (emilien.bai@insa-lyon.fr) on 07/2015.
 */
public class OSCManager {
    private static OSCPortOut oscPortOut;
    private static String address = "127.0.0.1";
    private static int port = 12345;

    /**
     * Choose the OSC Setup you want to use for this session
     *
     * @param address the Ip address to connect to
     * @param port    the sending port to use
     * @return true if the connexion has been established
     */
    protected static boolean chooseOSCParams(String address, int port) {
        if (oscPortOut != null) {
            oscPortOut.close();
        }
        try {
            oscPortOut = new OSCPortOut(InetAddress.getByName(address), port);
            OSCManager.address = address;
            OSCManager.port = port;
            return true;
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.err.println("Echec au lancement du serveur OSC");
            return false;
        }
    }

    /**
     * gettter for the used osc port out
     *
     * @return the currently used osc port Out
     */
    protected static OSCPortOut getOscPortOut() {
        return oscPortOut;
    }


    /**
     * Close the oscPort
     */
    protected static void exit() {
        if (oscPortOut != null) {
            oscPortOut.close();
        }
    }

    /**
     * Getter for the currently used address
     *
     * @return the currently used ip address
     */
    protected static String getAddress() {
        return address;
    }

    /**
     * Getter for the currently used port
     *
     * @return the currently used port
     */
    protected static int getPort() {
        return port;
    }
}