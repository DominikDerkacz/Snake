package snake.core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Klasa {@code Pictures} odpowiada za wczytywanie i rysowanie grafik gry.
 * Przechowuje obrazy węży, głów, owoców, żaby oraz ramek i zapewnia metody ich renderowania.
 */
public class Pictures {

    /** Tablica obrazów owoców (różne typy). */
    private final BufferedImage[] fruits;

    /** Obraz segmentu węża gracza. */
    private final BufferedImage snake;

    /** Obraz głowy węża gracza. */
    private final BufferedImage snakeHead;

    /** Obraz ramki dekoracyjnej (np. do menu). */
    private final BufferedImage frame;

    /** Obraz segmentu węża AI1. */
    private final BufferedImage snakeAI1;

    /** Obraz głowy węża AI1. */
    private final BufferedImage snakeAI1Head;

    /** Obraz segmentu węża AI2. */
    private final BufferedImage snakeAI2;

    /** Obraz głowy węża AI2. */
    private final BufferedImage snakeAI2Head;

    /** Obraz żaby. */
    private final BufferedImage frog;

    /**
     * Konstruktor. Wczytuje wszystkie obrazy z zasobów gry.
     * Rzuca wyjątek {@code RuntimeException} w przypadku błędu wczytywania.
     */
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
            frog = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/zaba.png")));
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Błąd wczytywania obrazów", e);
        }
    }

    /**
     * Rysuje owoc o podanym typie.
     *
     * @param g     kontekst graficzny
     * @param x     współrzędna X
     * @param y     współrzędna Y
     * @param width szerokość
     * @param height wysokość
     * @param type  indeks typu owocu
     */
    public void drawFruit(Graphics2D g, int x, int y, int width, int height, int type) {
        if (type < 0 || type >= fruits.length) type = 0;
        draw(g, fruits[type], x, y, 0, width, height, false);
    }

    /**
     * Zwraca liczbę dostępnych typów owoców.
     *
     * @return liczba obrazów owoców
     */
    public int getFruitCount() {
        return fruits.length;
    }

    /**
     * Rysuje segment węża gracza.
     */
    public void drawSnake(Graphics2D g, int x, int y, int width, int height) {
        draw(g, snake, x, y, 0, width, height, false);
    }

    /**
     * Rysuje głowę węża gracza z obrotem.
     */
    public void drawSnakeHead(Graphics2D g, int x, int y, float angle, int width, int height) {
        draw(g, snakeHead, x, y, angle, width, height, true);
    }

    /**
     * Rysuje segment węża AI1.
     */
    public void drawSnakeAI1(Graphics2D g, int x, int y, int width, int height) {
        draw(g, snakeAI1, x, y, 0, width, height, false);
    }

    /**
     * Rysuje głowę węża AI1 z obrotem.
     */
    public void drawSnakeAI1Head(Graphics2D g, int x, int y, float angle, int width, int height) {
        draw(g, snakeAI1Head, x, y, angle, width, height, true);
    }

    /**
     * Rysuje segment węża AI2.
     */
    public void drawSnakeAI2(Graphics2D g, int x, int y, int width, int height) {
        draw(g, snakeAI2, x, y, 0, width, height, false);
    }

    /**
     * Rysuje głowę węża AI2 z obrotem.
     */
    public void drawSnakeAI2Head(Graphics2D g, int x, int y, float angle, int width, int height) {
        draw(g, snakeAI2Head, x, y, angle, width, height, true);
    }

    /**
     * Rysuje żabę.
     */
    public void drawFrog(Graphics2D g, int x, int y, int width, int height) {
        draw(g, frog, x, y, 0, width, height, false);
    }

    /**
     * Rysuje ramkę dekoracyjną.
     */
    public void drawFrame(Graphics2D g, int x, int y, int width, int height) {
        draw(g, frame, x, y, 0, width, height, true);
    }

    /**
     * Metoda pomocnicza do rysowania obrazków z opcjonalnym obrotem i skalowaniem.
     *
     * @param g            kontekst graficzny
     * @param img          obraz do narysowania
     * @param x            współrzędna X
     * @param y            współrzędna Y
     * @param angle        kąt obrotu w stopniach
     * @param width        szerokość docelowa
     * @param height       wysokość docelowa
     * @param centerOrigin czy obrót ma być wokół środka
     */
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
