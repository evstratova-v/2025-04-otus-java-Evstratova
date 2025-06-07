package ru.otus.test;

import java.lang.reflect.Method;
import java.util.List;

public record TestResults(List<Method> passedTests, List<Method> failedTests, List<Method> skippedTests) {}
