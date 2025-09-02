package ru.otus.appcontainer.exception;

public class ComponentAlreadyExistsException extends RuntimeException {

    public ComponentAlreadyExistsException(String appComponentName) {
        super("Component with name %s already exists".formatted(appComponentName));
    }
}
