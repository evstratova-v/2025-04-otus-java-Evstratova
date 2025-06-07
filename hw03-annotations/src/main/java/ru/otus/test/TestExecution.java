package ru.otus.test;

import static ru.otus.reflection.ReflectionHelper.callMethod;
import static ru.otus.reflection.ReflectionHelper.instantiate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

public class TestExecution {
    private static final Logger logger = LoggerFactory.getLogger(TestExecution.class);

    private final Class<?> testClass;

    private final TestingMethods testingMethods;

    public TestExecution(Class<?> testClass) {
        this.testClass = testClass;
        this.testingMethods = prepareTestingMethods(testClass);
    }

    public TestResults execute() {
        List<Method> passedTests = new ArrayList<>();
        List<Method> failedTests = new ArrayList<>();
        List<Method> skippedTests = new ArrayList<>();

        logger.atInfo()
                .setMessage("START TESTS '{}', count of tests: {}\n")
                .addArgument(testClass.getSimpleName())
                .addArgument(testingMethods.testMethods().size())
                .log();

        for (Method testMethod : testingMethods.testMethods()) {
            logger.info("------------------------------\nSTART TEST with name '{}'\n", testMethod.getName());

            var instance = instantiate(testClass);
            boolean isBeforeSuccess = runBeforeMethods(instance);
            if (isBeforeSuccess) {
                boolean isTestPassed = runTestMethod(instance, testMethod);
                if (isTestPassed) {
                    passedTests.add(testMethod);
                } else {
                    failedTests.add(testMethod);
                }
            } else {
                skippedTests.add(testMethod);
            }
            runAfterMethods(instance);

            logger.info("END TEST with name '{}'\n------------------------------\n", testMethod.getName());
        }
        return new TestResults(passedTests, failedTests, skippedTests);
    }

    private TestingMethods prepareTestingMethods(Class<?> clazz) {
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethods.add(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            } else if (method.isAnnotationPresent(After.class)) {
                afterMethods.add(method);
            }
        }
        return new TestingMethods(beforeMethods, testMethods, afterMethods);
    }

    private boolean runBeforeMethods(Object instance) {
        for (Method beforeMethod : testingMethods.beforeMethods()) {
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

    private boolean runTestMethod(Object instance, Method testMethod) {
        try {
            callMethod(instance, testMethod);
            logger.info("Test '{}' passed\n", testMethod.getName());
            return true;
        } catch (InvocationTargetException e) {
            logger.error(
                    "Test '{}' failed,\nException message: {}\n",
                    testMethod.getName(),
                    e.getTargetException().getMessage());
            return false;
        }
    }

    private void runAfterMethods(Object instance) {
        for (Method afterMethod : testingMethods.afterMethods()) {
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
}
