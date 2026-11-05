package fr.efrei.residence;

/** Couche qui modifie le loyer qu'elle enveloppe. */
public abstract class DecorateurLoyer implements Loyer {

    protected final Loyer loyer;

    protected DecorateurLoyer(Loyer loyer) {
        this.loyer = loyer;
    }
}
