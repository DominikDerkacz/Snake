package snake.core;

import snake.enums.Direction;
import snake.enums.SnakeType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa {@code Snake} reprezentuje węża w grze Snake.
 * Odpowiada za jego pozycję, ruch, rysowanie, długość, kierunek oraz stan (żywy/martwy).
 * Obsługuje zarówno węża gracza, jak i AI.
 */
public class Snake {

    /** Obiekt do rysowania grafik. */
    private final Pictures pictures;

    /** Plansza gry. */
    private final Board board;

    /** Typ węża – gracz lub AI. */
    private final SnakeType type;

    /** Początkowy ogon węża. */
    private final List<Point> tailStart;

    /** Czy wąż jest żywy. */
    private boolean alive = true;

    /** Lista segmentów ogona (pierwszy element to głowa). */
    public List<Point> tail;

    /** Wektor ruchu – określa kierunek przesunięcia głowy. */
    private Point move = new Point(1, 0);

    /** Kierunek ruchu węża. */
    private Direction direction = Direction.RIGHT;

    /** Czy gra została rozpoczęta przez gracza. */
    private boolean gameRunning = false;

    /** Pomocniczy znacznik czasu do pomiaru opóźnienia ruchu. */
    private float lastTime = 0f;

    /** Kąt obrotu głowy węża (dla rysowania). */
    private int angle = 0;

    /** Czas ostatniego ruchu (w milisekundach). */
    private long lastMoveMillis = System.currentTimeMillis();

    /**
     * Konstruktor domyślny węża gracza z ustalonym pozycjonowaniem startowym.
     *
     * @param board plansza gry
     * @param pictures zasoby graficzne
     */
    public Snake(Board board, Pictures pictures) {
        this(board, pictures, SnakeType.PLAYER, List.of(
                new Point(7, 6),
                new Point(6, 6),
                new Point(5, 6)
        ));
    }

    /**
     * Konstruktor węża z niestandardowym typem i startową pozycją.
     *
     * @param board plansza gry
     * @param pictures zasoby graficzne
     * @param type typ węża (gracz, AI1, AI2)
     * @param start lista punktów startowego ogona
     */
    public Snake(Board board, Pictures pictures, SnakeType type, List<Point> start) {
        this.board = board;
        this.pictures = pictures;
        this.type = type;
        this.tailStart = new ArrayList<>(start);
        this.tail = new ArrayList<>(tailStart);
    }

    /**
     * Rysuje węża (głowę oraz ogon) na planszy.
     *
     * @param g kontekst graficzny
     */
    public void draw(Graphics2D g) {
        if (!alive || tail.isEmpty()) return;
        int cellSize = board.getCellSize();
        Point head = tail.get(0);
        int x = head.x * cellSize;
        int y = head.y * cellSize;

        // Rysuj głowę
        switch (type) {
            case AI1 -> pictures.drawSnakeAI1Head(g, x, y, angle, cellSize, cellSize);
            case AI2 -> pictures.drawSnakeAI2Head(g, x, y, angle, cellSize, cellSize);
            default -> pictures.drawSnakeHead(g, x, y, angle, cellSize, cellSize);
        }

        // Rysuj ogon
        for (int i = 1; i < tail.size(); i++) {
            Point segment = tail.get(i);
            switch (type) {
                case AI1 -> pictures.drawSnakeAI1(g, segment.x * cellSize, segment.y * cellSize, cellSize, cellSize);
                case AI2 -> pictures.drawSnakeAI2(g, segment.x * cellSize, segment.y * cellSize, cellSize, cellSize);
                default -> pictures.drawSnake(g, segment.x * cellSize, segment.y * cellSize, cellSize, cellSize);
            }
        }
    }

    /**
     * Sprawdza, czy minął czas do wykonania kolejnego ruchu.
     *
     * @param delay opóźnienie między ruchami (sekundy)
     * @return true, jeśli ruch powinien się odbyć
     */
    public boolean moveTime(float delay) {
        long current = System.currentTimeMillis();
        if ((current - lastMoveMillis) / 1000.0f >= delay) {
            lastMoveMillis = current;
            return true;
        }
        return false;
    }

    /**
     * Aktualizuje pozycję węża – przesuwa ogon i dodaje nową głowę.
     */
    public void update() {
        if (gameRunning && alive) {
            tail.remove(tail.size() - 1);
            Point newHead = new Point(tail.get(0).x + move.x, tail.get(0).y + move.y);
            tail.add(0, newHead);
        }
    }

    /**
     * Ustawia nowy kierunek ruchu, o ile nie jest przeciwny do aktualnego.
     * Włącza tryb gry przy pierwszym ruchu.
     *
     * @param dir nowy kierunek ruchu
     */
    public void moveDirection(Direction dir) {
        if (!alive) return;
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

    /**
     * Dodaje jeden segment do ogona węża.
     */
    public void addTail() {
        if (alive && !tail.isEmpty()) {
            tail.add(new Point(tail.get(tail.size() - 1)));
        }
    }

    /**
     * Resetuje węża do jego stanu początkowego.
     */
    public void reset() {
        tail = new ArrayList<>(tailStart);
        direction = Direction.RIGHT;
        move.setLocation(1, 0);
        angle = 0;
        gameRunning = false;
        alive = true;
    }

    /**
     * Sprawdza, czy gra została uruchomiona.
     *
     * @return true, jeśli gra jest aktywna
     */
    public boolean isGameRunning() {
        return gameRunning;
    }

    /**
     * Zwraca aktualny ogon węża.
     *
     * @return lista punktów ogona
     */
    public List<Point> getTail() {
        return tail;
    }

    /**
     * Zwraca aktualny kierunek ruchu węża.
     *
     * @return aktualny kierunek
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Sprawdza, czy wąż żyje.
     *
     * @return true, jeśli wąż jest żywy
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Oznacza węża jako martwego i czyści jego ogon.
     */
    public void die() {
        alive = false;
        tail.clear();
    }
}
