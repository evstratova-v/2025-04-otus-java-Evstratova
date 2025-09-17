package ru.otus.services.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final BlockingQueue<SensorData> dataBuffer;
    private final Lock lock;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
        this.dataBuffer = new PriorityBlockingQueue<>(bufferSize, Comparator.comparing(SensorData::getMeasurementTime));
        this.lock = new ReentrantLock();
    }

    @Override
    public void process(SensorData data) {
        boolean result = dataBuffer.offer(data);
        if (!result) {
            log.warn("Очередь обработки данных переполнена");
        }
        log.info("Обработка данных в буфер: {}", data);
        if (dataBuffer.size() >= bufferSize) {
            flush();
        }
    }

    public void flush() {
        lock.lock();
        try {
            List<SensorData> bufferedData = new ArrayList<>();
            dataBuffer.drainTo(bufferedData, bufferSize);
            if (!bufferedData.isEmpty()) {
                writer.writeBufferedData(bufferedData);
            }
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
