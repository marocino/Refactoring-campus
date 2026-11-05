package fr.efrei.residence;

import java.util.Optional;

/** Persistance des étudiants inscrits. */
public interface EtudiantRepository {

    void sauvegarder(Etudiant etudiant);

    Optional<Etudiant> trouverParEmail(String email);

    Optional<Etudiant> trouverParId(String id);
}
