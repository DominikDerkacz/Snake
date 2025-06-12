package snake.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Klasa {@code Frog} reprezentuje żabę, która okresowo pojawia się na planszy.
 * Unika węży i przeszkód, porusza się rzadziej niż wąż, a po zjedzeniu znika i pojawia się ponownie po pewnym czasie.
 */
public class Frog {
    private final Board board;
    private final Pictures pictures;
    private final Obstacle obstacle;
    private final List<Snake> snakes;
    private final Random random = new Random();

    private Point position;
    private long lastMoveMillis = System.currentTimeMillis();
    private long nextSpawnTime = 0;

    /** Opóźnienie ruchu żaby względem czasu (w sekundach). */
    private static final float MOVE_DELAY = 0.3f;

    /** Opóźnienie ponownego pojawienia się żaby po zjedzeniu (w sekundach). */
    private static final float RESPAWN_DELAY = 5f;

    /**
     * Tworzy nowy obiekt {@code Frog} i od razu umieszcza żabę na planszy.
     *
     * @param board    plansza gry
     * @param pictures obiekt rysujący żabę
     * @param obstacle obiekt z przeszkodami
     * @param snakes   lista węży, których obecność jest uwzględniana w logice ruchu żaby
     */
    public Frog(Board board, Pictures pictures, Obstacle obstacle, List<Snake> snakes) {
        this.board = board;
        this.pictures = pictures;
        this.obstacle = obstacle;
        this.snakes = new ArrayList<>(snakes);
        spawn();
    }

    /** Losuje nową pozycję żaby i resetuje czas ostatniego ruchu. */
    private void spawn() {
        position = getRandomPos();
        lastMoveMillis = System.currentTimeMillis();
    }

    /**
     * Losuje pozycję na planszy, która nie koliduje z wężami ani przeszkodami.
     *
     * @return nowa, bezpieczna pozycja żaby
     */
    private Point getRandomPos() {
        Point p;
        do {
            int x = random.nextInt(board.getCellCount());
            int y = random.nextInt(board.getCellCount());
            p = new Point(x, y);
        } while (onSnake(p) || obstacle.getObstacles().contains(p));
        return p;
    }

    /**
     * Sprawdza, czy dany punkt koliduje z jakimkolwiek wężem.
     *
     * @param p punkt do sprawdzenia
     * @return {@code true}, jeśli punkt jest zajęty przez węża
     */
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

    /**
     * Aktualizuje stan żaby – wykonuje ruch lub generuje nową żabę po respawnie.
     */
    public void update() {
        long current = System.currentTimeMillis();
        if (position == null) {
            if (current >= nextSpawnTime) {
                spawn();
            }
            return;
        }
        if ((current - lastMoveMillis) / 1000f >= MOVE_DELAY) {
            moveAwayFromSnakes();
            lastMoveMillis = current;
        }
    }

    /**
     * Przesuwa żabę w kierunku przeciwnym do najbliższego węża (ucieczka).
     */
    private void moveAwayFromSnakes() {
        if (position == null) return;
        List<Point> candidates = new ArrayList<>();
        int[][] moves = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        for (int[] m : moves) {
            Point np = new Point(position.x + m[0], position.y + m[1]);
            if (isSafe(np)) {
                candidates.add(np);
            }
        }
        if (candidates.isEmpty()) return;

        Point best = candidates.get(0);
        int bestDist = distanceToNearestSnake(best);
        for (Point cand : candidates) {
            int d = distanceToNearestSnake(cand);
            if (d > bestDist) {
                bestDist = d;
                best = cand;
            }
        }
        position = best;
    }

    /**
     * Oblicza dystans Manhattan do najbliższego segmentu dowolnego żywego węża.
     *
     * @param p punkt do sprawdzenia
     * @return minimalna odległość od któregoś z segmentów węża
     */
    private int distanceToNearestSnake(Point p) {
        int best = Integer.MAX_VALUE;
        for (Snake s : snakes) {
            if (!s.isAlive()) continue;
            for (Point seg : s.getTail()) {
                int d = Math.abs(seg.x - p.x) + Math.abs(seg.y - p.y);
                if (d < best) best = d;
            }
        }
        return best;
    }

    /**
     * Sprawdza, czy punkt znajduje się w granicach planszy i nie koliduje z przeszkodami ani wężami.
     *
     * @param p punkt do sprawdzenia
     * @return {@code true}, jeśli punkt jest bezpieczny
     */
    private boolean isSafe(Point p) {
        if (p.x < 0 || p.y < 0 || p.x >= board.getCellCount() || p.y >= board.getCellCount())
            return false;
        if (obstacle.getObstacles().contains(p)) return false;
        return !onSnake(p);
    }

    /**
     * Rysuje żabę na planszy, jeśli jest obecna.
     *
     * @param g obiekt graficzny do rysowania
     */
    public void draw(Graphics2D g) {
        if (position == null) return;
        int size = board.getCellSize();
        pictures.drawFrog(g, position.x * size, position.y * size, size, size);
    }

    /**
     * Zwraca bieżącą pozycję żaby.
     *
     * @return pozycja żaby lub {@code null}, jeśli nie jest obecna
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Oznacza, że żaba została zjedzona – znika i zaczyna odliczać czas do ponownego pojawienia się.
     */
    public void eaten() {
        position = null;
        nextSpawnTime = System.currentTimeMillis() + (long) (RESPAWN_DELAY * 1000);
    }
}
