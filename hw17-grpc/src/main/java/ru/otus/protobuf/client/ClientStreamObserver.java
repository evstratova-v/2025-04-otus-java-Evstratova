package ru.otus.protobuf.client;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.NumberResponse;

public class ClientStreamObserver implements StreamObserver<NumberResponse> {
    private static final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);

    private final CountDownLatch latch;

    private long lastValue = 0;

    public ClientStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(NumberResponse numberResponse) {
        long newValue = numberResponse.getNumber();
        log.info("new value:{}", newValue);
        setLastValue(newValue);
    }

    @Override
    public void onError(Throwable e) {
        log.error("got error", e);
    }

    @Override
    public void onCompleted() {
        log.info("request completed");
        latch.countDown();
    }

    public synchronized long getLastValueAndReset() {
        long value = lastValue;
        this.lastValue = 0;
        return value;
    }

    private synchronized void setLastValue(long lastValue) {
        this.lastValue = lastValue;
    }
}
