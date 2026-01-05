package ru.yandex.practicum.sleeptracker;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

public class SleepTrackerAppTest {

    @TempDir
    Path tempDir;

    private SleepTrackerApp app;

    @Test
    public void testReadSleepLog_ValidFile() throws Exception {
        // Сценарий: файл с корректными данными о сне
        Path testFile = tempDir.resolve("sleep_log.txt");
        Files.writeString(testFile, """
                01.10.25 22:15;02.10.25 08:00;GOOD
                02.10.25 23:00;03.10.25 08:00;NORMAL
                03.10.25 14:30;03.10.25 15:20;NORMAL
                """);

        SleepTrackerApp app = new SleepTrackerApp();
        List<SleepingSession> sessions = app.readSleepLog(testFile.toString());

        assertEquals(3, sessions.size());

        // Проверяем первую сессию
        SleepingSession first = sessions.get(0);
        assertEquals(LocalDateTime.of(2025, 10, 1, 22, 15), first.getStartTime());
        assertEquals(LocalDateTime.of(2025, 10, 2, 8, 0), first.getEndTime());
        assertEquals(SleepQuality.GOOD, first.getQuality());

        // Проверяем вторую сессию
        SleepingSession second = sessions.get(1);
        assertEquals(LocalDateTime.of(2025, 10, 2, 23, 0), second.getStartTime());
        assertEquals(LocalDateTime.of(2025, 10, 3, 8, 0), second.getEndTime());
        assertEquals(SleepQuality.NORMAL, second.getQuality());
    }

    @Test
    public void testReadSleepLog_EmptyFile() throws Exception {
        // Сценарий: пустой файл
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.createFile(emptyFile);

        app = new SleepTrackerApp();
        List<SleepingSession> sessions = app.readSleepLog(emptyFile.toString());

        assertTrue(sessions.isEmpty());
    }

    @Test
    public void testReadSleepLog_FileNotFound() {
        // Сценарий: файл не существует
        app = new SleepTrackerApp();

        assertThrows(IOException.class, () -> {
            app.readSleepLog("non_existent_file.txt");
        });
    }

    @Test
    public void testRegisterAnalysisFunction() {
        // Сценарий: регистрация нескольких функций
        app = new SleepTrackerApp();

        SleepAnalysisFunction func1 = session -> new SleepAnalysisResult("Test 1", 1);
        SleepAnalysisFunction func2 = session -> new SleepAnalysisResult("Test 2", 2);

        app.registerAnalysisFunction(func1);
        app.registerAnalysisFunction(func2);

        assertEquals(2, app.analysisFunctions.size());
        assertTrue(app.analysisFunctions.contains(func1));
        assertTrue(app.analysisFunctions.contains(func2));
    }

    @Test
    public void testRunAnalysis_WithFunctions() {
        // Сценарий: запуск анализа с двумя тестовыми функциями
        app = new SleepTrackerApp();

        // Регистрируем две функции
        app.registerAnalysisFunction(session -> new SleepAnalysisResult("Count", (long) session.size()));
        app.registerAnalysisFunction(session -> new SleepAnalysisResult("First Quality",
                session.isEmpty() ? "N/A" : session.get(0).getQuality().toString()));

        // Подготавливаем тестовые сессии
        List<SleepingSession> testSessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 1, 22, 15),
                        LocalDateTime.of(2025, 10, 2, 8, 0),
                        SleepQuality.GOOD),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 23, 0),
                        LocalDateTime.of(2025, 10, 3, 8, 0),
                        SleepQuality.NORMAL)
        );

        // Захватываем вывод через System.out (используя ByteArrayOutputStream)
        var outContent = new java.io.ByteArrayOutputStream();
        var originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));


        try {
            app.runAnalysis(testSessions);
        } finally {
            System.setOut(originalOut);
        }

        String output = outContent.toString();

        // Проверяем, что обе функции выполнились и вывели результат
        assertTrue(output.contains("Count: 2"));
        assertTrue(output.contains("First Quality: GOOD"));
    }

    @Test
    public void testMain_WithValidFile() throws Exception {
        // Сценарий: вызов main с корректным файлом
        // (Этот тест имитирует запуск main, но в рамках юнит‑теста)

        Path testFile = tempDir.resolve("test_input.txt");
        Files.writeString(testFile, """
                01.10.25 22:15;02.10.25 08:00;GOOD
                02.10.25 23:00;03.10.25 08:00;NORMAL
                """);

        // Имитируем аргументы командной строки
        String[] cliArgs = {testFile.toString()};

        // Захватываем System.out
        var outContent = new java.io.ByteArrayOutputStream();
        var originalOut = System.out;
        System.setOut(new java.io.PrintStream(outContent));

        try {
            SleepTrackerApp.main(cliArgs);
        } catch (Exception e) {
            fail("main() выбросил исключение: " + e.getMessage());
        } finally {
            System.setOut(originalOut);
        }

        String output = outContent.toString();

        // Проверяем, что хотя бы одна функция выполнилась (например, TotalSessionsFunction)
        assertTrue(output.contains("Общее количество сессий сна за период"));
        // И что данные считаны (минимум 2 сессии)
        assertTrue(output.contains(": 2") || output.contains(":2"));
    }

    @Test
    public void testMain_WithoutArguments() {
        // Сценарий: main вызван без аргументов
        var errContent = new java.io.ByteArrayOutputStream();
        var originalErr = System.err;
        System.setErr(new java.io.PrintStream(errContent));

        try {
            SleepTrackerApp.main(new String[0]);
        } finally {
            System.setErr(originalErr);
        }

        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Usage: java SleepTrackerApp <path-to-log-file>"));
    }
}
