import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    public static final String DIR_ARRIBADA = "/tmp";
    private ObjectOutputStream out;
    private ObjectInputStream in;
    Socket socket;

    public void connectar() throws IOException {
        socket = new Socket(Servidor.getHost(), Servidor.getPort());
        System.out.println("Connectant a -> " + Servidor.getHost() + ":" + Servidor.getPort());
        System.out.println("Connexio acceptada: " + socket.getInetAddress());

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void tancarConnexio() throws IOException {
        if (out != null) {
            out.close();
        }
        if (in != null) {
            in.close();
        }
        System.out.println("Connexio del client tancada.");
    }
}
