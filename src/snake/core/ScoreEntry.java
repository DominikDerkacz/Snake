package snake.core;

import snake.enums.GameLevel;

/**
 * Klasa {@code ScoreEntry} reprezentuje pojedynczy wpis w tablicy wyników.
 * Przechowuje datę i godzinę uzyskania wyniku, liczbę punktów oraz poziom trudności.
 */
public class ScoreEntry {

    /** Data i godzina uzyskania wyniku w formacie "yyyy-MM-dd HH:mm". */
    private final String dateTime;

    /** Liczba punktów zdobytych przez gracza. */
    private final int score;

    /** Poziom trudności, na którym został osiągnięty wynik. */
    private final GameLevel level;

    /**
     * Tworzy nowy wpis z określoną datą, wynikiem i poziomem trudności.
     *
     * @param dateTime data i godzina w formacie tekstowym
     * @param score liczba punktów
     * @param level poziom trudności gry
     */
    public ScoreEntry(String dateTime, int score, GameLevel level) {
        this.dateTime = dateTime;
        this.score = score;
        this.level = level;
    }

    /**
     * Tworzy nowy wpis z domyślnym poziomem trudności (EASY),
     * wykorzystywane dla starszych wpisów bez tej informacji.
     *
     * @param dateTime data i godzina
     * @param score liczba punktów
     */
    public ScoreEntry(String dateTime, int score) {
        this(dateTime, score, GameLevel.EASY);
    }

    /**
     * Zwraca datę i godzinę wpisu.
     *
     * @return data i godzina jako tekst
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Zwraca liczbę punktów.
     *
     * @return wynik punktowy
     */
    public int getScore() {
        return score;
    }

    /**
     * Zwraca poziom trudności.
     *
     * @return poziom gry, na którym uzyskano wynik
     */
    public GameLevel getLevel() {
        return level;
    }
}
