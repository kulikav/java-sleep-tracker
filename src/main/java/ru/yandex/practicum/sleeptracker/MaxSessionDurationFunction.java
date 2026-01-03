package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MaxSessionDurationFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Максимальная продолжительность сессии (мин)", 0);
        }

        long maxDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .max()
                .orElse(0);

        return new SleepAnalysisResult("Максимальная продолжительность сессии (мин)", maxDuration);
    }
}

