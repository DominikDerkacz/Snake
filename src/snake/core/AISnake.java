package snake.core;

import snake.enums.Direction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simple AI controlled snake running in its own thread.
 */
public class AISnake extends Snake implements Runnable {
    private final Obstacle obstacle;
    private final List<Snake> others;
    private final float delay;
    private volatile boolean running = true;
    private final Random random = new Random();
    private final List<Point> startTail;

    public AISnake(Board board, Pictures pictures, Obstacle obstacle, List<Snake> others,
                   float delay, List<Point> startTail) {
        super(board, pictures);
        this.obstacle = obstacle;
        this.others = new ArrayList<>(others);
        this.delay = delay;
        this.startTail = new ArrayList<>(startTail);
        reset();
    }

    @Override
    public void run() {
        while (running) {
            if (moveTime(delay)) {
                chooseDirection();
                update();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    private void chooseDirection() {
        List<Direction> possible = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (d == opposite(getDirection())) continue;
            Point next = nextPoint(d);
            if (next.x < 0 || next.y < 0 ||
                    next.x >= getBoard().getCellCount() ||
                    next.y >= getBoard().getCellCount()) continue;
            if (obstacle.getObstacles().contains(next)) continue;
            boolean collides = false;
            for (Snake s : others) {
                if (s.getTail().contains(next)) {
                    collides = true;
                    break;
                }
            }
            if (!collides) possible.add(d);
        }
        if (!possible.isEmpty()) {
            moveDirection(possible.get(random.nextInt(possible.size())));
        }
    }

    private Point nextPoint(Direction dir) {
        Point head = getTail().get(0);
        return switch (dir) {
            case UP -> new Point(head.x, head.y - 1);
            case DOWN -> new Point(head.x, head.y + 1);
            case LEFT -> new Point(head.x - 1, head.y);
            case RIGHT -> new Point(head.x + 1, head.y);
        };
    }

    private Direction opposite(Direction dir) {
        return switch (dir) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    public void stop() {
        running = false;
    }

    @Override
    public void reset() {
        super.reset();
        tail = new ArrayList<>(startTail);
    }
}
