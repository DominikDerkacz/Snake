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
    private int hoveredMenuIndex = -1;

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
        if (gameScreen == GameScreen.GAME) {
            if (keyCode == KeyEvent.VK_UP) snake.moveDirection(Direction.UP);
            if (keyCode == KeyEvent.VK_DOWN) snake.moveDirection(Direction.DOWN);
            if (keyCode == KeyEvent.VK_LEFT) snake.moveDirection(Direction.LEFT);
            if (keyCode == KeyEvent.VK_RIGHT) snake.moveDirection(Direction.RIGHT);
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
        int xCenter = panelWidth / 2;
        int yStart = panelHeight / 4;

        for (int i = 0; i < levels.length; i++) {
            String text = levels[i];

            Font font = (i == hoveredMenuIndex) ? new Font("Arial", Font.BOLD, 72) : new Font("Arial", Font.BOLD, 60);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();

            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int textAscent = fm.getAscent();

            int frameX = xCenter - frameWidth / 2;
            int frameY = yStart + i * gap - frameHeight / 2;

            // Pozycja tekstu: środek ramki + korekta do środka tekstu
            int textX = xCenter - textWidth / 2;
            int textY = frameY + (frameHeight - textHeight) / 2 + textAscent;

            g.setColor(Color.BLACK);
            g.drawString(text, textX, textY);

            if (i == hoveredMenuIndex) {
                pictures.drawFrame(g, frameX, frameY, frameWidth, frameHeight);
            }
        }
    }


    private void resetGame() {
        snake.reset();
        food.regenerate();
        score = 0;
        gameScreen = GameScreen.MENU;
    }

    public void onMouseClick(int x, int y, int panelWidth, int panelHeight) {
        if (gameScreen == GameScreen.MENU) {
            String[] levels = {"EASY", "MEDIUM", "HARD"};
            int yStart = panelHeight / 4;

            for (int i = 0; i < levels.length; i++) {
                int optionY = yStart + i * gap;
                int optionHeight = frameHeight;

                // Sprawdzenie, czy kliknięto w obszar opcji
                if (y >= optionY - optionHeight / 2 && y <= optionY + optionHeight / 2) {
                    currentMenuIndex = i; // <--- Aktualizacja indeksu ramki
                    switch (i) {
                        case 0 -> gameLevel = GameLevel.EASY;
                        case 1 -> gameLevel = GameLevel.MEDIUM;
                        case 2 -> gameLevel = GameLevel.HARD;
                    }
                    snake.reset();
                    score = 0;
                    gameScreen = GameScreen.GAME;
                    break;
                }
            }
        }
    }

    public void onMouseMove(int mouseX, int mouseY, int panelWidth, int panelHeight) {
        if (gameScreen == GameScreen.MENU) {
            String[] levels = {"EASY", "MEDIUM", "HARD"};
            int yStart = panelHeight / 4;

            hoveredMenuIndex = -1;
            for (int i = 0; i < levels.length; i++) {
                int optionY = yStart + i * gap;
                int optionHeight = frameHeight;

                if (mouseY >= optionY - optionHeight / 2 && mouseY <= optionY + optionHeight / 2) {
                    hoveredMenuIndex = i;
                    break;
                }
            }
        }
    }
}


