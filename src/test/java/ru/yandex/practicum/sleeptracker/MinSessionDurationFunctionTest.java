package ru.yandex.practicum.sleeptracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

public class MinSessionDurationFunctionTest {

    @Test
    public void testSingleSession() {
        SleepingSession session = new SleepingSession(
                LocalDateTime.of(2025, 10, 1, 22, 15),
                LocalDateTime.of(2025, 10, 2, 8, 0),
                SleepQuality.GOOD
        );

        MinSessionDurationFunction function = new MinSessionDurationFunction();
        SleepAnalysisResult result = function.analyze(List.of(session));

        assertEquals(585L, result.getResult());
    }

    @Test
    public void testMultipleSessions() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15), LocalDateTime.of(2025, 10, 2, 8, 0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 3, 14, 30), LocalDateTime.of(2025, 10, 3, 15, 20), SleepQuality.NORMAL)
        );

        MinSessionDurationFunction function = new MinSessionDurationFunction();
        SleepAnalysisResult result = function.analyze(sessions);

        assertEquals(50L, result.getResult()); // 50 минут (вторая сессия)
    }
}

