package ru.otus.aop.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("java:S112")
class Ioc {
    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    private Ioc() {}

    static TestLoggingInterface createTestLogging(TestLoggingInterface target) {
        InvocationHandler handler = new DemoInvocationHandler(target);
        return (TestLoggingInterface) Proxy.newProxyInstance(
                Ioc.class.getClassLoader(), new Class<?>[] {TestLoggingInterface.class}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final TestLoggingInterface target;
        private final Set<Method> methodsLog;

        DemoInvocationHandler(TestLoggingInterface target) {
            this.target = target;
            this.methodsLog = Arrays.stream(target.getClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Log.class))
                    .collect(Collectors.toSet());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (shouldMethodLog(method)) {
                logger.info("executed method:{}, param: {}", method.getName(), args);
            }
            return method.invoke(target, args);
        }

        private boolean shouldMethodLog(Method method) {
            try {
                Method targetMethod = target.getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
                return methodsLog.contains(targetMethod);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
