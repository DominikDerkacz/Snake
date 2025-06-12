package snake;

import snake.core.Board;
import snake.core.Game;
import snake.core.Pictures;
import snake.enums.GameScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private final Game game;
    private final Timer timer;


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
    @Override
    public void mouseClicked(MouseEvent e) {
        game.onMouseClick(e.getX(), e.getY(), getWidth(), getHeight());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (game.getGameScreen() == GameScreen.SCORE_BOARD) {
            game.startDraggingScrollbar(e.getX(), e.getY(), getWidth(), getHeight());
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        game.stopDraggingScrollbar();
    }
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        game.onMouseMove(e.getX(), e.getY(), getWidth(), getHeight());
        repaint(); // odśwież panel aby ramka się przesunęła
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (game.getGameScreen() == GameScreen.SCORE_BOARD) {
            game.dragScrollbar(e.getY(), getHeight());
            repaint();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (game.getGameScreen() == GameScreen.SCORE_BOARD) {
            game.adjustScrollOffset(e.getWheelRotation());
            repaint();
        }
    }
}
