package snake.core;

import snake.enums.GameLevel;

public class ScoreEntry {
    private final String dateTime;
    private final int score;
    private final GameLevel level;

    public ScoreEntry(String dateTime, int score, GameLevel level) {
        this.dateTime = dateTime;
        this.score = score;
        this.level = level;
    }

    public ScoreEntry(String dateTime, int score) {
        this(dateTime, score, GameLevel.EASY); // domyślnie dla starych wpisów
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getScore() {
        return score;
    }

    public GameLevel getLevel() {
        return level;
    }
}
