package fr.efrei.residence;

import java.time.LocalDate;

/**
 * Un règlement de loyer.
 *
 * @param mois     mois réglé, format AAAA-MM
 * @param penalite pénalité de retard incluse dans le dû
 * @param du       loyer charges comprises + pénalité
 */
public record Paiement(String mois, LocalDate date, double montant, double penalite, double du) {

    public boolean estIncomplet() {
        return montant < du;
    }
}
