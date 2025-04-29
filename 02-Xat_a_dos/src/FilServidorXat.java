import java.io.*;

class FilServidorXat extends Thread {

    private ObjectInputStream inputStream; // Stream d'entrada per rebre missatges del client

    // Constructor que rep un ObjectInputStream
    public FilServidorXat(ObjectInputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            // Rep missatges del client fins a rebre MSG_SORTIR
            String message;
            while ((message = (String) inputStream.readObject()) != null) {
                System.out.println("Missatge del client: " + message);
                if (message.equalsIgnoreCase(ServidorXat.MSG_SORTIR)) {
                    break;
                }
            }

        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Error al rebre el missatge del client: " + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                System.err.println("Error al tancar el stream d'entrada: ".concat(e.getMessage()));
            }
        }
    }
}