package snake.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Obstacle {
    private final List<Point> obstacles = new ArrayList<>();
    private final Board board;
    private int obstacleCount;
    private final List<Snake> snakes = new ArrayList<>();

    public Obstacle(Board board, int count) {
        this.board = board;
        this.obstacleCount = count;
        generateObstacles();
    }

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

    public List<Point> getObstacles() {
        return obstacles;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        int size = board.getCellSize();
        for (Point p : obstacles) {
            g.fillRect(p.x * size, p.y * size, size, size);
        }
    }

    public void regenerate() {
        generateObstacles();
    }

    public void setSnakes(List<Snake> snakes) {
        this.snakes.clear();
        this.snakes.addAll(snakes);
    }

    public void setObstacleCount(int count) {
        this.obstacleCount = count;
    }
}
