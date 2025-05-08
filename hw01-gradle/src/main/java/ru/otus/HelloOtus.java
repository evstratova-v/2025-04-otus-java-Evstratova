package ru.otus;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloOtus {
    private static final Logger logger = LoggerFactory.getLogger(HelloOtus.class);

    public static void main(String... args) {
        List<String> words = Lists.newArrayList("Otus", null, "Hello");

        Iterables.removeIf(words, Predicates.isNull());
        List<String> reversed = Lists.reverse(words);

        logger.atInfo()
                .setMessage("{}")
                .addArgument(() -> Joiner.on(", ").join(reversed))
                .log();
    }
}
