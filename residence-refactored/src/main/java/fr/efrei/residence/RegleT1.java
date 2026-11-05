package fr.efrei.residence;

public class RegleT1 implements RegleTypeLogement {

    static final double PRIX_M2 = 13;
    static final double FORFAIT = 50;

    @Override
    public double loyerDeBase(Logement logement) {
        return logement.getSurface() * PRIX_M2 + FORFAIT;
    }

    @Override
    public String libelleRapport() {
        return "T1           : ";
    }
}
