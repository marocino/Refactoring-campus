package fr.efrei.residence;

/** Dernière couche : la remise du profil porte sur tout le reste. */
public class RemiseProfil extends DecorateurLoyer {

    private final RegleProfil regle;
    private final int echelonBourse;

    public RemiseProfil(Loyer loyer, RegleProfil regle, int echelonBourse) {
        super(loyer);
        this.regle = regle;
        this.echelonBourse = echelonBourse;
    }

    @Override
    public double montant() {
        return regle.appliquerRemise(loyer.montant(), echelonBourse);
    }
}
