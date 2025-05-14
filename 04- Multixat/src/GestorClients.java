import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients implements Runnable {
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir;

    public GestorClients(Socket client, ServidorXat servidor) {
        this.client = client;
        this.servidor = servidor;
        this.sortir = false;

        try {
            // Inicialitzar streams
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.err.println("Error inicialitzant streams: " + e.getMessage());
        }
    }

    public String getNom() {
        return nom;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            do {
                missatge = (String) ois.readObject();
                processaMissatge(missatge);
            } while (!sortir);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en la comunicació amb el client: " + e.getMessage());
            if (nom != null) {
                servidor.eliminarClient(nom);
            }
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.err.println("Error tancant socket: " + e.getMessage());
            }
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            if (oos != null) {
                oos.writeObject(missatge);
                oos.flush();
            }
        } catch (IOException e) {
            System.err.println("Error enviant missatge a " + nom + ": " + e.getMessage());
        }
    }

    public void processaMissatge(String missatge) {
        String codi = Missatge.getCodiMissatge(missatge);
        if (codi == null)
            return;

        String[] parts = Missatge.getPartsMissatge(missatge);

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                if (parts.length >= 2) {
                    nom = parts[1];
                    servidor.afegirClient(this);
                }
                break;

            case Missatge.CODI_SORTIR_CLIENT:
                if (nom != null) {
                    sortir = true;
                    servidor.eliminarClient(nom);
                }
                break;

            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;

            case Missatge.CODI_MSG_PERSONAL:
                if (parts.length >= 3) {
                    String destinatari = parts[1];
                    String contingutMissatge = parts[2];
                    servidor.enviarMissatgePersonal(destinatari, nom, contingutMissatge);
                }
                break;

            case Missatge.CODI_MSG_GRUP:
                if (parts.length >= 2) {
                    String contingutMissatge = parts[1];
                    servidor.enviarMissatgeGrup(nom + ": " + contingutMissatge);
                }
                break;

            default:
                System.err.println("Error: codi de missatge desconegut");
                break;
        }
    }
}