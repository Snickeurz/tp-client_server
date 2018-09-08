package server;

import utilitaire.Transport;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer
{
    // The default port number that this server will listen on
    private final static int DEFAULT_PORT_NUMBER = 1234;

    // The maximum connections the operating system should
    // accept when the server is not waiting to accept one.
    private final static int MAX_BACKLOG = 20;
    
    // Timeout in milliseconds for accepting connections.
    // It may go this long before noticing a request to shut down.
    private final static int TIMEOUT = 500;

    // The port number to listen for connections on
    private int portNumber;

    // Sets to true when server should shut down.
    private boolean shutDownFlag = false;

    private int activeConnectionCount = 0;

    /**
     * Constructor for server that listens on default port.
     */
    public FileServer() {
        this(DEFAULT_PORT_NUMBER);
    }

    /**
     * Two instances of the server will not be able to
     * successfully listen for connections on the same port.
     * @param port The port number to listen on.
     */
    public FileServer(final int port) {
        portNumber = port;
    }

    /**
     * Return the number of active connections
     */
    public int getActiveConnectionCount() {
        return activeConnectionCount;
    }
    
    /**
     * This is the top level method for the file server.
     * It does not return until the server shuts down.
     */
    public void runServer() {
        ServerSocket s;
        
        try {
            // Create the ServerSocket.
            System.out.println ("Lancement du serveur");
            s = new ServerSocket(portNumber, MAX_BACKLOG);

            // Set timeout for accepting connections so server won't
            // wait forever until noticing a request to shut down.
            s.setSoTimeout(TIMEOUT);
        } catch (IOException e) {
            System.err.println("Impossible de créer la socket socket");
            e.printStackTrace();
            return;
        } // try

        // loop to keep accepting new connections and talking to clients
        try {
            Socket socket;
          serverLoop:
            while (true) {                  // Keep accepting connections.
                try {
                    socket = s.accept(); // Accept a connection.
                } catch (java.io.InterruptedIOException e) {
                    socket = null;
                    if (!shutDownFlag)
                      continue serverLoop;
                } // try
                if (shutDownFlag) {
                    if (socket != null)
                      socket.close();
                    s.close();
                    return;
                } // if
                // Create worker object to process connection.
                System.out.println ("[Server] Acceptation d'un client ");
                new FileServerWorker(socket);
            } // while
        } catch (IOException e) {
            // if there is an I/O error just return
        } // try
    } // start()

    /**
     * This is called to request the server to shut down.
     */
    public void stop() {
        shutDownFlag = true;
    } // shutDown()

    /**
     * Inner class.
     */
    private class FileServerWorker implements Runnable {
        /**
         * Transport attribute.
         */
        private Transport t;

        /**
         * FileServerWorker's Constructor.
         * @param s socket
         */
        FileServerWorker(Socket s) {
            try
            {
                // Instance de Transport avec un socket
                this.t = new Transport(s);
            }catch (IOException e)
            {
                System.out.println("Impossible ce créer le transport : " + e.getStackTrace());
                System.exit(1);
            }
            // Démarrage d'un thread FileServer
            new Thread(this).start();
        }

        /**
         * Runnable's implementation.
         */
        public void run() {
            // Le nom du fichier
            String fileName = "";
            // Le flux d'affichage
            PrintStream out = null;
            // Le fichier
            FileInputStream f;
            // Incrémentation du nombre de ocnnection active
            activeConnectionCount++;

 			System.out.println ("[Server] Lancement du thread pour gérer le protocole avec un client");

 			// read the file name sent by the client and open the file.
            try {
                // Récupération du nom de fichier
                fileName = (String) this.t.recevoir();
                // Ouvre le fichier
                f = new FileInputStream(fileName);
                System.out.println("[Server] Réception du fichier.. \n Lecture du fichier");

                //Lecture du fichier
                int content;
                StringBuilder contenueFichier = new StringBuilder();
                while ((content = f.read()) != -1) {
                    contenueFichier.append((char)content);
                }
                //Envoie du status good au client
                t.envoyer("Good");

                //Envoie du contenue du fichier au client
                t.envoyer(contenueFichier);

            } catch (IOException | ClassNotFoundException e) {
                // Décrémenter le nombre de connection active
                activeConnectionCount--;
                System.out.println("Problèmes possible : échec de Transport, échec de la lecture du fichier, fichier non trouvé .. "
                        + e.getMessage() + e.getStackTrace());
                try
                {
                    // Envoie du mot clef "Bad" au client
                    t.envoyer("Bad");
                } catch (IOException ie) {
                    System.out.println("Problème lors de l'envoie par Transport : " + ie.getStackTrace());
                }
                return;
            }

            byte[] buffer = new byte[4096];
            try {
                int len;
                while (!shutDownFlag && (len = f.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                } // while
            } catch (IOException e) {
            } finally {
                try {
                    t.fermer();
                } catch (IOException e) {
                }
            }
        }
    }
}
