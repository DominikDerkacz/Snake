package snake.core;

import snake.enums.Direction;
import snake.enums.GameLevel;
import snake.enums.GameScreen;
import java.awt.event.KeyEvent;

import java.awt.*;
import java.util.List;

public class Game {
    private GameScreen gameScreen = GameScreen.MENU;
    private GameLevel gameLevel = GameLevel.EASY;
    private final Pictures pictures;
    private final Food food;
    private final Board board;
    private final Snake snake;
    private int score = 0;
    private final int gap = 160;
    private final int frameWidth = 350;
    private final int frameHeight = 120;
    private int currentMenuIndex = 0;
    private long lastKeyTime = System.currentTimeMillis();

    public Game(Board board, Pictures pictures) {
        this.board = board;
        this.pictures = pictures;
        this.snake = new Snake(board, pictures);
        this.food = new Food(board, pictures);
    }

    public void draw(Graphics2D g, int panelWidth, int panelHeight) {
        if (gameScreen == GameScreen.GAME) {
            board.drawBoard(g);
            snake.draw(g);
            food.draw(g);
            drawScore(g, panelWidth);
        } else if (gameScreen == GameScreen.MENU) {
            drawMenu(g, panelWidth, panelHeight);
        }
    }

    public void update() {
        if (gameScreen == GameScreen.GAME) {
            snake.update();
            handleFoodCollision();
            handleTailCollision();
            handleWallCollision();
        }
    }

    public void handleFoodCollision() {
        if (snake.getTail().get(0).equals(food.position)) {
            score++;
            food.regenerate();
            snake.addTail();
        }
    }

    public void handleTailCollision() {
        List<Point> tail = snake.getTail();
        Point head = tail.get(0);
        for (int i = 1; i < tail.size(); i++) {
            if (head.equals(tail.get(i))) {
                resetGame();
                break;
            }
        }
    }

    public void handleWallCollision() {
        Point head = snake.getTail().get(0);
        if (head.x < 0 || head.y < 0 || head.x >= board.getCellCount() || head.y >= board.getCellCount()) {
            resetGame();
        }
    }

    public boolean shouldMove(float delay) {
        return snake.moveTime(delayForLevel());
    }

    private float delayForLevel() {
        return switch (gameLevel) {
            case EASY -> 0.1f;
            case MEDIUM -> 0.05f;
            case HARD -> 0.015f;
        };
    }

    public void onKeyPress(int keyCode) {
        long now = System.currentTimeMillis();
        if (gameScreen == GameScreen.GAME) {
            if (keyCode == KeyEvent.VK_UP) snake.moveDirection(Direction.UP);
            if (keyCode == KeyEvent.VK_DOWN) snake.moveDirection(Direction.DOWN);
            if (keyCode == KeyEvent.VK_LEFT) snake.moveDirection(Direction.LEFT);
            if (keyCode == KeyEvent.VK_RIGHT) snake.moveDirection(Direction.RIGHT);
        } else if (gameScreen == GameScreen.MENU) {
            if (now - lastKeyTime > 120) {
                if (keyCode == KeyEvent.VK_DOWN) {
                    currentMenuIndex = (currentMenuIndex + 1) % 3;
                    lastKeyTime = now;
                }
                if (keyCode == KeyEvent.VK_UP) {
                    currentMenuIndex = (currentMenuIndex - 1 + 3) % 3;
                    lastKeyTime = now;
                }
                if (keyCode == KeyEvent.VK_ENTER) {
                    switch (currentMenuIndex) {
                        case 0 -> gameLevel = GameLevel.EASY;
                        case 1 -> gameLevel = GameLevel.MEDIUM;
                        case 2 -> gameLevel = GameLevel.HARD;
                    }
                    snake.reset();
                    score = 0;
                    gameScreen = GameScreen.GAME;
                }
            }
        }
    }

    private void drawScore(Graphics2D g, int panelWidth) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        String scoreText = "Score: " + score;
        FontMetrics metrics = g.getFontMetrics();
        int x = (panelWidth - metrics.stringWidth(scoreText)) / 2;
        int y = board.getCellCount() * board.getCellSize() + 65;
        g.drawString(scoreText, x, y);
    }

    private void drawMenu(Graphics2D g, int panelWidth, int panelHeight) {
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, panelWidth, panelHeight);

        String[] levels = {"EASY", "MEDIUM", "HARD"};
        g.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics fm = g.getFontMetrics();
        int x = panelWidth / 2;
        int y = panelHeight / 4;

        for (int i = 0; i < levels.length; i++) {
            String text = levels[i];
            int textWidth = fm.stringWidth(text);
            g.setColor(Color.BLACK);
            g.drawString(text, x - textWidth / 2, y + i * gap);

            if (i == currentMenuIndex) {
                pictures.drawFrame(g, x - frameWidth / 2, y + i * gap - frameHeight / 2, frameWidth, frameHeight);
            }
        }
    }

    private void resetGame() {
        snake.reset();
        food.regenerate();
        score = 0;
        gameScreen = GameScreen.MENU;
    }
}
