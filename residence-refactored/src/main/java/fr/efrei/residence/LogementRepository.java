package fr.efrei.residence;

import java.util.List;
import java.util.Optional;

/** Le parc de logements de la résidence. */
public interface LogementRepository {

    void ajouter(Logement logement);

    Optional<Logement> trouver(String numero);

    /** Tous les logements, dans l'ordre d'ajout (ordre de recherche lors d'une attribution). */
    List<Logement> tous();
}
