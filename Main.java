public class Main {
    static final String[] CARTE = {
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

    static final int NOMBRE_DE_PAS = 10;
    static final double DUREE_D_UN_PAS = 0.1;

    public static void main(String[] args) {
        Scene sceneDepuisCarte = ChargeurDeScene.depuisGrille(CARTE);
        System.out.println("Carte : " + sceneDepuisCarte.entites.size() + " entités, monde " + sceneDepuisCarte.monde);
        afficherGrille(sceneDepuisCarte);

        Entite joueur = sceneDepuisCarte.joueurs().get(0);
        System.out.println("Joueur : id=" + joueur.identifiant + " position=(" + joueur.x + ", " + joueur.y + ")\n");

        Scene sceneAleatoire = GenerateurDeScene.generer(
                1000, 500,
                GenerateurDeScene.Repartition.REGROUPEE,
                GenerateurDeScene.Tailles.VARIABLES,
                3.0,
                5.0,
                1L);

        Entite premiereEntite = sceneAleatoire.entites.get(0);
        System.out.printf("Scène aléatoire : %d entités. Entité 0 avant : (%.2f, %.2f)%n",
                sceneAleatoire.entites.size(), premiereEntite.x, premiereEntite.y);

        for (int pas = 0; pas < NOMBRE_DE_PAS; pas++) {
            sceneAleatoire.avancer(DUREE_D_UN_PAS);
        }
        System.out.printf("Entité 0 après %d pas : (%.2f, %.2f)%n",
                NOMBRE_DE_PAS, premiereEntite.x, premiereEntite.y);
    }

    static void afficherGrille(Scene scene) {
        for (int[] ligne : scene.grille) {
            StringBuilder texte = new StringBuilder();
            for (int casePleine : ligne) {
                texte.append(caractereDe(casePleine));
            }
            System.out.println(texte);
        }
    }

    static char caractereDe(int typeDeCase) {
        if (typeDeCase == Entite.OBSTACLE) return '#';
        if (typeDeCase == Entite.JOUEUR) return 'P';
        return '.';
    }
}