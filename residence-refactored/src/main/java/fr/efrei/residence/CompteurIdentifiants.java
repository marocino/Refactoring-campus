package fr.efrei.residence;

/** Produit des identifiants successifs (ETU-1, ETU-2… ou BAIL-001, BAIL-002…) à partir d'un format. */
public class CompteurIdentifiants {

    private final String format;
    private int dernier = 0;

    /** @param format format {@link String#format} recevant le numéro, ex. {@code "BAIL-%03d"} */
    public CompteurIdentifiants(String format) {
        this.format = format;
    }

    public String suivant() {
        dernier++;
        return String.format(format, dernier);
    }
}
