import snake.GamePanel;
import snake.core.Board;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa {@code Main} zawiera metodę główną uruchamiającą grę Snake.
 * Tworzy planszę, ustala rozmiar okna i wyświetla główny panel gry.
 */
public class Main {

    /**
     * Punkt wejścia do programu. Uruchamia aplikację Snake w osobnym wątku graficznym.
     *
     * @param args argumenty wiersza poleceń (nieużywane)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Utwórz planszę, żeby obliczyć rozmiar
            Board board = new Board();
            int width = board.getCellCount() * board.getCellSize();
            int height = width + board.getScoreHeight();

            // 2. Utwórz panel i ustaw preferowany rozmiar
            GamePanel panel = new GamePanel();
            panel.setPreferredSize(new Dimension(width, height));

            // 3. Utwórz okno i przypnij panel
            JFrame frame = new JFrame("Snake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setContentPane(panel);
            frame.pack(); // dopasowuje się do preferowanego rozmiaru
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
