package fr.efrei.residence;

import java.util.List;
import java.util.Optional;

/** Persistance des baux. */
public interface BailRepository {

    void sauvegarder(Bail bail);

    void mettreAJour(Bail bail);

    Optional<Bail> trouver(String id);

    /** Tous les baux, triés par identifiant. */
    List<Bail> tous();
}
