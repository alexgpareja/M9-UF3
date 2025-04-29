import java.io.*;
import java.util.Scanner;

class FilLectorCX extends Thread {
    private ObjectOutputStream outputStream;

    // Constructor que rep el ObjectOutputStream
    public FilLectorCX(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        try {
            // Crea un Scanner para leer mensajes desde la consola
            Scanner scanner = new Scanner(System.in);

            // Lee mensajes desde la consola y los envía al servidor
            String message;
            while ((message = scanner.nextLine()) != null) {
                if (message.equalsIgnoreCase(ServidorXat.MSG_SORTIR)) {
                    break; // Sale del bucle si se ingresa "sortir"
                }
                // Envía el mensaje al servidor
                outputStream.writeObject(message);
                outputStream.flush();
                System.out.println("Mensaje enviado: " + message);
            }

            // Cierra el Scanner
            scanner.close();

        } catch (IOException e) {
            System.err.println("Error al enviar mensajes: " + e.getMessage());
        }
    }
}