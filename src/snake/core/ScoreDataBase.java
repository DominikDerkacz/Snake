package snake.core;

import snake.enums.GameLevel;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Klasa {@code ScoreDataBase} odpowiada za zarządzanie listą wyników graczy.
 * Wyniki są zapisywane i odczytywane z lokalnego pliku tekstowego {@code scores.db}.
 * Przechowywane są maksymalnie 100 najlepszych wyników.
 */
public class ScoreDataBase {

    /** Nazwa pliku, w którym przechowywane są wyniki. */
    private static final String FILE_NAME = "scores.db";

    /** Maksymalna liczba wyników przechowywanych w pliku. */
    private static final int MAX_ENTRIES = 100;

    /** Lista wyników graczy. */
    private final List<ScoreEntry> scores = new ArrayList<>();

    /**
     * Konstruktor klasy {@code ScoreDataBase}.
     * Automatycznie wczytuje dane z pliku po utworzeniu obiektu.
     */
    public ScoreDataBase() {
        load();
    }

    /**
     * Dodaje nowy wynik do listy wyników i zapisuje zmiany do pliku.
     * Lista zostaje posortowana malejąco i skrócona do 100 najlepszych wpisów.
     *
     * @param score wynik gracza
     * @param level poziom trudności, na którym wynik został osiągnięty
     */
    public void addScore(int score, GameLevel level) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        scores.add(new ScoreEntry(date, score, level));

        scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

        if (scores.size() > MAX_ENTRIES) {
            scores.subList(MAX_ENTRIES, scores.size()).clear();
        }

        save();
    }

    /**
     * Zwraca aktualną listę wyników.
     *
     * @return lista obiektów {@code ScoreEntry}
     */
    public List<ScoreEntry> getScores() {
        return scores;
    }

    /**
     * Wczytuje wyniki z pliku do pamięci.
     * Jeśli plik nie istnieje lub jest błędny – nie zgłasza wyjątku.
     * Dane są sortowane i ograniczane do maksymalnej liczby rekordów.
     */
    private void load() {
        scores.clear();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 2) {
                    int score = Integer.parseInt(parts[1]);
                    GameLevel level = (parts.length >= 3) ? GameLevel.valueOf(parts[2]) : GameLevel.EASY;
                    scores.add(new ScoreEntry(parts[0], score, level));
                }
            }
            scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
            if (scores.size() > MAX_ENTRIES) {
                scores.subList(MAX_ENTRIES, scores.size()).clear();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Zapisuje bieżącą listę wyników do pliku.
     * Każdy wpis zapisywany jest w formacie: data;wynik;poziom.
     */
    private void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            for (ScoreEntry entry : scores) {
                bw.write(entry.getDateTime() + ";" + entry.getScore() + ";" + entry.getLevel().name());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
