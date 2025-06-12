package snake.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages a pool of fruits and runs in its own thread to regenerate them.
 */
public class FoodManager implements Runnable {
    private final Board board;
    private final Pictures pictures;
    private final Obstacle obstacle;
    private final List<Snake> snakes;
    private final List<Point> fruits = new ArrayList<>();
    private final Random random = new Random();
    private final int poolSize;
    private volatile boolean running = true;

    public FoodManager(Board board, Pictures pictures, Obstacle obstacle, List<Snake> snakes, int poolSize) {
        this.board = board;
        this.pictures = pictures;
        this.obstacle = obstacle;
        this.snakes = snakes;
        this.poolSize = poolSize;
    }

    @Override
    public void run() {
        while (running) {
            synchronized (fruits) {
                while (fruits.size() < poolSize) {
                    fruits.add(randomPos());
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    private Point randomPos() {
        Point p;
        do {
            p = new Point(random.nextInt(board.getCellCount()), random.nextInt(board.getCellCount()));
        } while (occupied(p));
        return p;
    }

    private boolean occupied(Point p) {
        if (obstacle.getObstacles().contains(p)) return true;
        for (Snake s : snakes) {
            if (s.getTail().contains(p)) return true;
        }
        return fruits.contains(p);
    }

    public boolean consume(Point p) {
        synchronized (fruits) {
            return fruits.remove(p);
        }
    }

    public void draw(Graphics2D g) {
        synchronized (fruits) {
            int size = board.getCellSize();
            for (Point f : fruits) {
                pictures.drawFruit(g, f.x * size, f.y * size, size, size);
            }
        }
    }

    public void stop() {
        running = false;
    }
}
