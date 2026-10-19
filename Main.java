

public class Main {


    static final String[] MAP = {
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

    public static void main(String[] args) {

        Scene map = SceneLoader.fromGrid(MAP);
        System.out.println("Carte : " + map.entities.size() + " entités, monde " + map.world);
        printGrid(map);

        Entity player = map.players().get(0);
        System.out.println("Joueur : id=" + player.id + " position=(" + player.x + ", " + player.y + ")\n");


        Scene random = SceneGenerator.generate(
                1000, 500,
                SceneGenerator.Distribution.CLUSTERED,
                SceneGenerator.Sizes.VARIABLE,
                3.0,
                5.0,
                1L);
        Entity e0 = random.entities.get(0);
        System.out.printf("Scène aléatoire : %d entités. Entité 0 avant : (%.2f, %.2f)%n",
                random.entities.size(), e0.x, e0.y);

        for (int i = 0; i < 10; i++) random.step(0.1);
        System.out.printf("Entité 0 après 10 pas : (%.2f, %.2f)%n", e0.x, e0.y);
    }

    static void printGrid(Scene s) {
        for (int[] row : s.grid) {
            StringBuilder sb = new StringBuilder();
            for (int v : row) {
                sb.append(v == Entity.OBSTACLE ? '#' : v == Entity.PLAYER ? 'P' : '.');
            }
            System.out.println(sb);
        }
    }
}