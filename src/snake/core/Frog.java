package snake.core;

import snake.enums.Direction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Moving frog object that avoids snakes and obstacles.
 */
public class Frog implements Runnable {
    private final Board board;
    private final Obstacle obstacle;
    private final List<Snake> snakes;
    private final Random random = new Random();
    private volatile boolean running = true;
    private Point position;

    public Frog(Board board, Obstacle obstacle, List<Snake> snakes) {
        this.board = board;
        this.obstacle = obstacle;
        this.snakes = snakes;
        this.position = randomPos();
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
        return false;
    }

    @Override
    public void run() {
        while (running) {
            move();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    private void move() {
        List<Point> options = new ArrayList<>();
        for (Direction d : Direction.values()) {
            Point next = switch (d) {
                case UP -> new Point(position.x, position.y - 1);
                case DOWN -> new Point(position.x, position.y + 1);
                case LEFT -> new Point(position.x - 1, position.y);
                case RIGHT -> new Point(position.x + 1, position.y);
            };
            if (next.x < 0 || next.y < 0 || next.x >= board.getCellCount() || next.y >= board.getCellCount())
                continue;
            if (!occupied(next)) options.add(next);
        }
        if (!options.isEmpty()) position = options.get(random.nextInt(options.size()));
    }

    public synchronized Point getPosition() {
        return new Point(position);
    }

    public void draw(Graphics2D g) {
        int size = board.getCellSize();
        g.setColor(Color.MAGENTA);
        g.fillOval(position.x * size, position.y * size, size, size);
    }

    public synchronized void reset() {
        this.position = randomPos();
    }

    public void stop() {
        running = false;
    }
}
