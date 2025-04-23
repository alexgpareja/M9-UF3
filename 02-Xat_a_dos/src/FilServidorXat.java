import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class FilServidorXat extends Thread {
    
    private Socket clientSocket;
    private ObjectInputStream inputStream;

    public FilServidorXat(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (Exception e) {
            System.err.println("Error al crear el ObjectInputStream: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Recibe mensajes del cliente hasta que reciba MSG_SORTIR
            String message;
            while ((message = (String) inputStream.readObject()) != null) {
                System.out.println("Mensaje del cliente: " + message);
                if (message.equalsIgnoreCase(ServidorXat.MSG_SORTIR)) {
                    break; // Sale del bucle si el cliente envía "sortir"
                }
            }

        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Error en la recepción de mensajes: " + e.getMessage());
        } finally {
            try {
                // Cierra la conexión con el cliente
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
