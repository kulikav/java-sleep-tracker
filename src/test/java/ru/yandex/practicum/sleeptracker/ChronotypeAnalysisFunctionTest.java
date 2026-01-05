package ru.yandex.practicum.sleeptracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;

public class ChronotypeAnalysisFunctionTest {

    private final ChronotypeAnalysisFunction function = new ChronotypeAnalysisFunction();

    @Test
    public void testAllOwlNights() {
        // Сценарий: все ночи соответствуют типу «сова» (засыпание после 23:00, пробуждение после 9:00)
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 23, 30),
                        LocalDateTime.of(2025, 10, 2, 9, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 45),
                        LocalDateTime.of(2025, 10, 3, 10, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = function.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals(Chronotype.OWL.getDescription(), result.getResult());
    }

    @Test
    public void testAllLarkNights() {
        // Сценарий: все ночи — «жаворонки» (засыпание до 22:00, пробуждение до 7:00)
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 21, 0),
                        LocalDateTime.of(2025, 10, 2, 6, 30),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 20, 45),
                        LocalDateTime.of(2025, 10, 3, 6, 45),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = function.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals(Chronotype.LARK.getDescription(), result.getResult());
    }

    @Test
    public void testMixedTypesWithSparrowDominance() {
        // Сценарий: по одной ночи каждого типа → доминирует «голубь» (равное количество)
        List<SleepingSession> sessions = List.of(
                // Сова
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 30), LocalDateTime.of(2025, 10, 2, 9, 30), SleepQuality.GOOD),
                // Жаворонок
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 21, 0), LocalDateTime.of(2025, 10, 3, 6, 30), SleepQuality.NORMAL),
                // Голубь (не подходит ни под сову, ни под жаворонка)
                new SleepingSession(LocalDateTime.of(2025, 10, 3, 22, 30), LocalDateTime.of(2025, 10, 4, 7, 30), SleepQuality.BAD)
        );

        SleepAnalysisResult result = function.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals(Chronotype.SPARROW.getDescription(), result.getResult());
    }

    @Test
    public void testMostlySparrow() {
        // Сценарий: большинство ночей — «голуби», остальные типы представлены меньше
        List<SleepingSession> sessions = List.of(
                // Голубь (2 ночи)
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15), LocalDateTime.of(2025, 10, 2, 7, 15), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 22, 45), LocalDateTime.of(2025, 10, 3, 8, 0), SleepQuality.NORMAL),
                // Сова (1 ночь)
                new SleepingSession(LocalDateTime.of(2025, 10, 3, 23, 30), LocalDateTime.of(2025, 10, 4, 9, 30), SleepQuality.BAD)
        );

        SleepAnalysisResult result = function.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals(Chronotype.SPARROW.getDescription(), result.getResult());
    }

    @Test
    public void testEmptySessionList() {
        // Сценарий: нет сессий сна → по умолчанию «голубь»
        SleepAnalysisResult result = function.analyze(List.of());

        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals(Chronotype.SPARROW.getDescription(), result.getResult());
    }

    @Test
    public void testOnlyDaytimeSessions() {
        // Сценарий: все сессии — дневные (не пересекают 00:00–06:00) → игнорируются, результат — «голубь»
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 14, 0),
                        LocalDateTime.of(2025, 10, 1, 16, 0),
                        SleepQuality.GOOD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 10, 0),
                        LocalDateTime.of(2025, 10, 2, 12, 0),
                        SleepQuality.NORMAL
                )
        );

        SleepAnalysisResult result = function.analyze(sessions);
        assertEquals("Хронотип пользователя", result.getDescription());
        assertEquals(Chronotype.SPARROW.getDescription(), result.getResult());
    }
}
