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

    /**
     * Choose the OSC Setup you want to use for this session
     *
     * @param address the Ip address to connect to
     * @param port    the sending port to use
     * @return true if the connexion has been established
     */
    public static boolean chooseOSCParams(String address, int port) {
        if (oscPortOut != null) {
            oscPortOut.close();
        }
        try {
            oscPortOut = new OSCPortOut(InetAddress.getByName(address), port);
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
    public static OSCPortOut getOscPortOut() {
        return oscPortOut;
    }

    /**
     * Close the oscPort
     */
    public static void exit() {
        if (oscPortOut != null) {
            oscPortOut.close();
        }
    }
}