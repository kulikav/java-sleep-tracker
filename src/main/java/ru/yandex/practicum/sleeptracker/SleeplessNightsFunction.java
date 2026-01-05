package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SleeplessNightsFunction implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult analyze(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0L);
        }

        // 1. Определяем границы периода логирования
        LocalDateTime firstStart = sessions.stream()
                .map(SleepingSession::getStartTime)
                .min(Comparator.naturalOrder()).orElseThrow();
        LocalDateTime lastEnd = sessions.stream()
                .map(SleepingSession::getEndTime)
                .max(Comparator.naturalOrder()).orElseThrow();

        // 2. Определяем startDate по условию (до/после 12:00)
        LocalDate startDate = firstStart.toLocalDate();
        if (firstStart.toLocalTime().isAfter(LocalTime.NOON)) {
            startDate = startDate.plusDays(1);
        } else {
            startDate = startDate.minusDays(1);
        }
        LocalDate sDate = startDate;
        LocalDate endDate = lastEnd.toLocalDate();

        // 3. Считаем общее количество ночей.
        // Используем ChronoUnit.DAYS, так как Period.getDays() не учитывает месяцы.
        long totalNights = ChronoUnit.DAYS.between(startDate, endDate);
        if (totalNights <= 0) return new SleepAnalysisResult("Количество бессонных ночей", 0L);

        // 4. Собираем даты ночей, когда пользователь спал (00:00 - 06:00)
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .map(this::getNightsCoveredBySession)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        // Фильтруем только те ночи, которые входят в наш отчетный период
        long sleptNightsInPeriod = nightsWithSleep.stream()
                .filter(date -> !date.isBefore(sDate) && date.isBefore(endDate))
                .count();

        long sleeplessNights = totalNights - sleptNightsInPeriod;

        return new SleepAnalysisResult("Количество бессонных ночей", Math.max(0, sleeplessNights));
    }

    private Set<LocalDate> getNightsCoveredBySession(SleepingSession session) {
        LocalDateTime start = session.getStartTime();
        LocalDateTime end = session.getEndTime();

        // Ночь N — это интервал от 00:00 до 06:00 даты N.
        // Проверяем, пересекается ли сессия с [date 00:00, date 06:00]
        LocalDate current = start.toLocalDate();
        LocalDate last = end.toLocalDate();

        Set<LocalDate> covered = current.datesUntil(last.plusDays(1))
                .filter(date -> {
                    LocalDateTime nightStart = date.atStartOfDay();
                    LocalDateTime nightEnd = date.atTime(6, 0);
                    return start.isBefore(nightEnd) && end.isAfter(nightStart);
                })
                .collect(Collectors.toSet());
        return covered;
    }
}

