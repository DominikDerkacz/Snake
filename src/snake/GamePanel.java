package snake;

import snake.core.Board;
import snake.core.Game;
import snake.core.Pictures;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements KeyListener {
    private final Game game;
    private final Timer timer;

    public GamePanel() {
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        Board board = new Board();
        Pictures pictures = new Pictures();
        this.game = new Game(board, pictures);

        int delayMs = 16; // ok. 60 FPS
        timer = new Timer(delayMs, e -> {
            if (game.shouldMove(0.1f)) {
                game.update();
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.draw((Graphics2D) g, getWidth(), getHeight());
    }

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
}
