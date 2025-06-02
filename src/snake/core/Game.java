package snake.core;

import snake.enums.Direction;
import snake.enums.GameLevel;
import snake.enums.GameScreen;
import java.awt.event.KeyEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
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
    private final ScoreDataBase scoreDataBase = new ScoreDataBase();
    private int[] menuYPositions = new int[0];

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
        else if (gameScreen == GameScreen.SCORE_BOARD) {
            drawScoreBoard(g, panelWidth, panelHeight);
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

        String[] levels = {"EASY", "MEDIUM", "HARD", "SCORE BOARD"};
        int xCenter = panelWidth / 2;
        int totalItems = levels.length;
        int topMargin = 170; // opcjonalny margines od góry
        int bottomMargin = 170; // i od dołu
        int usableHeight = panelHeight - topMargin - bottomMargin;
        int spacing = usableHeight / (totalItems - 1); // równe odstępy

        int[] yPositions = new int[totalItems];
        for (int i = 0; i < totalItems; i++) {
            yPositions[i] = topMargin + i * spacing;
        }
        this.menuYPositions = yPositions;

        for (int i = 0; i < levels.length; i++) {
            String text = levels[i];

            // Mniejsza czcionka (np. 48 i 56 dla hovera)
            Font font = (i == hoveredMenuIndex) ? new Font("Arial", Font.BOLD, 60) : new Font("Arial", Font.BOLD, 50);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();

            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int textAscent = fm.getAscent();

            // Ramka dopasowana do rozmiaru tekstu + margines
            int marginX = 30;
            int marginY = 20;

            int frameWidth = textWidth + marginX * 2;
            int frameHeight = textHeight + marginY;

            int frameX = xCenter - frameWidth / 2;
            int frameY = yPositions[i] - frameHeight / 2;
            int textY = frameY + (frameHeight - textHeight) / 2 + textAscent;
            int textX = xCenter - textWidth / 2;


            // Rysowanie tekstu
            g.setColor(Color.BLACK);
            g.drawString(text, textX, textY);

            // Rysowanie ramki (jeśli najechane)
            if (i == hoveredMenuIndex) {
                pictures.drawFrame(g, frameX, frameY, frameWidth, frameHeight);
            }
        }
    }


    private void resetGame() {
        scoreDataBase.addScore(score);
        snake.reset();
        food.regenerate();
        score = 0;
        gameScreen = GameScreen.MENU;
    }

    public void onMouseClick(int x, int y, int panelWidth, int panelHeight) {
        if (gameScreen == GameScreen.MENU && hoveredMenuIndex != -1 && menuYPositions.length > hoveredMenuIndex) {
            int xCenter = panelWidth / 2;

            String text = switch (hoveredMenuIndex) {
                case 0 -> "EASY";
                case 1 -> "MEDIUM";
                case 2 -> "HARD";
                case 3 -> "SCORE BOARD";
                default -> "";
            };

            Font font = (hoveredMenuIndex == hoveredMenuIndex) ? new Font("Arial", Font.BOLD, 56) : new Font("Arial", Font.BOLD, 48);
            Graphics2D g = (Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int frameWidth = textWidth + 60;
            int frameHeight = textHeight + 20;

            int frameX = xCenter - frameWidth / 2;
            int frameY = menuYPositions[hoveredMenuIndex] - frameHeight / 2;

            Rectangle frameBounds = new Rectangle(frameX, frameY, frameWidth, frameHeight);

            if (frameBounds.contains(x, y)) {
                switch (hoveredMenuIndex) {
                    case 0 -> {
                        gameLevel = GameLevel.EASY;
                        snake.reset();
                        score = 0;
                        gameScreen = GameScreen.GAME;
                    }
                    case 1 -> {
                        gameLevel = GameLevel.MEDIUM;
                        snake.reset();
                        score = 0;
                        gameScreen = GameScreen.GAME;
                    }
                    case 2 -> {
                        gameLevel = GameLevel.HARD;
                        snake.reset();
                        score = 0;
                        gameScreen = GameScreen.GAME;
                    }
                    case 3 -> gameScreen = GameScreen.SCORE_BOARD;
                }
            }
        }
    }


    public void onMouseMove(int mouseX, int mouseY, int panelWidth, int panelHeight) {
        if (gameScreen == GameScreen.MENU && menuYPositions.length > 0) {
            hoveredMenuIndex = -1;
            int xCenter = panelWidth / 2;

            for (int i = 0; i < menuYPositions.length; i++) {
                String text = switch (i) {
                    case 0 -> "EASY";
                    case 1 -> "MEDIUM";
                    case 2 -> "HARD";
                    case 3 -> "SCORE BOARD";
                    default -> "";
                };

                Font font = (i == hoveredMenuIndex) ? new Font("Arial", Font.BOLD, 56) : new Font("Arial", Font.BOLD, 48);
                Graphics2D g = (Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
                g.setFont(font);
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int frameWidth = textWidth + 60;
                int frameHeight = textHeight + 20;

                int frameX = xCenter - frameWidth / 2;
                int frameY = menuYPositions[i] - frameHeight / 2;

                Rectangle frameBounds = new Rectangle(frameX, frameY, frameWidth, frameHeight);

                if (frameBounds.contains(mouseX, mouseY)) {
                    hoveredMenuIndex = i;
                    break;
                }
            }
        }
    }

    private void drawScoreBoard(Graphics2D g, int panelWidth, int panelHeight) {
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, panelWidth, panelHeight);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        String title = "High Scores";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (panelWidth - fm.stringWidth(title)) / 2, 80);

        g.setFont(new Font("Arial", Font.PLAIN, 24));
        fm = g.getFontMetrics();

        int y = 130;
        int lineHeight = fm.getHeight() + 5;
        for (int i = 0; i < Math.min(20, scoreDataBase.getScores().size()); i++) {
            ScoreEntry entry = scoreDataBase.getScores().get(i);
            String text = String.format("%-20s %4d", entry.getDateTime(), entry.getScore());
            g.drawString(text, 100, y + i * lineHeight);
        }
    }
}


