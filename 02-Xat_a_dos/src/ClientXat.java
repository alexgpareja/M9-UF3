import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {

    // Constants
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public void connecta(String host, int port) {
        try {
            // Obrim el socket al servidor
            socket = new Socket(host, port);
            System.out.println("Conectat al servidor: " + host + ":" + port);

            // Creem els streams de sortida i entrada
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            System.err.println("Error al connectar al servidor: " + e.getMessage());
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            // Enviamos el missatge al servidor
            outputStream.writeObject(missatge);
            outputStream.flush(); // Assegurem que el missatge s'enviï immediatament
        } catch (IOException e) {
            System.err.println("Error al enviar missatge: " + e.getMessage());
        }
    }

    public void tancarClient() {
        try {
            // Tanquem els streams i el socket
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("Client desconectat.");
        } catch (IOException e) {
            System.err.println("Error al tancar el client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try {
            // Connectem al servidor
            client.connecta(HOST, PORT);

            // Creem el fil per enviar missatges al servidor
            FilLectorCX filLectorCX = new FilLectorCX(client.outputStream);
            filLectorCX.start();

            // Scanner per llegir missatges des de la consola
            Scanner scanner = new Scanner(System.in);

            try {
                // Llegeix missatges des de la consola fins que es digui "sortir"
                String message;
                while ((message = scanner.nextLine()) != null) {
                    if (message.equalsIgnoreCase(MSG_SORTIR)) {
                        break; // Sortim del bucle si s'ingressa "sortir"
                    }
                    client.enviarMissatge(message); // Enviem el missatge al servidor
                }
            } finally {
                // Tanquem el Scanner
                scanner.close();

                // Esperem a que el fil acabi
                try {
                    filLectorCX.join();
                } catch (InterruptedException e) {
                    System.err.println("Error al esperar al fil: " + e.getMessage());
                }

                // Tanquem el client
                client.tancarClient();
            }
        } catch (Exception e) {
            System.err.println("Error en el client: " + e.getMessage());
        }
    }
}