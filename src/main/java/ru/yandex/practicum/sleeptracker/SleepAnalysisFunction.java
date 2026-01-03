package ru.yandex.practicum.sleeptracker;

import java.util.List;

public interface SleepAnalysisFunction {
    SleepAnalysisResult analyze(List<SleepingSession> sessions);
}

