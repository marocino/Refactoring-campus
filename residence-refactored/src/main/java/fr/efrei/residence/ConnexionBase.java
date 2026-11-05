package fr.efrei.residence;

/** Connexion (simulée) à la base de la résidence : affiche chaque requête exécutée. */
public class ConnexionBase {

    static final String URL = "jdbc:postgresql://db.campusvert.local:5432/residence";

    public ConnexionBase() {
        System.out.println("[DB] Connexion à " + URL);
    }

    public void executer(String sql) {
        System.out.println("[SQL] " + sql);
    }
}
