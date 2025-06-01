package ru.otus.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotations.After;
import ru.otus.annotations.Before;
import ru.otus.annotations.Test;

@SuppressWarnings({"java:S125", "java:S112"})
public class AnnotationsTest {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationsTest.class);

    @Before
    void before1() {
        logger.info("@Before 1, hashcode: {}", hashCode());
        // throw new RuntimeException("Exception in @Before 1");
    }

    @Before
    void before2() {
        logger.info("@Before 2, hashcode: {}", hashCode());
        // throw new RuntimeException("Exception in @Before 2");
    }

    @Test
    void test1() {
        logger.info("@Test 1, hashcode: {}", hashCode());
        throw new RuntimeException("Exception in @Test 1");
    }

    @Test
    void test2() {
        logger.info("@Test 2, hashcode: {}", hashCode());
    }

    @Test
    void test3() {
        logger.info("@Test 3, hashcode: {}", hashCode());
    }

    @After
    void after1() {
        logger.info("@After 1, hashcode: {}", hashCode());
        // throw new RuntimeException("Exception in @After 1");
    }

    @After
    void after2() {
        logger.info("@After 2, hashcode: {}", hashCode());
        // throw new RuntimeException("Exception in @After 2");
    }
}
