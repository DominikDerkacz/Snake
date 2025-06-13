package snake.enums;

/**
 * Enum {@code GameScreen} reprezentuje aktualny ekran gry.
 * Służy do przełączania między widokami gry, menu i tablicą wyników.
 */
public enum GameScreen {
    /** Ekran menu głównego. */
    MENU,

    /** Ekran rozgrywki. */
    GAME,

    /** Ekran tablicy wyników. */
    SCORE_BOARD,

    /** Ekran wyboru trybu gry wieloosobowej. */
    MULTIPLAYER_MENU
}
