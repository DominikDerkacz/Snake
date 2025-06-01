package snake.core;

import snake.enums.Direction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Snake {
    private final Pictures pictures;
    private final Board board;
    private final List<Point> tailStart = List.of(
            new Point(7, 6),
            new Point(6, 6),
            new Point(5, 6)
    );

    public List<Point> tail = new ArrayList<>(tailStart);
    private Point move = new Point(1, 0);
    private Direction direction = Direction.RIGHT;
    private boolean gameRunning = false;
    private float lastTime = 0f;
    private int angle = 0;
    private long lastMoveMillis = System.currentTimeMillis();

    public Snake(Board board, Pictures pictures) {
        this.board = board;
        this.pictures = pictures;
    }

    public void draw(Graphics2D g) {
        int cellSize = board.getCellSize();
        Point head = tail.get(0);
        int x = head.x * cellSize;
        int y = head.y * cellSize;

        // Rysuj głowę bez przesunięcia
        pictures.drawSnakeHead(g, x, y, angle, cellSize, cellSize);

        // Rysuj resztę ogona
        for (int i = 1; i < tail.size(); i++) {
            Point segment = tail.get(i);
            pictures.drawSnake(g, segment.x * cellSize, segment.y * cellSize, cellSize, cellSize);
        }
    }



    public boolean moveTime(float delay) {
        long current = System.currentTimeMillis();
        if ((current - lastMoveMillis) / 1000.0f >= delay) {
            lastMoveMillis = current;
            return true;
        }
        return false;
    }

    public void update() {
        if (gameRunning) {
            tail.remove(tail.size() - 1);
            Point newHead = new Point(tail.get(0).x + move.x, tail.get(0).y + move.y);
            tail.add(0, newHead);
        }
    }

    public void moveDirection(Direction dir) {
        if (dir == Direction.UP && direction != Direction.DOWN) {
            move.setLocation(0, -1);
            direction = Direction.UP;
            angle = 270;
            gameRunning = true;
        }
        if (dir == Direction.DOWN && direction != Direction.UP) {
            move.setLocation(0, 1);
            direction = Direction.DOWN;
            angle = 90;
            gameRunning = true;
        }
        if (dir == Direction.LEFT && direction != Direction.RIGHT) {
            move.setLocation(-1, 0);
            direction = Direction.LEFT;
            angle = 180;
            gameRunning = true;
        }
        if (dir == Direction.RIGHT && direction != Direction.LEFT) {
            move.setLocation(1, 0);
            direction = Direction.RIGHT;
            angle = 0;
            gameRunning = true;
        }
    }

    public void addTail() {
        tail.add(new Point(tail.get(tail.size() - 1)));
    }

    public void reset() {
        tail = new ArrayList<>(tailStart);
        direction = Direction.RIGHT;
        move.setLocation(1, 0);
        angle = 0;
        gameRunning = false;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public List<Point> getTail() {
        return tail;
    }

    public Direction getDirection() {
        return direction;
    }
}
