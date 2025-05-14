import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";

    private Hashtable<String, GestorClients> clients;
    private boolean sortir;
    private ServerSocket serverSocket;

    public ServidorXat() {
        clients = new Hashtable<>();
        sortir = false;
    }

    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (IOException e) {
            System.err.println("Error iniciant servidor: " + e.getMessage());
            System.exit(1);
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error tancant servidor: " + e.getMessage());
        }
    }

    public void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        clients.clear();
        sortir = true;
        System.out.println("Tancant tots els clients.");
        System.out.println("DEBUG: multicast sortir");
        pararServidor();
    }

    public void afegirClient(GestorClients client) {
        clients.put(client.getNom(), client);
        enviarMissatgeGrup("Entra: " + client.getNom());
        System.out.println(client.getNom() + " connectat.");
        System.out.println("DEBUG: multicast Entra: " + client.getNom());
    }

    public void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
            System.out.println(nom + " desconnectat.");
        }
    }

    public void enviarMissatgeGrup(String missatge) {
        String msgFormatejat = Missatge.getMissatgeGrup(missatge);
        for (GestorClients client : clients.values()) {
            client.enviarMissatge(msgFormatejat);
        }
    }

    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        if (clients.containsKey(destinatari)) {
            GestorClients client = clients.get(destinatari);
            String msgFormatejat = Missatge.getMissatgePersonal(remitent, missatge);
            client.enviarMissatge(msgFormatejat);
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
        } else {
            // El destinatari no existeix
            if (clients.containsKey(remitent)) {
                GestorClients client = clients.get(remitent);
                client.enviarMissatge(Missatge.getMissatgeGrup("El destinatari " + destinatari + " no existeix."));
            }
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();

        while (!servidor.sortir) {
            try {
                Socket clientSocket = servidor.serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getInetAddress());

                GestorClients gestorClient = new GestorClients(clientSocket, servidor);
                Thread threadClient = new Thread(gestorClient);
                threadClient.start();

            } catch (IOException e) {
                if (!servidor.sortir) {
                    System.err.println("Error acceptant connexió: " + e.getMessage());
                }
            }
        }

        servidor.pararServidor();
    }
}