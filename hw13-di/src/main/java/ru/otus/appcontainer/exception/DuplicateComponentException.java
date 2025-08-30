package ru.otus.appcontainer.exception;

public class DuplicateComponentException extends RuntimeException {

    public DuplicateComponentException(Class<?> componentClass) {
        super("More than one component was found for class: %s".formatted(componentClass.getName()));
    }
}
