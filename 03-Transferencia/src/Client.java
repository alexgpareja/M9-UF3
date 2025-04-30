import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public void rebreFixers() throws IOException, ClassNotFoundException {
        // Demanem el nom del fitxer a rebre
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
        String nomFitxer = reader.readLine();

        // Si el nom és "sortir", tanquem la connexió
        if ("sortir".equalsIgnoreCase(nomFitxer)) {
            System.out.println("Sortint...");
            return;
        }

        // Enviem el nom del fitxer al servidor
        out.writeObject(nomFitxer);
        out.flush();

        // Rebre el contingut del fitxer
        byte[] contingut = (byte[]) in.readObject();

        if (contingut != null) {
            File file = new File(DIR_ARRIBADA, new File(nomFitxer).getName());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(contingut);
                System.out.println("Nom del fitxer a guardar: " + file.getAbsolutePath());
                System.out.println("Fitxer rebut i guardat com: " + file.getAbsolutePath());
            }
        } else {
            System.out.println("El servidor no ha pogut trobar el fitxer.");
        }
    }

    public static void main(String[] args) {

        Client client = new Client();

        try {
            // Connexió al servidor
            client.connectar();

            // Rebre fitxers
            client.rebreFixers();

            // Tancar connexió
            client.tancarConnexio();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
