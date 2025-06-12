package snake.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Food {
    private final Board board;
    private final Pictures pictures;
    private final int fruitCount;
    public List<Point> positions = new ArrayList<>();
    private final List<Integer> types = new ArrayList<>();

    public static final int GOLDEN_APPLE_INDEX = 3;
    private float fruitScale = 1.0f;
    private float scaleDirection = 0.09f; // szybka animacja
    private Obstacle obstacle; // przeszkody
    private final List<Snake> snakes;

    public Food(Board board, Pictures pictures, Obstacle obstacle, int fruitCount, List<Snake> snakes) {
        this.board = board;
        this.pictures = pictures;
        this.obstacle = obstacle;
        this.fruitCount = fruitCount;
        this.snakes = new ArrayList<>(snakes);
        regenerate();
    }

    public Food(Board board, Pictures pictures, Obstacle obstacle, int fruitCount) {
        this(board, pictures, obstacle, fruitCount, List.of());
    }

    public Point getRandomPos() {
        Random random = new Random();
        Point p;
        do {
            int x = random.nextInt(board.getCellCount());
            int y = random.nextInt(board.getCellCount());
            p = new Point(x, y);
        } while (obstacle.getObstacles().contains(p) || positions.contains(p) || onSnake(p)); // unika kolizji z przeszkodą, wężami i innym owocem
        return p;
    }

    private boolean onSnake(Point p) {
        for (Snake s : snakes) {
            if (!s.isAlive()) continue;
            for (Point seg : s.getTail()) {
                if (seg.equals(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int randomFruit() {
        Random rand = new Random();
        return rand.nextInt(pictures.getFruitCount());
    }

    public void regenerate() {
        positions.clear();
        types.clear();
        for (int i = 0; i < fruitCount; i++) {
            positions.add(getRandomPos());
            types.add(randomFruit());
        }
    }

    public void replace(Point eaten) {
        int idx = positions.indexOf(eaten);
        if (idx != -1) {
            positions.set(idx, getRandomPos());
            types.set(idx, randomFruit());
        }
    }


    public int getType(int index) {
        return types.get(index);
    }

    public void draw(Graphics2D g) {
        int baseSize = board.getCellSize();
        int scaledSize = (int) (baseSize * fruitScale);
        int offset = (baseSize - scaledSize) / 2;

        for (int i = 0; i < positions.size(); i++) {
            Point p = positions.get(i);
            int drawX = p.x * baseSize + offset;
            int drawY = p.y * baseSize + offset;
            int type = types.get(i);
            pictures.drawFruit(g, drawX, drawY, scaledSize, scaledSize, type);
        }
    }

    public void updateAnimation() {
        fruitScale += scaleDirection;
        if (fruitScale >= 1.4f || fruitScale <= 1.0f) {
            scaleDirection *= -1;
        }
    }
}
