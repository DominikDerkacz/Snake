package snake.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Klasa {@code Obstacle} reprezentuje przeszkody pojawiające się na planszy gry Snake.
 * Przeszkody są losowo generowane z zachowaniem odstępów od siebie oraz od węży.
 */
public class Obstacle {
    /** Lista punktów reprezentujących pozycje przeszkód. */
    private final List<Point> obstacles = new ArrayList<>();

    /** Plansza, na której rozmieszczone są przeszkody. */
    private final Board board;

    /** Liczba przeszkód do wygenerowania. */
    private int obstacleCount;

    /** Lista węży, względem których przeszkody nie mogą być zbyt blisko. */
    private final List<Snake> snakes = new ArrayList<>();

    /**
     * Tworzy nowy obiekt {@code Obstacle} i generuje przeszkody.
     *
     * @param board plansza gry
     * @param count liczba przeszkód do wygenerowania
     */
    public Obstacle(Board board, int count) {
        this.board = board;
        this.obstacleCount = count;
        generateObstacles();
    }

    /**
     * Generuje przeszkody na planszy z zachowaniem minimalnej odległości między nimi i wężami.
     * Próbuje do skutku lub do przekroczenia limitu prób.
     */
    private void generateObstacles() {
        obstacles.clear();
        Random rand = new Random();
        int cellCount = board.getCellCount();

        int attempts = 0;
        int maxAttempts = obstacleCount * 100;

        while (obstacles.size() < obstacleCount && attempts < maxAttempts) {
            attempts++;
            Point candidate = new Point(rand.nextInt(cellCount), rand.nextInt(cellCount));
            boolean tooClose = false;

            // Sprawdzenie dystansu do innych przeszkód
            for (Point existing : obstacles) {
                int dx = Math.abs(candidate.x - existing.x);
                int dy = Math.abs(candidate.y - existing.y);
                if (dx <= 3 && dy <= 3) {
                    tooClose = true;
                    break;
                }
            }

            // Sprawdzenie dystansu do węży
            for (Snake s : snakes) {
                for (Point segment : s.getTail()) {
                    int dx = Math.abs(candidate.x - segment.x);
                    int dy = Math.abs(candidate.y - segment.y);
                    if (dx <= 3 && dy <= 2) {
                        tooClose = true;
                        break;
                    }
                }
                if (tooClose) break;
            }

            if (!tooClose) {
                obstacles.add(candidate);
            }
        }
    }

    /**
     * Zwraca listę przeszkód.
     *
     * @return lista punktów, w których znajdują się przeszkody
     */
    public List<Point> getObstacles() {
        return obstacles;
    }

    /**
     * Rysuje przeszkody na planszy.
     *
     * @param g kontekst graficzny
     */
    public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        int size = board.getCellSize();
        for (Point p : obstacles) {
            g.fillRect(p.x * size, p.y * size, size, size);
        }
    }

    /**
     * Generuje nowe przeszkody (np. po zmianie poziomu trudności).
     */
    public void regenerate() {
        generateObstacles();
    }

    /**
     * Ustawia listę węży, względem których przeszkody powinny zachowywać odległość.
     *
     * @param snakes lista węży
     */
    public void setSnakes(List<Snake> snakes) {
        this.snakes.clear();
        this.snakes.addAll(snakes);
    }

    /**
     * Ustawia nową liczbę przeszkód do wygenerowania.
     *
     * @param count nowa liczba przeszkód
     */
    public void setObstacleCount(int count) {
        this.obstacleCount = count;
    }
}
