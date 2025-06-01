package ru.otus.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({"java:S3011", "java:S112"})
public class ReflectionHelper {
    private ReflectionHelper() {}

    public static Object callMethod(Object object, Method method) throws InvocationTargetException {
        try {
            method.setAccessible(true);
            return method.invoke(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T instantiate(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
