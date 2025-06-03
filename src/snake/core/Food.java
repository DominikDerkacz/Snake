package snake.core;

import java.awt.*;
import java.util.Random;

public class Food {
    private final Board board;
    private final Pictures pictures;
    public Point position;
    private float fruitScale = 1.0f;
    private float scaleDirection = 0.09f; // szybka animacja
    private Obstacle obstacle; // przeszkody

    public Food(Board board, Pictures pictures, Obstacle obstacle) {
        this.board = board;
        this.pictures = pictures;
        this.obstacle = obstacle;
        this.position = getRandomPos();
    }

    public Point getRandomPos() {
        Random random = new Random();
        Point p;
        do {
            int x = random.nextInt(board.getCellCount());
            int y = random.nextInt(board.getCellCount());
            p = new Point(x, y);
        } while (obstacle.getObstacles().contains(p)); // unika kolizji z przeszkodą
        return p;
    }

    public void regenerate() {
        this.position = getRandomPos();
    }

    public void draw(Graphics2D g) {
        int baseSize = board.getCellSize();
        int scaledSize = (int) (baseSize * fruitScale);
        int offset = (baseSize - scaledSize) / 2;

        int drawX = position.x * baseSize + offset;
        int drawY = position.y * baseSize + offset;

        pictures.drawFruit(g, drawX, drawY, scaledSize, scaledSize);
    }

    public void updateAnimation() {
        fruitScale += scaleDirection;
        if (fruitScale >= 1.1f || fruitScale <= 0.9f) {
            scaleDirection *= -1;
        }
    }
}
