import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    // Constants
    private static final int PORT = 9999;
    private static final String HOST = "localhost";

    public static int getPort() {
        return PORT;
    }

    public static String getHost() {
        return HOST;
    }

    ServerSocket serverSocket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        Socket socket = serverSocket.accept(); // Aceptar una conexión entrante
        System.out.println("Connexio acceptada: " + socket.getInetAddress());
        return socket;
    }

    public void tancarConnexio(Socket socket) throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Connexió tancada.");
        }
    }

    public void enviarFixers(Socket socket) throws IOException, ClassNotFoundException {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) input.readObject();

            if ("sortir".equalsIgnoreCase(nomFitxer)) {
                System.out.println("El client ha decidit sortir. Tancant connexió...");
                return;
            }

            System.out.println("Nomfitxer rebut: " + nomFitxer);

            Fitxer fitxer = new Fitxer(nomFitxer);

            try {
                // Obtenir el contingut del fitxer
                byte[] contingut = fitxer.getContingut();
                long tamany = contingut.length;

                System.out.println("Contingut del fitxer a enviar: " + tamany + " bytes");

                // Enviar el contingut al client
                output.writeObject(contingut);
                output.flush(); // Assegurar-se que s'ha enviat tot el contingut
                System.out.println("Fitxer enviat al client: " + nomFitxer);
            } catch (IOException e) {
                System.out.println("Error llegint el fitxer: " + e.getMessage());
                output.writeObject(null);
                System.out.println("Nom del fitxer buit o nul. Sortint...");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error en la comunicació amb el client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();

        try {
            // Connectar al servidor
            Socket socket = servidor.connectar();

            // Enviar fitxers al client
            servidor.enviarFixers(socket);

            // Tancar connexio
            servidor.tancarConnexio(socket);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
