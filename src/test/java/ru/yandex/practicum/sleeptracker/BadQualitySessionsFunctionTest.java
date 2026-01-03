package ru.yandex.practicum.sleeptracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

public class BadQualitySessionsFunctionTest {

    @Test
    public void testNoBadSessions() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15), LocalDateTime.of(2025, 10, 2, 8, 0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 3, 14, 30), LocalDateTime.of(2025, 10, 3, 15, 20), SleepQuality.NORMAL)
        );

        BadQualitySessionsFunction function = new BadQualitySessionsFunction();
        SleepAnalysisResult result = function.analyze(sessions);

        assertEquals(0L, result.getResult());
    }

    @Test
    public void testWithBadSessions() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15), LocalDateTime.of(2025, 10, 2, 8, 0), SleepQuality.BAD),
                new SleepingSession(LocalDateTime.of(2025, 10, 3, 14, 30), LocalDateTime.of(2025, 10, 3, 15, 20), SleepQuality.BAD)
        );

        BadQualitySessionsFunction function = new BadQualitySessionsFunction();
        SleepAnalysisResult result = function.analyze(sessions);

        assertEquals(2L, result.getResult());
    }
}