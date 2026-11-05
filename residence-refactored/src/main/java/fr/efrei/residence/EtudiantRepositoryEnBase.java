package fr.efrei.residence;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Étudiants conservés en mémoire, chaque accès étant journalisé comme une requête SQL. */
public class EtudiantRepositoryEnBase implements EtudiantRepository {

    private final ConnexionBase connexion;
    private final Map<String, Etudiant> parEmail = new LinkedHashMap<>();

    public EtudiantRepositoryEnBase(ConnexionBase connexion) {
        this.connexion = connexion;
    }

    @Override
    public void sauvegarder(Etudiant e) {
        connexion.executer("INSERT INTO etudiant (id, nom, email, profil) VALUES ('" + e.getId() + "', '" + e.getNom()
                + "', '" + e.getEmail() + "', '" + e.getProfil() + "')");
        parEmail.put(e.getEmail(), e);
    }

    @Override
    public Optional<Etudiant> trouverParEmail(String email) {
        connexion.executer("SELECT * FROM etudiant WHERE email = '" + email + "'");
        return Optional.ofNullable(parEmail.get(email));
    }

    @Override
    public Optional<Etudiant> trouverParId(String id) {
        connexion.executer("SELECT * FROM etudiant WHERE id = '" + id + "'");
        return parEmail.values().stream().filter(e -> e.getId().equals(id)).findFirst();
    }
}
