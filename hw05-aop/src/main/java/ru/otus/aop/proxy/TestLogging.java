package ru.otus.aop.proxy;

@SuppressWarnings("java:S1186")
public class TestLogging implements TestLoggingInterface {

    @Log
    @Override
    public void calculation(int param1) {}

    @Log
    @Override
    public void calculation(int param1, int param2) {}

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {}
}
