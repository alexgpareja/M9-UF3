import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class ServidorXat {

    // Constants
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    // Per gestionar les conexions
    private ServerSocket serverSocket;

    public void iniciarServidor() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a: " + HOST + " " + PORT);
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor aturat");
            }
        } catch (IOException e) {
            System.err.println("Error al aturar el servidor: " + e.getMessage());
        }
    }

    public Object getNom(ObjectInputStream input) {
        try {
            return input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al llegir el nom del client: " + e.getMessage());
            return null;
        }
    }

}
