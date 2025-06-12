package snake.core;

import snake.enums.Direction;
import snake.enums.GameLevel;
import snake.enums.GameScreen;
import snake.enums.SnakeType;
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
    private final Snake snakeAI1;
    private final Snake snakeAI2;
    private final Obstacle obstacle;
    private final Frog frog;
    private int score = 0;
    private int hoveredMenuIndex = -1;
    private final ScoreDataBase scoreDataBase = new ScoreDataBase();
    private int[] menuYPositions = new int[0];
    private Rectangle backButtonBounds = null;
    private int scrollOffset = 0;
    private boolean draggingThumb = false;
    private int dragOffsetY = 0;
    private boolean hoveredBackButton;

    public Game(Board board, Pictures pictures) {
        this.board = board;
        this.pictures = pictures;
        this.snake = new Snake(board, pictures);
        this.snakeAI1 = new Snake(board, pictures, SnakeType.AI1, List.of(
                new Point(12, 6), new Point(11, 6), new Point(10, 6)));
        this.snakeAI2 = new Snake(board, pictures, SnakeType.AI2, List.of(
                new Point(7, 12), new Point(6, 12), new Point(5, 12)));
        this.obstacle = new Obstacle(board, 0); // najpierw przeszkody
        this.obstacle.setSnakes(List.of(snake, snakeAI1, snakeAI2));
        this.food = new Food(board, pictures, obstacle, 5, List.of(snake, snakeAI1, snakeAI2)); // potem jedzenie
        this.frog = new Frog(board, pictures, obstacle, List.of(snake, snakeAI1, snakeAI2));
        hoveredBackButton = false;

    }

    public void draw(Graphics2D g, int panelWidth, int panelHeight) {
        if (gameScreen == GameScreen.GAME) {
            board.drawBoard(g);
            obstacle.draw(g);
            snake.draw(g);
            snakeAI1.draw(g);
            snakeAI2.draw(g);
            food.draw(g);
            frog.draw(g);
            drawScore(g, panelWidth);

        }
        else if (gameScreen == GameScreen.MENU) {
            drawMenu(g, panelWidth, panelHeight);
        }
        else if (gameScreen == GameScreen.SCORE_BOARD) {
            drawScoreBoard(g, panelWidth, panelHeight);
        }
        
    }

    public void update() {
        if (gameScreen == GameScreen.GAME) {
            if (snake.isGameRunning()) {
                if (snakeAI1.isAlive()) updateAISnake(snakeAI1, snakeAI2);
                if (snakeAI2.isAlive()) updateAISnake(snakeAI2, snakeAI1);
            }

            snake.update();
            if (snake.isGameRunning()) {
                if (snakeAI1.isAlive()) snakeAI1.update();
                if (snakeAI2.isAlive()) snakeAI2.update();
            }

            food.updateAnimation();
            frog.update();
            handleFoodCollision();
            handleFrogCollision();
            handleTailCollision();
            handleWallCollision();
            handleObstacleCollision();
            handleAICollisions();
        }
    }

    public void handleFoodCollision() {
        for (int i = 0; i < food.positions.size(); i++) {
            Point fruit = food.positions.get(i);
            if (snake.getTail().getFirst().equals(fruit)) {
                int type = food.getType(i);
                score += (type == Food.GOLDEN_APPLE_INDEX) ? 2 : 1;
                food.replace(fruit);
                snake.addTail();
                if (type == Food.GOLDEN_APPLE_INDEX) {
                    snake.addTail();
                }
                break;
            }
        }

        checkAIFoodCollision(snakeAI1);
        checkAIFoodCollision(snakeAI2);
    }

    private void handleFrogCollision() {
        Point head = snake.getTail().getFirst();
        if (frog.getPosition() != null && frog.getPosition().equals(head)) {
            score += 2;
            snake.addTail();
            snake.addTail();
            frog.eaten();
        }

        checkAIFrogCollision(snakeAI1);
        checkAIFrogCollision(snakeAI2);
    }

    private void checkAIFrogCollision(Snake ai) {
        if (!ai.isAlive() || frog.getPosition() == null) return;
        if (ai.getTail().getFirst().equals(frog.getPosition())) {
            ai.addTail();
            ai.addTail();
            frog.eaten();
        }
    }

    private void checkAIFoodCollision(Snake ai) {
        if (!ai.isAlive()) return;
        for (int i = 0; i < food.positions.size(); i++) {
            Point fruit = food.positions.get(i);
            if (ai.getTail().getFirst().equals(fruit)) {
                int type = food.getType(i);
                food.replace(fruit);
                ai.addTail();
                if (type == Food.GOLDEN_APPLE_INDEX) {
                    ai.addTail();
                }
                break;
            }
        }
    }

    private void handleObstacleCollision() {
        Point head = snake.getTail().getFirst();
        for (Point p : obstacle.getObstacles()) {
            if (p.equals(head)) {
                resetGame();
                return;
            }
        }
    }


    public void handleTailCollision() {
        List<Point> tail = snake.getTail();
        Point head = tail.getFirst();
        for (int i = 1; i < tail.size(); i++) {
            if (head.equals(tail.get(i))) {
                resetGame();
                break;
            }
        }

        for (Point p : snakeAI1.getTail()) {
            if (head.equals(p)) {
                resetGame();
                return;
            }
        }
        for (Point p : snakeAI2.getTail()) {
            if (head.equals(p)) {
                resetGame();
                return;
            }
        }
    }

    public void handleWallCollision() {
        Point head = snake.getTail().getFirst();
        if (head.x < 0 || head.y < 0 || head.x >= board.getCellCount() || head.y >= board.getCellCount()) {
            resetGame();
        }
    }

    private void handleAICollisions() {
        handleAICollision(snakeAI1, snakeAI2);
        handleAICollision(snakeAI2, snakeAI1);
    }

    private void handleAICollision(Snake ai, Snake other) {
        if (!ai.isAlive()) return;
        Point head = ai.getTail().getFirst();

        // collision with wall
        if (head.x < 0 || head.y < 0 || head.x >= board.getCellCount() || head.y >= board.getCellCount()) {
            ai.die();
            return;
        }

        // collision with obstacle
        for (Point p : obstacle.getObstacles()) {
            if (p.equals(head)) {
                ai.die();
                return;
            }
        }

        // collision with player's snake
        for (Point p : snake.getTail()) {
            if (head.equals(p)) {
                resetGame();
                return;
            }
        }

        // collision with other AI
        if (other.isAlive()) {
            for (Point p : other.getTail()) {
                if (head.equals(p)) {
                    ai.die();
                    return;
                }
            }
        }

        // collision with own tail
        List<Point> tail = ai.getTail();
        for (int i = 1; i < tail.size(); i++) {
            if (head.equals(tail.get(i))) {
                ai.die();
                return;
            }
        }
    }

    private void updateAISnake(Snake ai, Snake other) {
        if (!ai.isAlive()) return;
        Point head = ai.getTail().getFirst();

        Point target = null;
        int best = Integer.MAX_VALUE;
        for (Point fruit : food.positions) {
            int dist = Math.abs(fruit.x - head.x) + Math.abs(fruit.y - head.y);
            if (dist < best) {
                best = dist;
                target = fruit;
            }
        }
        if (frog.getPosition() != null) {
            int dist = Math.abs(frog.getPosition().x - head.x) + Math.abs(frog.getPosition().y - head.y);
            if (dist < best) {
                best = dist;
                target = frog.getPosition();
            }
        }
        if (target == null) return;

        Direction[] dirs = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
        int[][] moves = {{0,-1},{0,1},{-1,0},{1,0}};
        Direction chosen = ai.getDirection();
        int bestDist = Integer.MAX_VALUE;

        for (int i=0;i<dirs.length;i++) {
            int nx = head.x + moves[i][0];
            int ny = head.y + moves[i][1];
            Point cand = new Point(nx, ny);
            if (!isSafe(cand, ai, other)) continue;
            int d = Math.abs(target.x - nx) + Math.abs(target.y - ny);
            if (d < bestDist) {
                bestDist = d;
                chosen = dirs[i];
            }
        }

        if (bestDist != Integer.MAX_VALUE) {
            ai.moveDirection(chosen);
        }
    }

    private boolean isSafe(Point p, Snake current, Snake other) {
        if (p.x < 0 || p.y < 0 || p.x >= board.getCellCount() || p.y >= board.getCellCount())
            return false;
        if (obstacle.getObstacles().contains(p))
            return false;
        for (Point seg : snake.getTail()) {
            if (seg.equals(p)) return false;
        }
        if (other.isAlive()) {
            for (Point seg : other.getTail()) {
                if (seg.equals(p)) return false;
            }
        }
        List<Point> self = current.getTail();
        for (int i=0; i<self.size()-1; i++) {
            if (self.get(i).equals(p)) return false;
        }
        return true;
    }

    public boolean shouldMove() {
        return snake.moveTime(delayForLevel());
    }

    private float delayForLevel() {
        return switch (gameLevel) {
            case EASY, HARD, MEDIUM -> 0.1f;
        };
    }

    public void onKeyPress(int keyCode) {
        if (gameScreen == GameScreen.GAME) {
            if (keyCode == KeyEvent.VK_UP) snake.moveDirection(Direction.UP);
            if (keyCode == KeyEvent.VK_DOWN) snake.moveDirection(Direction.DOWN);
            if (keyCode == KeyEvent.VK_LEFT) snake.moveDirection(Direction.LEFT);
            if (keyCode == KeyEvent.VK_RIGHT) snake.moveDirection(Direction.RIGHT);
        }

        // Wyjście z SCORE_BOARD do menu
        if (gameScreen == GameScreen.SCORE_BOARD && keyCode == KeyEvent.VK_ESCAPE) {
            hoveredMenuIndex=-1;
            hoveredBackButton = false;
            gameScreen = GameScreen.MENU;
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
        int topMargin = 140; // opcjonalny margines od góry
        int bottomMargin = 140; // i od dołu
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
        scoreDataBase.addScore(score, gameLevel);

        snake.reset();
        snakeAI1.reset();
        snakeAI2.reset();
        obstacle.regenerate();
        food.regenerate();
        frog.eaten();
        score = 0;
        gameScreen = GameScreen.MENU;
    }

    public void onMouseClick(int x, int y, int panelWidth) {
        if (gameScreen == GameScreen.MENU
                && hoveredMenuIndex != -1 && menuYPositions.length > hoveredMenuIndex) {

            int xCenter = panelWidth / 2;
            String text = switch (gameScreen) {
                case MENU -> switch (hoveredMenuIndex) {
                    case 0 -> "EASY";
                    case 1 -> "MEDIUM";
                    case 2 -> "HARD";
                    case 3 -> "SCORE BOARD";
                    default -> "";
                };
                default -> "";
            };

            Font font = new Font("Arial", Font.BOLD, 56);
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
                if (gameScreen == GameScreen.MENU) {
                    switch (hoveredMenuIndex) {
                        case 0 -> {
                            gameLevel = GameLevel.EASY;
                            snake.reset();
                            score = 0;
                            obstacle.setObstacleCount(10);
                            obstacle.regenerate();
                            food.regenerate();
                            gameScreen = GameScreen.GAME;
                        }
                        case 1 -> {
                            gameLevel = GameLevel.MEDIUM;
                            snake.reset();
                            score = 0;
                            obstacle.setObstacleCount(20);
                            obstacle.regenerate();
                            food.regenerate();
                            gameScreen = GameScreen.GAME;
                        }
                        case 2 -> {
                            gameLevel = GameLevel.HARD;
                            snake.reset();
                            score = 0;
                            obstacle.setObstacleCount(30);
                            obstacle.regenerate();
                            food.regenerate();
                            gameScreen = GameScreen.GAME;
                        }
                        case 3 -> gameScreen = GameScreen.SCORE_BOARD;
                    }
                }
            }
        }

        if (gameScreen == GameScreen.SCORE_BOARD
                && backButtonBounds != null && backButtonBounds.contains(x, y)) {
            hoveredMenuIndex = -1;
            hoveredBackButton = false;
            gameScreen = GameScreen.MENU;
        }
    }


    public void onMouseMove(int mouseX, int mouseY, int panelWidth) {
        if (gameScreen == GameScreen.MENU
                && menuYPositions.length > 0) {
            hoveredMenuIndex = -1;
            int xCenter = panelWidth / 2;

            for (int i = 0; i < menuYPositions.length; i++) {
                String text = switch (gameScreen) {
                    case MENU -> switch (i) {
                        case 0 -> "EASY";
                        case 1 -> "MEDIUM";
                        case 2 -> "HARD";
                        case 3 -> "SCORE BOARD";
                        default -> "";
                    };
                    default -> "";
                };

                Font font = new Font("Arial", Font.BOLD, 50);
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

        if (gameScreen == GameScreen.SCORE_BOARD
                && backButtonBounds != null) {
            hoveredBackButton = backButtonBounds.contains(mouseX, mouseY);
        }
    }

    private void drawScoreBoard(Graphics2D g, int panelWidth, int panelHeight) {
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, panelWidth, panelHeight);

        // === Tytuł ===
        String title = "TOP 100 Scores";
        g.setColor(Color.BLACK);
        Font titleFont = new Font("Arial", Font.BOLD, 50);
        g.setFont(titleFont);
        FontMetrics fmTitle = g.getFontMetrics();
        int titleY = 60 + fmTitle.getAscent();
        g.drawString(title, (panelWidth - fmTitle.stringWidth(title)) / 2, titleY);

        // === Czcionka wyników ===
        Font scoreFont = new Font("Monospaced", Font.PLAIN, 20);
        g.setFont(scoreFont);
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();

        // === Obszar wyświetlania wyników ===
        int yStart = titleY + 40;
        int yEnd = panelHeight - 100;

        List<ScoreEntry> entries = scoreDataBase.getScores();
        int maxVisibleLines = (yEnd - yStart) / lineHeight;
        int maxScrollOffset = Math.max(0, entries.size() - maxVisibleLines);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset));

// === Wyświetlanie wyników ===
        int colDateX = 100;
        int colScoreX = 320;
        int colLevelX = 400;

        for (int i = 0; i < maxVisibleLines && (scrollOffset + i) < entries.size(); i++) {
            ScoreEntry entry = entries.get(scrollOffset + i);

            int y = yStart + i * lineHeight + fm.getAscent();

            g.drawString(entry.getDateTime(), colDateX, y);
            g.drawString(String.format("%4d", entry.getScore()), colScoreX, y);
            g.drawString("[" + entry.getLevel().toString() + "]", colLevelX, y);
        }

        // === Przycisk BACK TO MENU ===
        String backText = "BACK TO MENU (ESC)";
        Font backFont = new Font("Arial", Font.BOLD, hoveredBackButton ? 42 : 36);
        g.setFont(backFont);
        FontMetrics fmBack = g.getFontMetrics();

        int textWidth = fmBack.stringWidth(backText);
        int textHeight = fmBack.getHeight();
        int xCenter = panelWidth / 2;
        int backX = xCenter - textWidth / 2;
        int backY = panelHeight - 40;

        g.setColor(Color.BLACK);
        g.drawString(backText, backX, backY);

        int frameX = backX - 20;
        int frameY = backY - textHeight;
        int frameWidth = textWidth + 40;
        int frameHeight = textHeight + 20;

        backButtonBounds = new Rectangle(frameX, frameY, frameWidth, frameHeight);

        if (hoveredBackButton) {
            pictures.drawFrame(g, frameX, frameY, frameWidth, frameHeight);
        }

        // === Scrollbar ===
        if (entries.size() > maxVisibleLines) {
            int scrollbarWidth = 10;
            int scrollbarX = panelWidth - 30;
            int scrollbarY = yStart;
            int scrollbarHeight = yEnd - yStart;

            float ratio = (float) maxVisibleLines / entries.size();
            int thumbHeight = Math.max((int) (scrollbarHeight * ratio), 20);

            float scrollRatio = maxScrollOffset > 0 ? (float) scrollOffset / maxScrollOffset : 0;
            int thumbY = scrollbarY + Math.round((scrollbarHeight - thumbHeight) * scrollRatio);

            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeight);

            g.setColor(Color.DARK_GRAY);
            g.fillRect(scrollbarX, thumbY, scrollbarWidth, thumbHeight);
        }
    }





    public void adjustScrollOffset(int delta) {
        // Dynamiczne wyliczenie linii — zgodne z drawScoreBoard
        Font scoreFont = new Font("Monospaced", Font.PLAIN, 20);
        FontMetrics fm = new Canvas().getFontMetrics(scoreFont);
        int lineHeight = fm.getHeight() + 5;

        int panelHeight = board.getCellCount() * board.getCellSize() + board.getScoreHeight();
        int yStart = 60 + new Font("Arial", Font.BOLD, 50).getSize() + 40; // uproszczone oszacowanie
        int yEnd = panelHeight - 100;
        int maxVisibleLines = (yEnd - yStart) / lineHeight;

        int maxOffset = Math.max(0, scoreDataBase.getScores().size() - maxVisibleLines);

        scrollOffset = Math.max(0, Math.min(scrollOffset + delta, maxOffset));
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public void startDraggingScrollbar(int mouseX, int mouseY, int panelWidth, int panelHeight) {
        int scrollbarX = panelWidth - 30;
        int scrollbarWidth = 10;

        if (mouseX < scrollbarX || mouseX > scrollbarX + scrollbarWidth)
            return; // jeśli kliknięcie poza obszarem scrollbara – ignorujemy
        int scrollbarY = computeYStart(panelHeight);
        int scrollbarHeight = computeYEnd(panelHeight) - scrollbarY;

        int maxVisibleLines = getMaxVisibleLines(panelHeight);
        int totalEntries = scoreDataBase.getScores().size();
        if (totalEntries <= maxVisibleLines) return;

        float ratio = (float) maxVisibleLines / totalEntries;
        int thumbHeight = Math.max((int) (scrollbarHeight * ratio), 20);

        int maxOffset = totalEntries - maxVisibleLines;
        float scrollRatio = (float) scrollOffset / maxOffset;
        int thumbY = scrollbarY + Math.round((scrollbarHeight - thumbHeight) * scrollRatio);

        Rectangle thumbBounds = new Rectangle(scrollbarX, thumbY, scrollbarWidth, thumbHeight);

        if (thumbBounds.contains(mouseX, mouseY)) {
            draggingThumb = true;
            dragOffsetY = mouseY - thumbY;
        } else {
            // Kliknięcie w tło paska: przeskocz suwak
            int clickedThumbY = mouseY - scrollbarY - thumbHeight / 2;
            float newRatio = (float) clickedThumbY / (scrollbarHeight - thumbHeight);
            newRatio = Math.max(0, Math.min(newRatio, 1f));
            scrollOffset = Math.round(newRatio * maxOffset);
        }
    }

    public void dragScrollbar(int mouseY, int panelHeight) {
        if (!draggingThumb) return;

        int scrollbarY = computeYStart(panelHeight);
        int scrollbarHeight = computeYEnd(panelHeight) - scrollbarY;

        int maxVisibleLines = getMaxVisibleLines(panelHeight);
        int maxOffset = Math.max(0, scoreDataBase.getScores().size() - maxVisibleLines);
        if (maxOffset == 0) return;

        int thumbY = mouseY - dragOffsetY;
        thumbY = Math.max(scrollbarY, Math.min(thumbY, scrollbarY + scrollbarHeight - 20)); // ograniczenia

        float scrollRatio = (float) (thumbY - scrollbarY) / (scrollbarHeight - 20);
        scrollOffset = Math.round(scrollRatio * maxOffset);
    }

    private int computeYStart(int panelHeight) {
        FontMetrics fmTitle = new Canvas().getFontMetrics(new Font("Arial", Font.BOLD, 50));
        return 60 + fmTitle.getHeight() + 40;
    }

    private int computeYEnd(int panelHeight) {
        return panelHeight - 100;
    }

    private int getMaxVisibleLines(int panelHeight) {
        FontMetrics fm = new Canvas().getFontMetrics(new Font("Monospaced", Font.PLAIN, 20));
        int lineHeight = fm.getHeight();
        return (computeYEnd(panelHeight) - computeYStart(panelHeight)) / lineHeight;
    }
    public void stopDraggingScrollbar() {
        draggingThumb = false;
    }
}


