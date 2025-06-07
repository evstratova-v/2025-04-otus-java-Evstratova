package ru.otus.test;

import java.lang.reflect.Method;
import java.util.List;

public record TestingMethods(List<Method> beforeMethods, List<Method> testMethods, List<Method> afterMethods) {}
