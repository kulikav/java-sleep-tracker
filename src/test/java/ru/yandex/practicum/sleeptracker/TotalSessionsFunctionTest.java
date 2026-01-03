package ru.yandex.practicum.sleeptracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

public class TotalSessionsFunctionTest {

    @Test
    public void testEmptySessionList() {
        // Сценарий: пустой список сессий
        // Ожидаемый результат: 0 сессий

        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult result = function.analyze(List.of()); // пустой список

        assertEquals("Общее количество сессий сна за период", result.getDescription());
        assertEquals(0L, result.getResult());
    }

    @Test
    public void testMultipleSessions() {
        // Сценарий: список из 4 сессий разного качества и длительности
        // Ожидаемый результат: 4 сессии

        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 15),
                        LocalDateTime.of(2025, 10, 2, 8, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 8, 0),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 30),
                        LocalDateTime.of(2025, 10, 3, 15, 20),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 23, 30),
                        LocalDateTime.of(2025, 10, 4, 6, 20),
                        SleepQuality.BAD
                )
        );

        TotalSessionsFunction function = new TotalSessionsFunction();
        SleepAnalysisResult result = function.analyze(sessions);

        assertEquals("Общее количество сессий сна за период", result.getDescription());
        assertEquals(4L, result.getResult());
    }
}


