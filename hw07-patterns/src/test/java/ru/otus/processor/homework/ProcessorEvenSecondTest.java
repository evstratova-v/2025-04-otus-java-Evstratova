package ru.otus.processor.homework;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;

class ProcessorEvenSecondTest {

    @Test
    @DisplayName("Тестируем исключение в чётную секунду")
    void evenSecondTest() {
        var message = new Message.Builder(1L).build();
        var processorEvenSecond = new ProcessorEvenSecond(() -> LocalDateTime.MIN);

        assertThrows(EvenSecondException.class, () -> processorEvenSecond.process(message));
    }

    @Test
    @DisplayName("Тестируем нечётную секунду")
    void oddSecondTest() {
        var message = new Message.Builder(1L).build();
        var processorEvenSecond = new ProcessorEvenSecond(() -> LocalDateTime.MIN.plusSeconds(1));

        assertDoesNotThrow(() -> processorEvenSecond.process(message));
    }
}
