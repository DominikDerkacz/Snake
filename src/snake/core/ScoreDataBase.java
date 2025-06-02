package snake.core;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScoreDataBase {
    private static final String FILE_NAME = "scores.db";
    private static final int MAX_ENTRIES = 100;
    private final List<ScoreEntry> scores = new ArrayList<>();

    public ScoreDataBase() {
        load();
    }

    public void addScore(int score) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        scores.add(0, new ScoreEntry(date, score)); // dodaj na początek
        if (scores.size() > MAX_ENTRIES) {
            scores.remove(scores.size() - 1); // usuń najstarszy
        }
        save();
    }

    public List<ScoreEntry> getScores() {
        return scores;
    }

    private void load() {
        scores.clear();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    scores.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1])));
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            for (ScoreEntry entry : scores) {
                bw.write(entry.getDateTime() + ";" + entry.getScore());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
