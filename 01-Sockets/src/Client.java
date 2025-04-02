import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private final int PORT = 7777;
    private final String HOST = "localhost";
    Socket clientSocket;
    PrintWriter output;

    public void connecta() {
        try {
            // Obrir la connexio
            clientSocket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);

            // Crear el PrintWriter per enviar dades al servidor
            output = new PrintWriter(clientSocket.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Error al connectar desde el client: " + e.getMessage());
        }
    }

    public void tanca() {
        try {
            // Tancar la connexio
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Connexió amb el servidor tancada.");
            }

            // Tancar el PrintWriter
            if (output != null) {
                output.close();
                System.out.println("PrintWriter tancat.");
            }
        } catch (IOException e) {
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }

    public void envia(String mensaje) {
        if (output != null) {
            output.println(mensaje);
            System.out.println("Enviat al servidor: " + mensaje);
        } else {
            System.err.println("No estàs connectat al servidor.");
        }
    }

    public static void main(String[] args) {

        Client client = new Client();
        client.connecta();

        // Enviar un missatge al servidor
        client.envia("Hola Juanma, que tal estás rey!");
        client.envia("Prova d'enviament 2 jeje");
        client.envia("Adèu, fins aviat!");

        // Esperem a que l'usuari prengui ENTER
        System.out.print("Premi ENTER per tancar...");
        try {
            System.in.read(); // Espera a que l'usuari prengui ENTER
        } catch (IOException e) {
            System.err.println("Error llegint entrada: " + e.getMessage());
        }

        // Tancar la connexió
        client.tanca();
    }
}
