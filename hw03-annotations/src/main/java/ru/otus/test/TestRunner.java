package ru.otus.test;

import static ru.otus.reflection.ReflectionHelper.callMethod;
import static ru.otus.reflection.ReflectionHelper.getClassForName;
import static ru.otus.reflection.ReflectionHelper.instantiate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    private TestRunner() {}

    public static void runTests(String className) {
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();

        Class<?> clazz = getClassForName(className);
        prepareTestsMethods(clazz, beforeMethods, testMethods, afterMethods);

        List<Method> passedTests = new ArrayList<>();
        List<Method> failedTests = new ArrayList<>();
        List<Method> skippedTests = new ArrayList<>();

        logger.info("START TESTS '{}', count of tests: {}\n", clazz.getSimpleName(), testMethods.size());

        for (Method testMethod : testMethods) {
            logger.info("------------------------------\nSTART TEST with name '{}'\n", testMethod.getName());

            var instance = instantiate(clazz);
            boolean isBeforeSuccess = runBeforeMethods(instance, beforeMethods);
            if (isBeforeSuccess) {
                runTestMethod(instance, testMethod, passedTests, failedTests);
            } else {
                skippedTests.add(testMethod);
            }
            runAfterMethods(instance, afterMethods);

            logger.info("END TEST with name '{}'\n------------------------------\n", testMethod.getName());
        }
        logger.info(
                "TEST RESULT: count of all: {}, passed: {}, failed: {}, skipped: {}",
                testMethods.size(),
                passedTests.size(),
                failedTests.size(),
                skippedTests.size());

        logResultDetails(passedTests, "PASSED");
        logResultDetails(failedTests, "FAILED");
        logResultDetails(skippedTests, "SKIPPED");
    }

    private static void prepareTestsMethods(
            Class<?> clazz, List<Method> beforeMethods, List<Method> testMethods, List<Method> afterMethods) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethods.add(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            } else if (method.isAnnotationPresent(After.class)) {
                afterMethods.add(method);
            }
        }
    }

    private static boolean runBeforeMethods(Object instance, List<Method> beforeMethods) {
        for (Method beforeMethod : beforeMethods) {
            try {
                callMethod(instance, beforeMethod);
                logger.info("Run before method '{}' success\n", beforeMethod.getName());
            } catch (InvocationTargetException e) {
                logger.error(
                        "Before method '{}' failed,\nException message: {}\n",
                        beforeMethod.getName(),
                        e.getTargetException().getMessage());
                return false;
            }
        }
        return true;
    }

    private static void runTestMethod(
            Object instance, Method testMethod, List<Method> passedTests, List<Method> failedTests) {
        try {
            callMethod(instance, testMethod);
            logger.info("Test '{}' passed\n", testMethod.getName());
            passedTests.add(testMethod);
        } catch (InvocationTargetException e) {
            logger.error(
                    "Test '{}' failed,\nException message: {}\n",
                    testMethod.getName(),
                    e.getTargetException().getMessage());
            failedTests.add(testMethod);
        }
    }

    private static void runAfterMethods(Object instance, List<Method> afterMethods) {
        for (Method afterMethod : afterMethods) {
            try {
                callMethod(instance, afterMethod);
                logger.info("Run after method '{}' success\n", afterMethod.getName());
            } catch (InvocationTargetException e) {
                logger.error(
                        "After method '{}' failed,\nException message: {}\n",
                        afterMethod.getName(),
                        e.getTargetException().getMessage());
            }
        }
    }

    private static void logResultDetails(List<Method> result, String status) {
        if (!result.isEmpty()) {
            logger.atInfo()
                    .setMessage("TESTS {}: {}")
                    .addArgument(status)
                    .addArgument(() -> result.stream().map(Method::getName).collect(Collectors.joining(", ")))
                    .log();
        }
    }
}
