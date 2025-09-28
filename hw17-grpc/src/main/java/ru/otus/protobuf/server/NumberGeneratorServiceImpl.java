package ru.otus.protobuf.server;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import ru.otus.protobuf.NumberGeneratorServiceGrpc;
import ru.otus.protobuf.NumberRequest;
import ru.otus.protobuf.NumberResponse;

@SuppressWarnings("java:S2095")
public class NumberGeneratorServiceImpl extends NumberGeneratorServiceGrpc.NumberGeneratorServiceImplBase {

    @Override
    public void generate(NumberRequest numberRequest, StreamObserver<NumberResponse> responseObserver) {
        var currentValue = new AtomicLong(numberRequest.getFirstValue());
        var executor = Executors.newSingleThreadScheduledExecutor();
        Runnable command = () -> {
            long value = currentValue.incrementAndGet();
            var response = NumberResponse.newBuilder().setNumber(value).build();
            responseObserver.onNext(response);
            if (value == numberRequest.getLastValue()) {
                executor.shutdown();
                responseObserver.onCompleted();
            }
        };
        executor.scheduleAtFixedRate(command, 0, 2, TimeUnit.SECONDS);
    }
}
