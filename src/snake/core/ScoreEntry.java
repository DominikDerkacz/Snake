package snake.core;

public class ScoreEntry {
    private final String dateTime;
    private final int score;

    public ScoreEntry(String dateTime, int score) {
        this.dateTime = dateTime;
        this.score = score;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getScore() {
        return score;
    }
}
