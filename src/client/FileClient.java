package client;

import utilitaire.Transport;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

public class FileClient {
    private static final int PORT = 1234;

    private static boolean usageOk(String[] argv) {
        if (argv.length != 2) {
            String msg = "usage is: "
                       + "client.FileClient server-name file-name";
            System.out.println(msg);
            return false;
        } // if
        return true;
    }
    
    public static void main(String[] argv) {
        int exitCode = 0;
        System.out.println("Lancement file Client");
        if (!usageOk(argv))
        {
            System.exit(1);
        }
        System.out.println("Arguments acceptés !");
        System.out.println("Creation d'un nouveau transport");
        Transport transport = null;
        try
        {
            transport = new Transport(argv[0], PORT);
            System.out.println(
                    String.format("Transport OK avec les arguments suivants :\nadr : %s \n port : %d ", argv[0], PORT)
            );
        }catch (Exception e)
        {
            System.err.println("Unable to connect to transporter");
            e.printStackTrace();
            System.exit(1);
        }
        // Envoie du fichier
        try {
            System.out.println("Envoie du fichier");
            transport.envoyer(argv[1]);

            System.out.println("Reception du status :");
            String serverStatus = (String) transport.recevoir();

            if (serverStatus.startsWith("Bad")) {
                System.out.println ("serverStatus : Bad");
                exitCode = 1;
            } else {
            	System.out.println ("serverStatus : Good");
                StringBuilder s = (StringBuilder) transport.recevoir();
                System.out.println(s.toString());
            }
        }catch (IOException | ClassNotFoundException exception)
        {
            System.out.println("Erreur lors de l'appel de la méthode transport.recevoir() : " + exception.getMessage());
            exitCode = 1;
        }finally
        {
            try
            {
               transport.fermer();
            } catch (IOException e){
                System.out.println("Impossible de fermer le transport.. You'r dead.");
            }
        }
        System.exit(exitCode);
    }
}
