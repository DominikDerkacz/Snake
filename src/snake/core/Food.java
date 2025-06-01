package snake.core;

import java.awt.*;
import java.util.Random;

public class Food {
    private final Board board;
    private final Pictures pictures;
    public Point position;

    public Food(Board board, Pictures pictures) {
        this.board = board;
        this.pictures = pictures;
        this.position = getRandomPos();
    }

    public Point getRandomPos() {
        Random random = new Random();
        int x = random.nextInt(board.getCellCount());
        int y = random.nextInt(board.getCellCount());
        return new Point(x, y);
    }

    public void regenerate() {
        this.position = getRandomPos();
    }

    public void draw(Graphics2D g) {
        int size = board.getCellSize();
        pictures.drawFruit(g, position.x * size, position.y * size, size, size);
    }
}
