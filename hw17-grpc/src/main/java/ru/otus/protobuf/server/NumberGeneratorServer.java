package ru.otus.protobuf.server;

import io.grpc.ServerBuilder;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumberGeneratorServer {
    private static final Logger log = LoggerFactory.getLogger(NumberGeneratorServer.class);

    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {
        var remoteNumberGeneratorService = new NumberGeneratorServiceImpl();

        var server = ServerBuilder.forPort(SERVER_PORT)
                .addService(remoteNumberGeneratorService)
                .build();
        server.start();
        log.info("server waiting for client connections...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("received shutdown request");
            server.shutdown();
            log.info("server stopped");
        }));
        server.awaitTermination();
    }
}
