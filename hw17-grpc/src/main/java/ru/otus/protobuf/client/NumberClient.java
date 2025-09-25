package ru.otus.protobuf.client;

import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.NumberGeneratorServiceGrpc;
import ru.otus.protobuf.NumberRequest;

public class NumberClient {
    private static final Logger log = LoggerFactory.getLogger(NumberClient.class);

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;

    private long value = 0;

    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();

        NumberGeneratorServiceGrpc.NumberGeneratorServiceStub stub = NumberGeneratorServiceGrpc.newStub(channel);

        var latch = new CountDownLatch(1);
        new NumberClient().clientAction(stub, latch);
        latch.await();
        channel.shutdown();
    }

    private void clientAction(NumberGeneratorServiceGrpc.NumberGeneratorServiceStub stub, CountDownLatch latch) {
        var observer = new ClientStreamObserver(latch);
        var request =
                NumberRequest.newBuilder().setFirstValue(0).setLastValue(30).build();
        stub.generate(request, observer);

        log.info("numbers Client is starting...");
        for (int i = 0; i <= 50; i++) {
            value = value + observer.getLastValueAndReset() + 1;
            log.info("currentValue:{}", value);
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
