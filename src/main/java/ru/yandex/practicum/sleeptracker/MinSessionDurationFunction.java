package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class MinSessionDurationFunction implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Минимальная продолжительность сессии (мин)", 0);
        }

        long minDuration = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .min()
                .orElse(0);

        return new SleepAnalysisResult("Минимальная продолжительность сессии (мин)", minDuration);
    }
}


