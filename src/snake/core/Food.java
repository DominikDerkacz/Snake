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
    private float fruitScale = 1.0f;
    private float scaleDirection = 0.09f; // szybka animacja
    private Obstacle obstacle; // przeszkody

    public Food(Board board, Pictures pictures, Obstacle obstacle, int fruitCount) {
        this.board = board;
        this.pictures = pictures;
        this.obstacle = obstacle;
        this.fruitCount = fruitCount;
        regenerate();
    }

    public Point getRandomPos() {
        Random random = new Random();
        Point p;
        do {
            int x = random.nextInt(board.getCellCount());
            int y = random.nextInt(board.getCellCount());
            p = new Point(x, y);
        } while (obstacle.getObstacles().contains(p) || positions.contains(p)); // unika kolizji z przeszkodą i innym owocem
        return p;
    }

    public void regenerate() {
        positions.clear();
        for (int i = 0; i < fruitCount; i++) {
            positions.add(getRandomPos());
        }
    }

    public void replace(Point eaten) {
        positions.remove(eaten);
        positions.add(getRandomPos());
    }

    public void draw(Graphics2D g) {
        int baseSize = board.getCellSize();
        int scaledSize = (int) (baseSize * fruitScale);
        int offset = (baseSize - scaledSize) / 2;

        for (Point p : positions) {
            int drawX = p.x * baseSize + offset;
            int drawY = p.y * baseSize + offset;
            pictures.drawFruit(g, drawX, drawY, scaledSize, scaledSize);
        }
    }

    public void updateAnimation() {
        fruitScale += scaleDirection;
        if (fruitScale >= 1.1f || fruitScale <= 0.9f) {
            scaleDirection *= -1;
        }
    }
}
