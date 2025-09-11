package ru.otus.executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceOfNumbers {
    private static final Logger logger = LoggerFactory.getLogger(SequenceOfNumbers.class);
    private int lastNumber = 1;
    private int lastThreadNum = 1;
    private boolean increase = true;

    public static void main(String[] args) {
        SequenceOfNumbers sequenceOfNumbers = new SequenceOfNumbers();
        new Thread(() -> sequenceOfNumbers.action(0)).start();
        new Thread(() -> sequenceOfNumbers.action(1)).start();
    }

    private synchronized void action(int threadNum) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while (threadNum == lastThreadNum) {
                    this.wait();
                }
                logger.info("{} ", lastNumber);

                if (threadNum == 1) {
                    changeLastNumber();
                    checkAndChangeIncrease();
                }
                lastThreadNum = threadNum;
                sleep();
                notifyAll();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void changeLastNumber() {
        if (increase) {
            lastNumber++;
        } else {
            lastNumber--;
        }
    }

    private void checkAndChangeIncrease() {
        if (lastNumber >= 10) {
            increase = false;
        }
        if (lastNumber <= 1) {
            increase = true;
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
