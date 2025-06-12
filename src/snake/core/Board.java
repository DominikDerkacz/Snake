package snake.core;

import java.awt.*;

/**
 * Klasa {@code Board} odpowiada za rysowanie planszy gry Snake.
 * Tworzy szachownicę w odcieniach zieleni oraz dolny pasek na wynik.
 */
public class Board {
    /**
     * Ciemnozielony kolor używany do rysowania szachownicy.
     */
    private final Color darkGreen = new Color(58, 200, 28);

    /**
     * Jasnozielony kolor używany do rysowania szachownicy.
     */
    private final Color lightGreen = new Color(85, 237, 38);

    /**
     * Rozmiar jednej komórki planszy w pikselach.
     */
    private final int cellSize = 26;

    /**
     * Liczba komórek w jednym wierszu lub kolumnie planszy (plansza jest kwadratowa).
     */
    private final int cellCount = 26;

    /**
     * Wysokość paska wyników poniżej planszy w pikselach.
     */
    private final int scoreHeight = 100;

    /**
     * Zwraca rozmiar jednej komórki planszy.
     *
     * @return szerokość i wysokość komórki w pikselach
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Zwraca liczbę komórek w wierszu lub kolumnie planszy.
     *
     * @return liczba komórek w jednym wymiarze planszy
     */
    public int getCellCount() {
        return cellCount;
    }

    /**
     * Zwraca wysokość paska wyników poniżej planszy.
     *
     * @return wysokość w pikselach
     */
    public int getScoreHeight() {
        return scoreHeight;
    }

    /**
     * Rysuje planszę gry jako szachownicę oraz pasek wyników.
     *
     * @param g obiekt {@code Graphics2D} do rysowania
     */
    public void drawBoard(Graphics2D g) {
        for (int y = 0; y < cellCount; y++) {
            for (int x = 0; x < cellCount; x++) {
                if ((x + y) % 2 == 0) {
                    g.setColor(darkGreen);
                } else {
                    g.setColor(lightGreen);
                }
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }

        // Pasek wyniku
        g.setColor(Color.YELLOW);
        g.fillRect(0, cellCount * cellSize, cellCount * cellSize, scoreHeight);

        // Czarna linia oddzielająca planszę od paska wyniku
        g.setColor(Color.BLACK);
        g.fillRect(0, cellCount * cellSize, cellCount * cellSize, 5);
    }
}
