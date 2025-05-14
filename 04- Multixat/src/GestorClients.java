import java.io.*;
import java.net.Socket;

public class GestorClients implements Runnable {
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidorXat;
    private String nom;
    private boolean sortir;

    public GestorClients(Socket client, ServidorXat servidorXat) throws IOException {
        this.client = client;
        this.servidorXat = servidorXat;
        this.oos = new ObjectOutputStream(client.getOutputStream());
        this.ois = new ObjectInputStream(client.getInputStream());
        this.sortir = false;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        // Implementació pendent
    }

    public void enviarMissatge(String remitent, String missatge) {
        // Implementació pendent
    }

    public void processaMissatge(String missatge) {
        // Implementació pendent
    }
}