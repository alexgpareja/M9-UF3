import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat implements Runnable {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir;
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    public ClientXat() {
        this.sortir = false;
    }

    public void connecta() {
        try {
            socket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);

            // Inicialitzar ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.err.println("Error connectant al servidor: " + e.getMessage());
            System.exit(1);
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            if (oos != null) {
                oos.writeObject(missatge);
                oos.flush();
                System.out.println("Enviant missatge: " + missatge);
            } else {
                System.out.println("oos null. Sortint...");
            }
        } catch (IOException e) {
            System.err.println("Error enviant missatge: " + e.getMessage());
        }
    }

    public void tancarClient() {
        try {
            System.out.println("Tancant client...");
            if (ois != null) {
                ois.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (oos != null) {
                oos.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error tancant client: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");

            while (!sortir) {
                String missatgeCru = (String) ois.readObject();
                String codi = Missatge.getCodiMissatge(missatgeCru);

                if (codi == null)
                    continue;

                String[] parts = Missatge.getPartsMissatge(missatgeCru);

                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        break;

                    case Missatge.CODI_MSG_PERSONAL:
                        if (parts.length >= 3) {
                            String remitent = parts[1];
                            String missatge = parts[2];
                            System.out.println("Missatge de (" + remitent + "): " + missatge);
                        }
                        break;

                    case Missatge.CODI_MSG_GRUP:
                        if (parts.length >= 2) {
                            String missatge = parts[1];
                            System.out.println(missatge);
                        }
                        break;

                    default:
                        System.err.println("Error: codi de missatge desconegut");
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!sortir) {
                System.err.println("Error rebent missatge. Sortint...");
            }
        } finally {
            tancarClient();
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner sc, String missatge, boolean obligatori) {
        String linea = "";
        do {
            System.out.print(missatge);
            linea = sc.nextLine().trim();

            if (linea.isEmpty() && obligatori) {
                System.out.println("El camp és obligatori. Torna a provar.");
            } else {
                break;
            }
        } while (obligatori);

        return linea;
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();

        // Iniciar fil de lectura
        Thread threadLectura = new Thread(client);
        threadLectura.start();

        client.ajuda();

        Scanner sc = new Scanner(System.in);
        String linea;

        while (!client.sortir) {
            linea = sc.nextLine().trim();

            if (linea.isEmpty()) {
                client.sortir = true;
                // Enviar missatge de sortida
                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                continue;
            }

            switch (linea) {
                case "1":
                    String nom = client.getLinea(sc, "Introdueix el nom: ", true);
                    client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                    break;

                case "2":
                    String destinatari = client.getLinea(sc, "Destinatari:: ", true);
                    String msgPersonal = client.getLinea(sc, "Missatge a enviar: ", true);
                    client.enviarMissatge(Missatge.getMissatgePersonal(destinatari, msgPersonal));
                    break;

                case "3":
                    String msgGrup = client.getLinea(sc, "Missatge a enviar: ", true);
                    client.enviarMissatge(Missatge.getMissatgeGrup(msgGrup));
                    break;

                case "4":
                    client.sortir = true;
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                    break;

                case "5":
                    client.sortir = true;
                    client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    break;

                default:
                    System.out.println("Opció no vàlida.");
                    break;
            }

            client.ajuda();
        }

        sc.close();
        client.tancarClient();
        System.exit(0);
    }
}