import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Fitxer {

    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
    }

    public byte[] getContingut() throws IOException {

        File file = new File(nom);
        // Comprobar si el fitxer existeix
        if (!file.exists()) {
            throw new IOException("El fitxer no existeix: " + nom);
        }

        // Torna carregat el contingut del fitxer en bytes
        try (FileInputStream fis = new FileInputStream(file)) {
            int length = (int) file.length();
            byte[] bytes = new byte[length];
            fis.read(bytes);
            return bytes;
        }
    }
}
    