package utilitaire;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Transport implements InterfaceTransport {


    private Socket s = null;
    private ObjectOutputStream out;
    private ObjectInputStream din;

    // Dédié au client
    public Transport(final String adresse, final int port) throws Exception
    {
        s = new Socket(adresse, port);
        out = new ObjectOutputStream(s.getOutputStream());
        din = new ObjectInputStream(s.getInputStream());
        System.out.println("Transport client to server : OK");
    }

    // Dédié au server
    public Transport(final Socket socket) throws IOException
    {
        this.s = socket;
        din = new ObjectInputStream(s.getInputStream());
        out = new ObjectOutputStream(s.getOutputStream());
        System.out.println("Transport server to client : OK");
    }

    @Override
    public void envoyer(final Object object) throws IOException
    {
        out.writeObject(object);
    }

    @Override
    public Object recevoir() throws IOException, ClassNotFoundException
    {
        return din.readObject();
    }

    @Override
    public void fermer() throws IOException
    {
        if (out != null) {
            out.close();
        }
        if (din != null) {
            din.close();
        }
        if (s != null) {
            s.close();
        }
    }

}
