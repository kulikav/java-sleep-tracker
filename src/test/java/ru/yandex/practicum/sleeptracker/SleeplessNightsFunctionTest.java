package ru.yandex.practicum.sleeptracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

public class SleeplessNightsFunctionTest {
    private final SleeplessNightsFunction function = new SleeplessNightsFunction();

    @Test
    void testNormalSleep() {
        // Лог с 10:00 (startDate = вчера). 1 ночь в периоде, сон был.
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2026, 1, 1, 23, 0),
                        LocalDateTime.of(2026, 1, 2, 7, 0), SleepQuality.GOOD)
        );
        assertEquals(0L, function.analyze(sessions).getResult());
    }

    @Test
    void testSleeplessNight() {
        // Сон только днем (7:00 - 11:00). Ночь 00:00-06:00 пустая.
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2026, 1, 1, 7, 0),
                        LocalDateTime.of(2026, 1, 1, 11, 0), SleepQuality.NORMAL));
        assertEquals(1L, function.analyze(sessions).getResult());
    }

    @Test
    void testMonthTransition() {
        // Переход с января на февраль (31 января - 1 февраля)
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2026, 1, 31, 22, 0),
                        LocalDateTime.of(2026, 2, 1, 8, 0), SleepQuality.GOOD)
        );
        // 1 ночь, она покрыта сном.
        assertEquals(0L, function.analyze(sessions).getResult());
    }

    @Test
    void testLateStartNextNight() {
        // Старт после 12:00 (15:00). Первая ночь для анализа — следующая.
        List<SleepingSession> sessions = List.of(
                new SleepingSession(LocalDateTime.of(2026, 1, 1, 15, 0),
                        LocalDateTime.of(2026, 1, 1, 16, 0), SleepQuality.NORMAL),
                new SleepingSession(LocalDateTime.of(2026, 1, 3, 10, 0),
                        LocalDateTime.of(2026, 1, 3, 11, 0), SleepQuality.NORMAL)
        );
        // startDate = 02.01, endDate = 03.01. Всего 1 ночь. Сна 00-06 не было.
        assertEquals(1L, function.analyze(sessions).getResult());
    }
}

