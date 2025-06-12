package snake.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Frog {
    private final Board board;
    private final Pictures pictures;
    private final Obstacle obstacle;
    private final List<Snake> snakes;
    private final Random random = new Random();

    private Point position;
    private long lastMoveMillis = System.currentTimeMillis();
    private long nextSpawnTime = 0; // millis

    // movement delay in seconds (slower than snake)
    private static final float MOVE_DELAY = 0.3f;
    // respawn delay after being eaten (in seconds)
    private static final float RESPAWN_DELAY = 5f;

    public Frog(Board board, Pictures pictures, Obstacle obstacle, List<Snake> snakes) {
        this.board = board;
        this.pictures = pictures;
        this.obstacle = obstacle;
        this.snakes = new ArrayList<>(snakes);
        spawn();
    }

    private void spawn() {
        position = getRandomPos();
        lastMoveMillis = System.currentTimeMillis();
    }

    private Point getRandomPos() {
        Point p;
        do {
            int x = random.nextInt(board.getCellCount());
            int y = random.nextInt(board.getCellCount());
            p = new Point(x, y);
        } while (onSnake(p) || obstacle.getObstacles().contains(p));
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

    private void moveAwayFromSnakes() {
        if (position == null) return;
        List<Point> candidates = new ArrayList<>();
        int[][] moves = {{0,-1},{0,1},{-1,0},{1,0}};
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

    private boolean isSafe(Point p) {
        if (p.x < 0 || p.y < 0 || p.x >= board.getCellCount() || p.y >= board.getCellCount())
            return false;
        if (obstacle.getObstacles().contains(p)) return false;
        return !onSnake(p);
    }

    public void draw(Graphics2D g) {
        if (position == null) return;
        int size = board.getCellSize();
        pictures.drawFrog(g, position.x * size, position.y * size, size, size);
    }

    public Point getPosition() {
        return position;
    }

    public void eaten() {
        position = null;
        nextSpawnTime = System.currentTimeMillis() + (long)(RESPAWN_DELAY * 1000);
    }
}
