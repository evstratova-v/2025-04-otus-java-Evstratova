package ru.otus.appcontainer.exception;

import java.lang.reflect.Method;

public class ProcessConfigException extends RuntimeException {

    public ProcessConfigException(Class<?> configClass, Throwable e) {
        super("Process config exception, config class: %s".formatted(configClass.getName()), e);
    }

    public ProcessConfigException(Method componentMethod, Throwable e) {
        super("Process config exception, component method: %s".formatted(componentMethod.getName()), e);
    }
}
