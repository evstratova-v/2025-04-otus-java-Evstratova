package ru.otus;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.model.ObjectForMessage;
import ru.otus.processor.LoggerProcessor;
import ru.otus.processor.homework.ProcessorEvenSecond;
import ru.otus.processor.homework.ProcessorSwapField11Field12;

public class HomeWork {

    private static final Logger logger = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
        var processors = List.of(
                new ProcessorEvenSecond(LocalDateTime::now), new LoggerProcessor(new ProcessorSwapField11Field12()));

        var complexProcessor = new ComplexProcessor(processors, ex -> logger.error(ex.getMessage()));
        var historyListener = new HistoryListener();
        complexProcessor.addListener(historyListener);

        long id = 1L;
        var objectForMessage = new ObjectForMessage();
        objectForMessage.setData(List.of("a", "b"));

        var message = new Message.Builder(id)
                .field11("field11")
                .field12("field12")
                .field13(objectForMessage)
                .build();

        var result1 = complexProcessor.handle(message);
        logger.info("result1:{}", result1);
        logger.info("history message:{}", historyListener.findMessageById(id).orElse(null));

        result1.getField13().setData(null);
        complexProcessor.removeListener(historyListener);

        var result2 = complexProcessor.handle(result1);
        logger.info("result2:{}", result2);
        logger.info("history message:{}", historyListener.findMessageById(id).orElse(null));
    }
}
