package ru.otus.aop.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("java:S112")
class Ioc {
    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    private Ioc() {}

    static TestLoggingInterface createTestLogging() {
        InvocationHandler handler = new DemoInvocationHandler(new TestLogging());
        return (TestLoggingInterface) Proxy.newProxyInstance(
                Ioc.class.getClassLoader(), new Class<?>[] {TestLoggingInterface.class}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final TestLoggingInterface myClass;
        private final Map<Method, Boolean> methodsLog;

        DemoInvocationHandler(TestLoggingInterface myClass) {
            this.myClass = myClass;
            this.methodsLog = new HashMap<>();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (shouldMethodLog(method)) {
                logger.info("executed method:{}, param: {}", method.getName(), args);
            }
            return method.invoke(myClass, args);
        }

        private boolean shouldMethodLog(Method method) {
            if (methodsLog.containsKey(method)) {
                return methodsLog.get(method);
            } else {
                boolean isLogAnnotationPresent;
                try {
                    isLogAnnotationPresent = myClass.getClass()
                            .getDeclaredMethod(method.getName(), method.getParameterTypes())
                            .isAnnotationPresent(Log.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                methodsLog.put(method, isLogAnnotationPresent);
                return isLogAnnotationPresent;
            }
        }
    }
}
