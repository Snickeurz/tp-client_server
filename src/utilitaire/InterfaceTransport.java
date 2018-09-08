package utilitaire;

import java.io.IOException;

public interface InterfaceTransport {
    void envoyer(Object obj) throws IOException;
    Object recevoir() throws IOException, ClassNotFoundException ;
    void fermer() throws IOException;
}

