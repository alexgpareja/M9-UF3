import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

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

    public String getNom(ObjectInputStream input, ObjectOutputStream output) {
        try {
            // Enviem un missatge al client demanant el seu nom
            output.writeObject("Hola! Introdueix el teu nom:");
            output.flush(); // Assegurem que el missatge s'enviï immediatament

            // Llegim el nom del client des del stream d'entrada
            Object nomObject = input.readObject();
            if (nomObject instanceof String) {
                return (String) nomObject; // Retornem el nom com a cadena
            } else {
                System.err.println("El client ha enviat un objecte que no és un String.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error de comunicació al llegir el nom del client: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Error: El tipus de dades rebut no és vàlid: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.iniciarServidor();
            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            // Creem els streams
            ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

            // Llegim el nom del client
            String nomClient = servidor.getNom(inputStream, outputStream);
            if (nomClient == null) {
                System.err.println("No s'ha pogut obtenir el nom del client.");
                return;
            }
            System.out.println("Nom rebut: " + nomClient);

            // Creem i iniciem el fil per rebre missatges del client
            FilServidorXat filServidorXat = new FilServidorXat(inputStream);
            filServidorXat.start();

            // Enviem missatges des de la consola fins a la sortida
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(outputStream, true);
            String message;
            while ((message = consoleReader.readLine()) != null) {
                if (message.equalsIgnoreCase(MSG_SORTIR)) {
                    break; // Sortim del bucle si s'ingressa "sortir"
                }
                out.println(message); // Enviem el missatge al client
            }

            // Esperem a que el fil acabi
            filServidorXat.join();
            System.out.println("Fil de xat finalitzat.");

        } catch (IOException | InterruptedException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        } finally {
            servidor.pararServidor();
        }
    }

}
