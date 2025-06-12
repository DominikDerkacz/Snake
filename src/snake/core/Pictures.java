package snake.core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Pictures {
    private final BufferedImage[] fruits;
    private final BufferedImage snake;
    private final BufferedImage snakeHead;
    private final BufferedImage frame;
    private final BufferedImage snakeAI1;
    private final BufferedImage snakeAI1Head;
    private final BufferedImage snakeAI2;
    private final BufferedImage snakeAI2Head;

    public Pictures() {
        try {
            BufferedImage fruit = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/fruit.png")));
            BufferedImage apple = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/jablko.png")));
            BufferedImage cherry = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/wisnie.png")));
            BufferedImage goldApple = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/zlote_jablko.png")));
            this.fruits = new BufferedImage[]{fruit, apple, cherry, goldApple};

            snake = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/snake.png")));
            snakeHead = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/snakeHead.png")));
            frame = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/frame.png")));
            snakeAI1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/snakeAI1.png")));
            snakeAI1Head = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/snakeAI1_Head.png")));
            snakeAI2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/snakeAI2.png")));
            snakeAI2Head = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/snakeAI2_Head.png")));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Błąd wczytywania obrazów", e);
        }
    }

    public void drawFruit(Graphics2D g, int x, int y, int width, int height, int type) {
        if (type < 0 || type >= fruits.length) type = 0;
        draw(g, fruits[type], x, y, 0, width, height, false);
    }

    public int getFruitCount() {
        return fruits.length;
    }

    public void drawSnake(Graphics2D g, int x, int y, int width, int height) {
        draw(g, snake, x, y, 0, width, height, false);
    }

    public void drawSnakeHead(Graphics2D g, int x, int y, float angle, int width, int height) {
        draw(g, snakeHead, x, y, angle, width, height, true);
    }

    public void drawSnakeAI1(Graphics2D g, int x, int y, int width, int height) {
        draw(g, snakeAI1, x, y, 0, width, height, false);
    }

    public void drawSnakeAI1Head(Graphics2D g, int x, int y, float angle, int width, int height) {
        draw(g, snakeAI1Head, x, y, angle, width, height, true);
    }

    public void drawSnakeAI2(Graphics2D g, int x, int y, int width, int height) {
        draw(g, snakeAI2, x, y, 0, width, height, false);
    }

    public void drawSnakeAI2Head(Graphics2D g, int x, int y, float angle, int width, int height) {
        draw(g, snakeAI2Head, x, y, angle, width, height, true);
    }

    public void drawFrame(Graphics2D g, int x, int y, int width, int height) {
        draw(g, frame, x, y, 0, width, height, true);
    }

    private void draw(Graphics2D g, BufferedImage img, int x, int y, float angle, int width, int height, boolean centerOrigin) {
        AffineTransform transform = new AffineTransform();

        if (centerOrigin) {
            transform.translate(x + width / 2.0, y + height / 2.0);
            transform.rotate(Math.toRadians(angle));
            transform.translate(-width / 2.0, -height / 2.0);
        } else {
            transform.translate(x, y);
        }

        transform.scale((double) width / img.getWidth(), (double) height / img.getHeight());
        g.drawImage(img, transform, null);
    }
}
