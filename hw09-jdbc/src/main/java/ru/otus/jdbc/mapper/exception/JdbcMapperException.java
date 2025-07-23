package ru.otus.jdbc.mapper.exception;

public class JdbcMapperException extends RuntimeException {

    public JdbcMapperException(String message) {
        super(message);
    }

    public JdbcMapperException(String message, Throwable cause) {
        super(message, cause);
    }
}
