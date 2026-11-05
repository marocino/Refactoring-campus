package fr.efrei.residence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Décorateurs du loyer")
class DecorateursLoyerTest {

    private static final double PRECISION = 1e-9;
    private static final Loyer CENT_EUROS = () -> 100;

    @Test
    void loyer_de_base_selon_le_type() {
        assertEquals(350, new LoyerDeBase(new Studio("S", 20, 1, false), new RegleStudio()).montant(), PRECISION);
        assertEquals(440, new LoyerDeBase(new T1("T", 30, 1, false), new RegleT1()).montant(), PRECISION);
        assertEquals(640, new LoyerDeBase(new T2("D", 45, 2, false, 6), new RegleT2()).montant(), PRECISION);
        assertEquals(220, new LoyerDeBase(new Colocation("C", 80, 3, true, 4), new RegleColocation()).montant(), PRECISION);
    }

    @Test
    void meuble_majore_de_15_pourcent() {
        assertEquals(115, new MajorationMeuble(CENT_EUROS).montant(), PRECISION);
    }

    @Test
    void rez_de_chaussee_minore_de_5_pourcent() {
        assertEquals(95, new MinorationRezDeChaussee(CENT_EUROS).montant(), PRECISION);
    }

    @Test
    void menage_8_pourcent_ou_4_pourcent_en_colocation() {
        assertEquals(108, new OptionMenage(CENT_EUROS, new RegleStudio()).montant(), PRECISION);
        assertEquals(104, new OptionMenage(CENT_EUROS, new RegleColocation()).montant(), PRECISION);
    }

    @Test
    void parking_45_euros() {
        assertEquals(145, new OptionParking(CENT_EUROS).montant(), PRECISION);
    }

    @Test
    void laverie_12_euros() {
        assertEquals(112, new OptionLaverie(CENT_EUROS).montant(), PRECISION);
    }

    @Test
    void fibre_20_euros_sauf_en_colocation() {
        assertEquals(120, new OptionFibre(CENT_EUROS, new RegleT1()).montant(), PRECISION);
        assertEquals(100, new OptionFibre(CENT_EUROS, new RegleColocation()).montant(), PRECISION);
    }

    @Test
    void assurance_9_euros() {
        assertEquals(109, new OptionAssurance(CENT_EUROS).montant(), PRECISION);
    }

    @Test
    void remise_du_profil() {
        assertEquals(100, new RemiseProfil(CENT_EUROS, new RegleStandard(), 0).montant(), PRECISION);
        assertEquals(85, new RemiseProfil(CENT_EUROS, new RegleBoursier(), 3).montant(), PRECISION);
        assertEquals(70, new RemiseProfil(CENT_EUROS, new RegleBoursier(), 7).montant(), PRECISION);
        assertEquals(90, new RemiseProfil(CENT_EUROS, new RegleAlternant(), 0).montant(), PRECISION);
        assertEquals(100, new RemiseProfil(CENT_EUROS, new RegleInternational(), 0).montant(), PRECISION);
    }

    @Test
    void les_couches_se_combinent() {
        Loyer loyer = new OptionParking(new OptionMenage(new MajorationMeuble(CENT_EUROS), new RegleT1()));
        assertEquals(100 * 1.15 * 1.08 + 45, loyer.montant(), PRECISION);
    }

    @Test
    void l_assemblage_applique_le_menage_avant_les_prix_fixes_et_la_remise_en_dernier() {
        Logement b105 = new T1("B105", 30, 1, false); // base 440 €
        double loyer = new CalculateurLoyer().calculerLoyer(b105, Profil.BOURSIER, 4,
                EnumSet.of(Option.PARKING, Option.MENAGE));
        assertEquals(416.16, loyer, PRECISION); // (440 × 1,08 + 45) × 0,80
    }
}
