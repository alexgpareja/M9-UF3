import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    private final int PORT = 7777;
    private final String HOST = "localhost";
    ServerSocket serverSocket;
    Socket clientSocket;

    public void connecta() {
        try {

            // Obrir la connexió
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

            // Acceptar la connexió del client
            clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress().getHostAddress() + ":"
                    + clientSocket.getPort());

        } catch (IOException e) {
            System.out.println("Error al connectar desde el servidor: " + e.getMessage());
        }
    }

    public void repDades() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            // Llegim el text del client
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println("Rebut del client: " + line);
            }

        } catch (IOException e) {
            System.out.println("Error al rebre dades: " + e.getMessage());
        }
    }

    public void tanca() {
        try {
            // Tancar la connexió
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Connexió amb el client tancada.");
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Servidor tancat.");
            }
        } catch (IOException e) {
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Creem una instància del servidor
        Servidor servidor = new Servidor();

        // Connectem el servidor
        servidor.connecta();

        // Rebem dades del client
        servidor.repDades();

        // Tanquem la connexió
        servidor.tanca();
    }
}
