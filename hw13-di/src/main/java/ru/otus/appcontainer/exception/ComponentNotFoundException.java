package ru.otus.appcontainer.exception;

public class ComponentNotFoundException extends RuntimeException {

    public ComponentNotFoundException(Class<?> componentClass) {
        super("Component not found for class: %s".formatted(componentClass.getName()));
    }

    public ComponentNotFoundException(String componentName) {
        super("Component not found for name: %s".formatted(componentName));
    }
}
