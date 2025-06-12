package snake.core;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Klasa {@code Food} reprezentuje obiekty jedzenia na planszy gry Snake.
 * Obsługuje losowanie pozycji owoców, ich rysowanie, kolizje z przeszkodami i wężami,
 * a także prostą animację pulsującą.
 */
public class Food {
    private final Board board;
    private final Pictures pictures;
    private final int fruitCount;
    public List<Point> positions = new ArrayList<>();
    private final List<Integer> types = new ArrayList<>();

    /**
     * Stała określająca indeks złotego jabłka w zestawie grafik owoców.
     */
    public static final int GOLDEN_APPLE_INDEX = 3;

    private float fruitScale = 1.0f;
    private float scaleDirection = 0.09f;

    private Obstacle obstacle;
    private final List<Snake> snakes;

    /**
     * Tworzy nowy obiekt {@code Food} z określoną liczbą owoców oraz wężami do uwzględnienia w kolizjach.
     *
     * @param board       obiekt planszy gry
     * @param pictures    obiekt rysujący grafiki owoców
     * @param obstacle    przeszkody na planszy
     * @param fruitCount  liczba owoców do wygenerowania
     * @param snakes      lista węży, których ogony mają być uwzględnione przy losowaniu pozycji
     */
    public Food(Board board, Pictures pictures, Obstacle obstacle, int fruitCount, List<Snake> snakes) {
        this.board = board;
        this.pictures = pictures;
        this.obstacle = obstacle;
        this.fruitCount = fruitCount;
        this.snakes = new ArrayList<>(snakes);
        regenerate();
    }

    /**
     * Tworzy nowy obiekt {@code Food} bez węży (np. dla trybu jednoosobowego).
     *
     * @param board       obiekt planszy gry
     * @param pictures    obiekt rysujący grafiki owoców
     * @param obstacle    przeszkody na planszy
     * @param fruitCount  liczba owoców do wygenerowania
     */
    public Food(Board board, Pictures pictures, Obstacle obstacle, int fruitCount) {
        this(board, pictures, obstacle, fruitCount, List.of());
    }

    /**
     * Losuje nową pozycję dla owocu z uwzględnieniem przeszkód, innych owoców oraz pozycji węży.
     *
     * @return losowo wybrany punkt na planszy, który jest wolny
     */
    public Point getRandomPos() {
        Random random = new Random();
        Point p;
        do {
            int x = random.nextInt(board.getCellCount());
            int y = random.nextInt(board.getCellCount());
            p = new Point(x, y);
        } while (obstacle.getObstacles().contains(p) || positions.contains(p) || onSnake(p));
        return p;
    }

    /**
     * Sprawdza, czy dany punkt pokrywa się z ogonem któregokolwiek węża.
     *
     * @param p punkt do sprawdzenia
     * @return {@code true}, jeśli punkt koliduje z wężem; {@code false} w przeciwnym razie
     */
    private boolean onSnake(Point p) {
        for (Snake s : snakes) {
            if (!s.isAlive()) continue;
            for (Point seg : s.getTail()) {
                if (seg.equals(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Losuje indeks typu owocu do narysowania.
     *
     * @return indeks graficzny owocu
     */
    private int randomFruit() {
        Random rand = new Random();
        return rand.nextInt(pictures.getFruitCount());
    }

    /**
     * Regeneruje wszystkie owoce – losuje nowe pozycje i typy.
     */
    public void regenerate() {
        positions.clear();
        types.clear();
        for (int i = 0; i < fruitCount; i++) {
            positions.add(getRandomPos());
            types.add(randomFruit());
        }
    }

    /**
     * Zastępuje zjedzony owoc nowym w losowej pozycji i z nowym typem.
     *
     * @param eaten pozycja zjedzonego owocu
     */
    public void replace(Point eaten) {
        int idx = positions.indexOf(eaten);
        if (idx != -1) {
            positions.set(idx, getRandomPos());
            types.set(idx, randomFruit());
        }
    }

    /**
     * Zwraca typ owocu (indeks graficzny) o danym indeksie.
     *
     * @param index indeks owocu
     * @return typ owocu
     */
    public int getType(int index) {
        return types.get(index);
    }

    /**
     * Rysuje wszystkie owoce na planszy z uwzględnieniem skalowania (animacji).
     *
     * @param g obiekt {@code Graphics2D} do rysowania
     */
    public void draw(Graphics2D g) {
        int baseSize = board.getCellSize();
        int scaledSize = (int) (baseSize * fruitScale);
        int offset = (baseSize - scaledSize) / 2;

        for (int i = 0; i < positions.size(); i++) {
            Point p = positions.get(i);
            int drawX = p.x * baseSize + offset;
            int drawY = p.y * baseSize + offset;
            int type = types.get(i);
            pictures.drawFruit(g, drawX, drawY, scaledSize, scaledSize, type);
        }
    }

    /**
     * Aktualizuje skalowanie owoców do animacji pulsowania.
     */
    public void updateAnimation() {
        fruitScale += scaleDirection;
        if (fruitScale >= 1.4f || fruitScale <= 1.0f) {
            scaleDirection *= -1;
        }
    }
}
