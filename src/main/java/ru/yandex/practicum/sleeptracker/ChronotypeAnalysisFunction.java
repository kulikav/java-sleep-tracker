package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChronotypeAnalysisFunction implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Хронотип пользователя", "Голубь");
        }

        // Считаем количество ночей по типам
        Map<Chronotype, Long> typeCounts = sessions.stream()
                .filter(this::isNightSession) // только ночные сессии
                .map(this::classifyNight)
                .collect(Collectors.groupingBy(
                        chronotype -> chronotype,
                        Collectors.counting()
                ));

        // Определяем доминирующий тип
        Chronotype userType = determineDominantType(typeCounts);

        return new SleepAnalysisResult("Хронотип пользователя", userType.getDescription());
    }

    // Проверяет, является ли сессия ночной (пересекает интервал 00:00–06:00)
    private boolean isNightSession(SleepingSession session) {
        LocalTime startTime = session.getStartTime().toLocalTime();
        LocalTime endTime = session.getEndTime().toLocalTime();

        // Случай 1: сессия через полночь (начало до 00:00, конец после)
        if (session.getStartTime().toLocalDate()
                .isBefore(session.getEndTime().toLocalDate())) {
            return true;
        }

        // Случай 2: конец в интервале 00:00–06:00
        return endTime.isAfter(LocalTime.MIDNIGHT) && !endTime.isAfter(LocalTime.of(6, 0));
    }

    // Классифицирует ночную сессию по хронотипу
    private Chronotype classifyNight(SleepingSession session) {
        LocalTime sleepTime = session.getStartTime().toLocalTime();
        LocalTime wakeTime = session.getEndTime().toLocalTime();

        if (sleepTime.isAfter(LocalTime.of(23, 0)) && wakeTime.isAfter(LocalTime.of(9, 0))) {
            return Chronotype.OWL;
        } else if (sleepTime.isBefore(LocalTime.of(22, 0)) && wakeTime.isBefore(LocalTime.of(7, 0))) {
            return Chronotype.LARK;
        }
        return Chronotype.SPARROW;
    }

    // Определяет доминирующий хронотип по счётчикам
    private Chronotype determineDominantType(Map<Chronotype, Long> counts) {
        long owlCount = counts.getOrDefault(Chronotype.OWL, 0L);
        long larkCount = counts.getOrDefault(Chronotype.LARK, 0L);
        long sparrowCount = counts.getOrDefault(Chronotype.SPARROW, 0L);

        // Если два типа имеют одинаковое максимальное количество — выбираем SPARROW
        if ((owlCount == larkCount && owlCount >= sparrowCount) ||
                (owlCount == sparrowCount && owlCount >= larkCount) ||
                (larkCount == sparrowCount && larkCount >= owlCount)) {
            return Chronotype.SPARROW;
        }

        // Иначе — тип с максимальным количеством
        if (owlCount > larkCount && owlCount > sparrowCount) {
            return Chronotype.OWL;
        } else if (larkCount > owlCount && larkCount > sparrowCount) {
            return Chronotype.LARK;
        } else {
            return Chronotype.SPARROW;
        }
    }
}

