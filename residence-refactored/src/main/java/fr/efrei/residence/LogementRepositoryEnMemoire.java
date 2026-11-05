package fr.efrei.residence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Parc de logements conservé en mémoire, dans l'ordre d'ajout. */
public class LogementRepositoryEnMemoire implements LogementRepository {

    private final List<Logement> logements = new ArrayList<>();

    @Override
    public void ajouter(Logement logement) {
        logements.add(logement);
    }

    @Override
    public Optional<Logement> trouver(String numero) {
        return logements.stream().filter(l -> l.getNumero().equals(numero)).findFirst();
    }

    @Override
    public List<Logement> tous() {
        return Collections.unmodifiableList(logements);
    }
}
