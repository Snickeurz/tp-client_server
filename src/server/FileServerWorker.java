package server;

import utilitaire.Transport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

@SuppressWarnings("DanglingJavadoc")
public class FileServerWorker implements Runnable {

    /**
     * Transport socket.
     */
    private Transport transport;

    /**
     * Attributs utilitaires.
     */
    private String fileName;
    private PrintStream out;
    private FileInputStream f;

    /**
     * MUTATORS
     */
    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public PrintStream getOut() {
        return out;
    }

    public void setOut(final PrintStream out) {
        this.out = out;
    }

    public FileInputStream getF() {
        return f;
    }

    public void setF(final String f) {
        try{
            this.f = new FileInputStream(f);
        }catch (FileNotFoundException fileNotFound)
        {
            System.out.println("File's missing : " + fileNotFound.getStackTrace());
        }
    }
    /**
     * END MUTATORS
     */

    /**
     * FileServerWorker's Constructor.
     */
    public FileServerWorker(final Socket s) {
        try{
            //Nouveau transport de socket.
            this.transport = new Transport(s);

            //Default :
            this.setFileName("");
            this.setOut(null);
            this.setF(null);

            //Démarrage du thread
            new Thread(this).start();

        }catch (IOException e)
        {
            System.out.println("Erreur : " + e.getStackTrace());
        }
    }

    /**
     * run() implementation.
     */
    @Override
    public void run(){
        System.out.println("FileServerWorker working here ! ");

        //Read file logic :
        try{
            //Get the file
            this.setFileName((String) transport.recevoir());
            //Set the file input stream
            this.setF(this.getFileName());

            //call readFile()
            if(this.readFile().no)
            {

            }else{

            }


        }catch (IOException | ClassNotFoundException e)
        {
            System.out.println("File or Class missing : " + e.getStackTrace());
        }
    }

    public StringBuilder readFile()
    {
        StringBuilder stringBuilder = new StringBuilder();
        int content;
        StringBuilder contenueFichier = new StringBuilder();
        try {
            //Read file
            while ((content = this.getF().read()) != -1) {
                // Append stringBuilder
                contenueFichier.append((char) content);
            }
        }catch (IOException e)
        {
            System.out.println("IOException : "+e.getStackTrace());
        }
        return stringBuilder;
    }
}
