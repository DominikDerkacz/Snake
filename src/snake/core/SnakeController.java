package snake.core;

/**
 * Thread controller for a snake.
 */
public class SnakeController implements Runnable {
    private final Snake snake;
    private final float delay;
    private volatile boolean running = true;

    public SnakeController(Snake snake, float delay) {
        this.snake = snake;
        this.delay = delay;
    }

    @Override
    public void run() {
        while (running) {
            if (snake.moveTime(delay)) {
                snake.update();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void stop() {
        running = false;
    }
}
