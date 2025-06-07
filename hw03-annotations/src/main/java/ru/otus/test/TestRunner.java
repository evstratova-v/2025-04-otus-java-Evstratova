package ru.otus.test;

import static ru.otus.reflection.ReflectionHelper.getClassForName;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    private TestRunner() {}

    public static void runTests(String className) {
        Class<?> clazz = getClassForName(className);
        TestExecution testExecution = new TestExecution(clazz);
        TestResults testResults = testExecution.execute();

        logger.info(
                "TEST RESULT: count of all: {}, passed: {}, failed: {}, skipped: {}",
                testResults.passedTests().size()
                        + testResults.failedTests().size()
                        + testResults.skippedTests().size(),
                testResults.passedTests().size(),
                testResults.failedTests().size(),
                testResults.skippedTests().size());

        logResultDetails(testResults.passedTests(), "PASSED");
        logResultDetails(testResults.failedTests(), "FAILED");
        logResultDetails(testResults.skippedTests(), "SKIPPED");
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
