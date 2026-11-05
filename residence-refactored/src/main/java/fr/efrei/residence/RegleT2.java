package fr.efrei.residence;

public class RegleT2 implements RegleTypeLogement {

    static final double PRIX_M2 = 12;
    static final double FORFAIT = 100;

    @Override
    public double loyerDeBase(Logement logement) {
        return logement.getSurface() * PRIX_M2 + FORFAIT;
    }

    @Override
    public String libelleRapport() {
        return "T2           : ";
    }
}
