import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private Hashtable<String, GestorClients> clients;
    private boolean sortir;

    public ServidorXat() {
        this.clients = new Hashtable<>();
        this.sortir = false;
    }

    public void servidorAEscoltar() throws IOException {
        // Implementació pendent
    }

    public void pararServidor() {
        // Implementació pendent
    }

    public void finalitzarXat() {
        // Implementació pendent
    }

    public void afegirClient(GestorClients gestorClient) {
        // Implementació pendent
    }

    public void eliminarClient(String nom) {
        // Implementació pendent
    }

    public void enviarMissatgeGrup(String missatge) {
        // Implementació pendent
    }

    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        // Implementació pendent
    }

    public static void main(String[] args) {
        // Implementació pendent
    }
}