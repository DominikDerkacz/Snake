package snake.core;

import java.awt.*;

public class Board {
    private final Color darkGreen = new Color(58, 200, 28);
    private final Color lightGreen = new Color(85, 237, 38);
    private final int cellSize = 30;
    private final int cellCount = 20;
    private final int scoreHeight = 100;

    public int getCellSize() {
        return cellSize;
    }

    public int getCellCount() {
        return cellCount;
    }

    public int getScoreHeight() {
        return scoreHeight;
    }

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

        // Pasek wyniku (żółty prostokąt)
        g.setColor(Color.YELLOW);
        g.fillRect(0, cellCount * cellSize, cellCount * cellSize, scoreHeight);

        // Czarna linia oddzielająca planszę od wyniku
        g.setColor(Color.BLACK);
        g.fillRect(0, cellCount * cellSize, cellCount * cellSize, 5);
    }
}
