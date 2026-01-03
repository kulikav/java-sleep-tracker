package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class AvgSessionDurationFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Средняя продолжительность сессии (мин)", 0);
        }

        double avgDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .average()
                .orElse(0.0);

        return new SleepAnalysisResult("Средняя продолжительность сессии (мин)", Math.round(avgDuration));
    }
}

