package snake;

import snake.core.Board;
import snake.core.Game;
import snake.core.Pictures;
import snake.enums.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Klasa {@code GamePanel} jest głównym panelem graficznym gry Snake.
 * Odpowiada za wyświetlanie gry, obsługę klawiatury, myszy oraz uruchamianie pętli gry (repaint + update).
 * Implementuje obsługę wejścia użytkownika i przekazuje zdarzenia do klasy {@link Game}.
 */
public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    /** Główna logika gry. */
    private final Game game;

    /**
     * Konstruktor. Inicjalizuje planszę, zasoby graficzne i logikę gry.
     * Ustawia obsługę zdarzeń i uruchamia pętlę odświeżania (~60 FPS).
     */
    public GamePanel() {
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

        Board board = new Board();
        Pictures pictures = new Pictures();
        this.game = new Game(board, pictures);

        int delayMs = 16; // ok. 60 FPS
        Timer timer = new Timer(delayMs, e -> {
            if (game.shouldMove()) {
                game.update();
            }
            repaint();
        });
        timer.start();
    }

    /**
     * Nadpisana metoda {@code paintComponent}, odpowiedzialna za rysowanie zawartości panelu.
     *
     * @param g kontekst graficzny
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.draw((Graphics2D) g, getWidth(), getHeight());
    }

    // === Obsługa klawiatury ===

    /**
     * Obsługuje naciśnięcie klawisza i przekazuje je do logiki gry.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        game.onKeyPress(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // nieużywane
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // nieużywane
    }

    // === Obsługa kliknięć myszy ===

    /**
     * Obsługuje kliknięcie myszy i przekazuje je do logiki gry.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        game.onMouseClick(e.getX(), e.getY(), getWidth());
    }

    /**
     * Obsługuje naciśnięcie przycisku myszy.
     * Aktywuje przeciąganie paska przewijania w scoreboardzie.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (game.getGameScreen() == GameScreen.SCORE_BOARD) {
            game.startDraggingScrollbar(e.getX(), e.getY(), getWidth(), getHeight());
        }
    }

    /**
     * Zatrzymuje przeciąganie scrollbara po puszczeniu przycisku myszy.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        game.stopDraggingScrollbar();
    }

    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // === Obsługa ruchu myszy ===

    /**
     * Obsługuje ruch myszy – aktualizuje pozycję hovera w menu/scoreboardzie.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        game.onMouseMove(e.getX(), e.getY(), getWidth());
        repaint(); // odśwież, aby ramka była aktualna
    }

    /**
     * Obsługuje przeciąganie scrollbara w widoku SCORE_BOARD.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (game.getGameScreen() == GameScreen.SCORE_BOARD) {
            game.dragScrollbar(e.getY(), getHeight());
            repaint();
        }
    }

    // === Obsługa scrolla myszy ===

    /**
     * Obsługuje przewijanie kółkiem myszy w scoreboardzie.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (game.getGameScreen() == GameScreen.SCORE_BOARD) {
            game.adjustScrollOffset(e.getWheelRotation());
            repaint();
        }
    }
}
