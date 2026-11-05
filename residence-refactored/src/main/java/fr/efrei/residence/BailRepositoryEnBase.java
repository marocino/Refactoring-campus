package fr.efrei.residence;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Baux conservés en mémoire, chaque accès étant journalisé comme une requête SQL. */
public class BailRepositoryEnBase implements BailRepository {

    private final ConnexionBase connexion;
    private final Map<String, Bail> parId = new HashMap<>();

    public BailRepositoryEnBase(ConnexionBase connexion) {
        this.connexion = connexion;
    }

    @Override
    public void sauvegarder(Bail b) {
        connexion.executer("INSERT INTO bail (id, etudiant, logement, statut, loyer) VALUES ('" + b.getId() + "', '"
                + b.getEtudiant().getId() + "', '" + b.getLogement().getNumero() + "', '" + b.getStatut() + "', "
                + b.getLoyerChargesComprises() + ")");
        parId.put(b.getId(), b);
    }

    @Override
    public void mettreAJour(Bail b) {
        connexion.executer("UPDATE bail SET statut = '" + b.getStatut() + "', solde = " + b.getSoldeDu()
                + " WHERE id = '" + b.getId() + "'");
    }

    @Override
    public Optional<Bail> trouver(String id) {
        connexion.executer("SELECT * FROM bail WHERE id = '" + id + "'");
        return Optional.ofNullable(parId.get(id));
    }

    @Override
    public List<Bail> tous() {
        connexion.executer("SELECT * FROM bail");
        return parId.values().stream().sorted(Comparator.comparing(Bail::getId)).toList();
    }
}
