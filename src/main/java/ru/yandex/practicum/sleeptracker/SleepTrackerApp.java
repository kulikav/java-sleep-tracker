package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    List<SleepAnalysisFunction> analysisFunctions = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java SleepTrackerApp <path-to-log-file>");
            return;
        }

        String filePath = args[0];
        SleepTrackerApp app = new SleepTrackerApp();

        // Регистрация аналитических функций
        app.registerAnalysisFunction(new TotalSessionsFunction());
        app.registerAnalysisFunction(new MinSessionDurationFunction());
        app.registerAnalysisFunction(new MaxSessionDurationFunction());
        app.registerAnalysisFunction(new AvgSessionDurationFunction());
        app.registerAnalysisFunction(new BadQualitySessionsFunction());
        app.registerAnalysisFunction(new SleeplessNightsFunction());
        app.registerAnalysisFunction(new ChronotypeAnalysisFunction());

        try {
            List<SleepingSession> sessions = app.readSleepLog(filePath);
            app.runAnalysis(sessions);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    List<SleepingSession> readSleepLog(String filePath) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

        return Files.lines(Paths.get(filePath))
                .map(line -> line.split(";"))
                .map(parts -> {
                    LocalDateTime start = LocalDateTime.parse(parts[0], formatter);
                    LocalDateTime end = LocalDateTime.parse(parts[1], formatter);
                    SleepQuality quality = SleepQuality.valueOf(parts[2]);
                    return new SleepingSession(start, end, quality);
                })
                .collect(Collectors.toList());
    }

    void runAnalysis(List<SleepingSession> sessions) {
        analysisFunctions.stream()
                .map(function -> function.analyze(sessions))
                .forEach(System.out::println);
    }

    void registerAnalysisFunction(SleepAnalysisFunction function) {
        analysisFunctions.add(function);
    }
}
