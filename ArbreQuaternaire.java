import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Quadtree 2D pour des entites circulaires de taille variable.
 *
 * Une entite est rangee dans le noeud le plus profond dont les limites
 * englobent entierement ses propres limites (AABB). Une grosse entite a cheval
 * sur une frontiere reste donc dans un noeud interne : c'est ce qui gere
 * naturellement les tailles tres variables.
 *
 * Utilise uniquement les classes existantes : Entite et LimitesSpatiales.
 */
public class ArbreQuaternaire {

    static final class Noeud {
        final LimitesSpatiales limites;
        final int profondeur;
        final ArrayList<Entite> entites = new ArrayList<>();
        Noeud[] enfants; // null tant que le noeud est une feuille

        Noeud(LimitesSpatiales limites, int profondeur) {
            this.limites = limites;
            this.profondeur = profondeur;
        }

        Noeud enfantQuiEnglobe(LimitesSpatiales boite) {
            for (Noeud enfant : enfants) {
                if (enfant.limites.englobe(boite)) {
                    return enfant;
                }
            }
            return null;
        }
    }

    private Noeud racine;
    private final int capacite;
    private final int profondeurMaximale;

    // Entite -> noeud qui la contient (evite de modifier la classe Entite)
    private final IdentityHashMap<Entite, Noeud> noeudDe = new IdentityHashMap<>();

    private long testsDeCollision; // paires reellement testees lors du dernier appel a collisions()

    public ArbreQuaternaire(LimitesSpatiales monde, int capacite, int profondeurMaximale) {
        if (monde == null) {
            throw new IllegalArgumentException("Les limites du monde sont obligatoires.");
        }
        this.racine = new Noeud(monde, 0);
        this.capacite = capacite;
        this.profondeurMaximale = profondeurMaximale;
    }

    // ------------------------------------------------------------------
    // Construction et mise a jour
    // ------------------------------------------------------------------

    public void inserer(Entite entite) {
        LimitesSpatiales boite = entite.calculerLimites();
        Noeud noeud = racine;

        while (true) {
            if (noeud.enfants == null) {
                ajouter(noeud, entite);
                if (noeud.entites.size() > capacite && noeud.profondeur < profondeurMaximale) {
                    diviser(noeud);
                }
                return;
            }

            Noeud enfant = noeud.enfantQuiEnglobe(boite);
            if (enfant == null) { // chevauche une frontiere : reste dans ce noeud
                ajouter(noeud, entite);
                return;
            }
            noeud = enfant;
        }
    }

    public void retirer(Entite entite) {
        Noeud noeud = noeudDe.remove(entite);
        if (noeud != null) {
            noeud.entites.remove(entite);
        }
    }

    /**
     * A appeler apres un deplacement. Ne fait rien si l'entite est toujours
     * correctement placee, sinon la retire et la reinsere.
     */
    public void mettreAJour(Entite entite) {
        Noeud noeud = noeudDe.get(entite);

        if (noeud != null) {
            LimitesSpatiales boite = entite.calculerLimites();
            boolean toujoursDansSonNoeud = noeud.limites.englobe(boite);
            boolean descendraitPlusBas = noeud.enfants != null && noeud.enfantQuiEnglobe(boite) != null;

            if (toujoursDansSonNoeud && !descendraitPlusBas) {
                return;
            }
            retirer(entite);
        }
        inserer(entite);
    }

    public void reconstruire(Collection<Entite> entites) {
        racine = new Noeud(racine.limites, 0);
        noeudDe.clear();
        for (Entite entite : entites) {
            inserer(entite);
        }
    }

    private void ajouter(Noeud noeud, Entite entite) {
        noeud.entites.add(entite);
        noeudDe.put(entite, noeud);
    }

    private void diviser(Noeud noeud) {
        LimitesSpatiales l = noeud.limites;
        double milieuX = (l.minimumX + l.maximumX) / 2;
        double milieuY = (l.minimumY + l.maximumY) / 2;
        int p = noeud.profondeur + 1;

        noeud.enfants = new Noeud[] {
            new Noeud(new LimitesSpatiales(l.minimumX, l.minimumY, milieuX, milieuY), p),
            new Noeud(new LimitesSpatiales(milieuX, l.minimumY, l.maximumX, milieuY), p),
            new Noeud(new LimitesSpatiales(l.minimumX, milieuY, milieuX, l.maximumY), p),
            new Noeud(new LimitesSpatiales(milieuX, milieuY, l.maximumX, l.maximumY), p)
        };

        ArrayList<Entite> anciennes = new ArrayList<>(noeud.entites);
        noeud.entites.clear();

        for (Entite entite : anciennes) {
            Noeud enfant = noeud.enfantQuiEnglobe(entite.calculerLimites());
            ajouter(enfant != null ? enfant : noeud, entite);
        }

        // Si tout est tombe dans un seul enfant, on continue a diviser
        for (Noeud enfant : noeud.enfants) {
            if (enfant.entites.size() > capacite && enfant.profondeur < profondeurMaximale) {
                diviser(enfant);
            }
        }
    }

    // ------------------------------------------------------------------
    // Requetes
    // ------------------------------------------------------------------

    /** Entites dont le disque touche le rectangle donne. */
    public List<Entite> requeteRectangle(LimitesSpatiales zone) {
        List<Entite> resultat = new ArrayList<>();
        ArrayDeque<Noeud> pile = new ArrayDeque<>();
        pile.push(racine);

        while (!pile.isEmpty()) {
            Noeud noeud = pile.pop();
            if (!noeud.limites.chevauche(zone)) {
                continue;
            }
            for (Entite entite : noeud.entites) {
                double distanceCarree = zone.calculerDistanceCarree(entite.positionX, entite.positionY);
                if (distanceCarree <= entite.rayon * entite.rayon) {
                    resultat.add(entite);
                }
            }
            if (noeud.enfants != null) {
                for (Noeud enfant : noeud.enfants) {
                    pile.push(enfant);
                }
            }
        }
        return resultat;
    }

    /** Entites dont le disque touche le cercle (centreX, centreY, rayonZone). */
    public List<Entite> requeteCercle(double centreX, double centreY, double rayonZone) {
        List<Entite> resultat = new ArrayList<>();
        ArrayDeque<Noeud> pile = new ArrayDeque<>();
        pile.push(racine);

        while (!pile.isEmpty()) {
            Noeud noeud = pile.pop();
            if (noeud.limites.calculerDistanceCarree(centreX, centreY) > rayonZone * rayonZone) {
                continue;
            }
            for (Entite entite : noeud.entites) {
                double rayonTotal = entite.rayon + rayonZone;
                if (entite.calculerDistanceCarree(centreX, centreY) <= rayonTotal * rayonTotal) {
                    resultat.add(entite);
                }
            }
            if (noeud.enfants != null) {
                for (Noeud enfant : noeud.enfants) {
                    pile.push(enfant);
                }
            }
        }
        return resultat;
    }

    private static final class Candidat {
        final double distanceCarree;
        final Noeud noeud;
        final Entite entite;

        Candidat(double distanceCarree, Noeud noeud, Entite entite) {
            this.distanceCarree = distanceCarree;
            this.noeud = noeud;
            this.entite = entite;
        }
    }

    /**
     * k plus proches voisins du point (distance au centre, comme la force brute).
     * Parcours "best-first" : on depile toujours le noeud ou l'entite la plus proche.
     */
    public List<Entite> kPlusProches(double pointX, double pointY, int k) {
        List<Entite> resultat = new ArrayList<>();
        if (k <= 0) {
            return resultat;
        }

        PriorityQueue<Candidat> file = new PriorityQueue<>(
                Comparator.comparingDouble((Candidat c) -> c.distanceCarree));
        file.add(new Candidat(0, racine, null));

        while (!file.isEmpty() && resultat.size() < k) {
            Candidat candidat = file.poll();

            if (candidat.entite != null) {
                resultat.add(candidat.entite);
                continue;
            }

            Noeud noeud = candidat.noeud;
            for (Entite entite : noeud.entites) {
                file.add(new Candidat(entite.calculerDistanceCarree(pointX, pointY), null, entite));
            }
            if (noeud.enfants != null) {
                for (Noeud enfant : noeud.enfants) {
                    file.add(new Candidat(enfant.limites.calculerDistanceCarree(pointX, pointY), enfant, null));
                }
            }
        }
        return resultat;
    }

    /** Toutes les paires d'entites en collision (meme test que la force brute). */
    public List<Entite[]> collisions() {
        List<Entite[]> resultat = new ArrayList<>();
        testsDeCollision = 0;
        collisions(racine, new ArrayList<>(), resultat);
        return resultat;
    }

    private void collisions(Noeud noeud, ArrayList<List<Entite>> ancetres, List<Entite[]> resultat) {
        List<Entite> entites = noeud.entites;

        for (int i = 0; i < entites.size(); i++) {
            Entite a = entites.get(i);

            for (int j = i + 1; j < entites.size(); j++) {
                Entite b = entites.get(j);
                testsDeCollision++;
                if (a.estEnCollisionAvec(b)) {
                    resultat.add(new Entite[] { a, b });
                }
            }
            // Entites rangees dans les noeuds ancetres (grosses entites a cheval)
            for (List<Entite> ancetre : ancetres) {
                for (Entite b : ancetre) {
                    testsDeCollision++;
                    if (a.estEnCollisionAvec(b)) {
                        resultat.add(new Entite[] { a, b });
                    }
                }
            }
        }

        if (noeud.enfants != null) {
            boolean ajoute = !entites.isEmpty();
            if (ajoute) {
                ancetres.add(entites);
            }
            for (Noeud enfant : noeud.enfants) {
                collisions(enfant, ancetres, resultat);
            }
            if (ajoute) {
                ancetres.remove(ancetres.size() - 1);
            }
        }
    }

    // ------------------------------------------------------------------
    // Statistiques et visualisation
    // ------------------------------------------------------------------

    public long obtenirTestsDeCollision() {
        return testsDeCollision;
    }

    public int compterNoeuds() {
        int total = 0;
        ArrayDeque<Noeud> pile = new ArrayDeque<>();
        pile.push(racine);
        while (!pile.isEmpty()) {
            Noeud noeud = pile.pop();
            total++;
            if (noeud.enfants != null) {
                for (Noeud enfant : noeud.enfants) {
                    pile.push(enfant);
                }
            }
        }
        return total;
    }

    public int calculerProfondeur() {
        int maximum = 0;
        ArrayDeque<Noeud> pile = new ArrayDeque<>();
        pile.push(racine);
        while (!pile.isEmpty()) {
            Noeud noeud = pile.pop();
            maximum = Math.max(maximum, noeud.profondeur);
            if (noeud.enfants != null) {
                for (Noeud enfant : noeud.enfants) {
                    pile.push(enfant);
                }
            }
        }
        return maximum;
    }

    /** Limites de tous les noeuds, pour dessiner l'arbre. */
    public List<LimitesSpatiales> limitesDesNoeuds() {
        List<LimitesSpatiales> resultat = new ArrayList<>();
        ArrayDeque<Noeud> pile = new ArrayDeque<>();
        pile.push(racine);
        while (!pile.isEmpty()) {
            Noeud noeud = pile.pop();
            resultat.add(noeud.limites);
            if (noeud.enfants != null) {
                for (Noeud enfant : noeud.enfants) {
                    pile.push(enfant);
                }
            }
        }
        return resultat;
    }
}
