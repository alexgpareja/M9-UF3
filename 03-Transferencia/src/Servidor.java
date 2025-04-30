import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    // Constants
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
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

}
