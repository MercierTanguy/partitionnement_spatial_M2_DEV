public class Main {
    static final String[] GRILLE = {
            "11111111111111111111",
            "10000000000000000001",
            "10011100000001100001",
            "10010000000000000001",
            "10010000020000011101",
            "10000000000000000001",
            "10000110000000000001",
            "10000010000111000001",
            "10000000000000000001",
            "11111111111111111111"
    };

    static final int NOMBRE_ETAPES = 10;
    static final double DUREE_ETAPE = 0.1;

    public static void main(String[] arguments) {
        
        int nombreEntites = 20;
        int sizeWorld = 500;

        Scene sceneChargee = LecteurGrille.lireGrille(GRILLE);
        System.out.println("Carte : " + sceneChargee.entites.size()
                + " entités, monde " + sceneChargee.monde);
        afficherGrille(sceneChargee);

        Entite joueur = sceneChargee.joueurs().get(0);
        System.out.println("Joueur : id=" + joueur.id
                + " position=(" + joueur.positionX + ", " + joueur.positionY + ")\n");

        Scene niveauAleatoire = GenerateurNiveau.generer(
                nombreEntites, sizeWorld,
                GenerateurNiveau.Repartition.REGROUPEE,
                GenerateurNiveau.Tailles.VARIABLES,
                3.0,
                5.0,
                1L);

        Entite premiereEntite = niveauAleatoire.entites.get(0);
        System.out.printf("Niveau aléatoire : %d entités. Entité 0 avant : (%.2f, %.2f)%n",
                niveauAleatoire.entites.size(),
                premiereEntite.positionX,
                premiereEntite.positionY);

        for (int etape = 0; etape < NOMBRE_ETAPES; etape++) {
            niveauAleatoire.avancer(DUREE_ETAPE);
        }

        System.out.printf("Entité 0 après %d étapes : (%.2f, %.2f)%n",
                NOMBRE_ETAPES,
                premiereEntite.positionX,
                premiereEntite.positionY);

            
        try {
        MethodeBrute.executer(
            sceneChargee,
            joueur.positionX,
            joueur.positionY,
            nombreEntites
        );

        System.out.println(
            "Rapport genere : rapport_force_brute.txt");

        } catch (java.io.IOException e) {
        System.err.println(
            "Erreur lors de la generation du rapport : "
            + e.getMessage());
        }
    }

    static void afficherGrille(Scene scene) {
        for (int[] ligne : scene.grille) {
            StringBuilder texte = new StringBuilder();

            for (int typeCase : ligne) {
                texte.append(caractereDe(typeCase));
            }

            System.out.println(texte);
        }
    }

    static char caractereDe(int typeCase) {
        if (typeCase == Entite.OBSTACLE) return '#';
        if (typeCase == Entite.JOUEUR) return 'P';
        return '.';
    }
}