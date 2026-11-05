package fr.efrei.residence;

/** Relance, sur leur canal préféré, les étudiants dont un bail non annulé présente un impayé. */
public class ServiceRelances {

    private final BailRepository baux;
    private final RepertoireCanaux canaux;

    public ServiceRelances(BailRepository baux, RepertoireCanaux canaux) {
        this.baux = baux;
        this.canaux = canaux;
    }

    public void relancerImpayes() {
        System.out.println("--- Relance des impayés");
        for (Bail b : baux.tous()) {
            if (b.getSoldeDu() <= 0) continue;
            if (b.getStatut() == StatutBail.ANNULE) continue;
            Etudiant e = b.getEtudiant();
            canaux.pour(e.getCanalPrefere()).relancerImpaye(e, b.getId(), b.getSoldeDu());
        }
    }
}
