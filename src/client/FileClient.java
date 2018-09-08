package client;

import utilitaire.Transport;

import java.io.IOException;

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
        Transport transport = null;

        System.out.println("Lancement file Client");

        if (!usageOk(argv))
        {
            System.out.println("Erreur : Il faut 2 arguments.");
            System.exit(1);
        }

        System.out.println("Arguments acceptés !Creation d'un nouveau transport");

        try
        {
            // Nouvel instance de Transport
            transport = new Transport(argv[0], PORT);
            System.out.println(
                    String.format("Transport OK avec les arguments suivants :\nadr : %s \n port : %d \n %s", argv[0], PORT, argv[1])
            );
        }catch (Exception e)
        {
            System.err.println("Imposssible de se connecter.");
            e.printStackTrace();
            System.exit(1);
        }
        try {
            // Envoie du fichier sur le server
            System.out.println("Envoie du fichier");
            transport.envoyer(argv[1]);

            // Le server doit répondre "Good" or "Bad"
            System.out.println("Reception du status :");
            String serverStatus = (String) transport.recevoir();

            if (serverStatus.startsWith("Bad")) {
                System.out.println ("serverStatus : Bad");
                exitCode = 1;
            } else {
            	System.out.println ("serverStatus : Good");
            	// On récupère le contenu de la réponse & on l'affiche
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
                // Fermeture du transport
                transport.fermer();
            } catch (IOException e){
                System.out.println("Impossible de fermer le transport.. You'r dead.");
            }
        }
        System.exit(exitCode);
    }
}
