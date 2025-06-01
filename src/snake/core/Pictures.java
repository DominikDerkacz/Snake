package snake.core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Pictures {
    private final BufferedImage fruit;
    private final BufferedImage snake;
    private final BufferedImage snakeHead;
    private final BufferedImage frame;

    public Pictures() {
        try {
            fruit = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/fruit.png")));
            snake = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/snake.png")));
            snakeHead = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/snakeHead.png")));
            frame = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/frame.png")));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Błąd wczytywania obrazów", e);
        }
    }

    public void drawFruit(Graphics2D g, int x, int y, int width, int height) {
        draw(g, fruit, x, y, 0, width, height, false);
    }

    public void drawSnake(Graphics2D g, int x, int y, int width, int height) {
        draw(g, snake, x, y, 0, width, height, false);
    }

    public void drawSnakeHead(Graphics2D g, int x, int y, float angle, int width, int height) {
        draw(g, snakeHead, x, y, angle, width, height, true);
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
