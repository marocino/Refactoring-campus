# Diagnostic du code historique

Relevé fait sur `residence-legacy` avant tout refactoring.

## Défauts → emplacement → principe violé

| # | Défaut | Emplacement | Principe |
|---|---|---|---|
| 1 | Une seule classe fait l'inscription, la validation, l'ajout de logements, l'attribution, la solvabilité, le cycle de vie du bail, les pénalités, les relances, le rapport et les notifications | `GestionResidence` (421 lignes) | **S** – responsabilité unique |
| 2 | `Bail` met en forme sa propre quittance (texte, mise en page, formatage des montants) | `Bail.genererQuittance` | **S** |
| 3 | `Database` mélange connexion, SQL et cache en mémoire ; `Utils` regroupe dates, montants, arrondi et validation d'email | `Database`, `Utils` | **S** |
| 4 | Ajouter un type de logement oblige à modifier 3 `if/switch` sur le type (loyer de base, charges, rapport) et 2 fabriques dupliquées | `CalculateurLoyer.calculerLoyer`, `calculerCharges`, `GestionResidence.genererRapport`, `Residence.initialiser`, `GestionResidence.ajouterLogement` | **O** – ouvert/fermé |
| 5 | Ajouter un profil oblige à modifier 6 endroits : remise, solvabilité, caution, frais de dossier, préavis, pénalité | `CalculateurLoyer`, `GestionResidence.attribuerLogement`, `payerLoyer`, `donnerPreavis` | **O** |
| 6 | Ajouter une option oblige à ajouter un booléen dans `Bail`, un paramètre dans `calculerLoyer` et `attribuerLogement`, une ligne dans `listerOptions` | `Bail`, `CalculateurLoyer`, `GestionResidence.attribuerLogement` | **O** |
| 7 | Ajouter un canal oblige à modifier `notifier` et `relancerImpayes` (deux `switch` différents) | `GestionResidence.notifier`, `relancerImpayes` | **O** |
| 8 | `Colocation` ne peut pas remplacer un `Logement` : `loger()` / `liberer()` lèvent `UnsupportedOperationException`, d'où les `instanceof Colocation` | `Colocation`, `GestionResidence.attribuerLogement`, `sortir`, `annulerBail` | **L** – substitution de Liskov |
| 9 | `getSurfaceBalcon()` lève une exception pour `Studio` et `Colocation`, renvoie 0 pour `T1` ; `getColocataires()` renvoie `null` pour `T1` : contrats incohérents d'une sous-classe à l'autre | `Studio`, `T1`, `T2`, `Colocation` ; `catch` vide dans `genererRapport` | **L** |
| 10 | `ILogement` impose à tous les logements les méthodes de colocation, de balcon et d'entretien | `ILogement` | **I** – ségrégation des interfaces |
| 11 | `Studio`, `T1`, `T2` implémentent des méthodes qui n'ont pas de sens pour eux (`ajouterColocataire`, `nettoyerPartiesCommunes`) | `Studio`, `T1`, `T2` | **I** |
| 12 | `GestionResidence` dépend de classes concrètes qu'elle instancie elle-même : `Database.getInstance()`, `new CalculateurLoyer()`, `new SmsService()`, `new CourrierService()`, appels statiques à `EmailService` | `GestionResidence` | **D** – inversion des dépendances |
| 13 | État global modifiable : `Database.etudiants/baux`, `Residence.logements`, `EmailService.journal`, `SmsService.compteurSms`, `CourrierService.totalAffranchissement`, `Etudiant.compteur`, `GestionResidence.compteurBail` | plusieurs classes | **D** (et testabilité) |

## Autres défauts (hors SOLID)

| Défaut | Emplacement |
|---|---|
| Codes `String` pour le type, le profil, le canal, le statut ; options en 5 booléens | partout |
| Nombres magiques (14, 350, 1.15, 0.95, 45, 0.05, 30…) | `CalculateurLoyer`, `GestionResidence` |
| Champs publics mutables (`Bail.soldeDu`, `Bail.paiements`, `Paiement.*`) | `Bail`, `Paiement` |
| SQL construit par concaténation (injection) | `Database` |
| Erreurs signalées par `null` + `System.out` | `GestionResidence` |
| Exception avalée (`catch (Exception e) { return false; }`) | `EmailService.envoyer` |

## Avancement

| Palier | État |
|---|---|
| 0 – Mise en place | fait |
| 1 – Enums et constantes | fait |
| 2 – Factory | à faire |
| 3 – Strategy | à faire |
| 4 – Decorator | à faire |
| 5 à 7 | non traités |
