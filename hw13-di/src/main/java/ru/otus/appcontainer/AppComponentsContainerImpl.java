package ru.otus.appcontainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;
import ru.otus.appcontainer.exception.ComponentAlreadyExistsException;
import ru.otus.appcontainer.exception.ComponentNotFoundException;
import ru.otus.appcontainer.exception.DuplicateComponentException;
import ru.otus.appcontainer.exception.ProcessConfigException;

@SuppressWarnings({"squid:S1068", "this-escape"})
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass, Class<?>... initialConfigClasses) {
        List<Class<?>> allInitialConfigClasses = new ArrayList<>(Arrays.asList(initialConfigClasses));
        allInitialConfigClasses.add(initialConfigClass);
        processConfigs(allInitialConfigClasses);
    }

    public AppComponentsContainerImpl(String packageName) {
        Reflections reflections = new Reflections(packageName);
        List<Class<?>> initialConfigClasses =
                new ArrayList<>(reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class));
        processConfigs(initialConfigClasses);
    }

    private void processConfigs(List<Class<?>> configClasses) {
        for (Class<?> configClass : configClasses) {
            checkConfigClass(configClass);
        }
        configClasses = configClasses.stream()
                .sorted(Comparator.comparing(configClass -> configClass
                        .getAnnotation(AppComponentsContainerConfig.class)
                        .order()))
                .toList();
        for (Class<?> configClass : configClasses) {
            processConfig(configClass);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private void processConfig(Class<?> configClass) {
        Object config = getConfig(configClass);
        List<Method> componentMethods = getComponentMethods(configClass);
        for (Method componentMethod : componentMethods) {
            addComponent(config, componentMethod);
        }
    }

    @SuppressWarnings("java:S3011")
    private Object getConfig(Class<?> configClass) {
        try {
            Constructor<?> constructor = configClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            throw new ProcessConfigException(configClass, e);
        }
    }

    private List<Method> getComponentMethods(Class<?> configClass) {
        List<Method> methods = Arrays.asList(configClass.getDeclaredMethods());
        return methods.stream()
                .filter(method -> method.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparing(
                        method -> method.getAnnotation(AppComponent.class).order()))
                .toList();
    }

    @SuppressWarnings("java:S3011")
    private void addComponent(Object config, Method componentMethod) {
        componentMethod.setAccessible(true);
        Class<?>[] parameterTypes = componentMethod.getParameterTypes();
        Object[] args = Arrays.stream(parameterTypes).map(this::getAppComponent).toArray();
        try {
            Object component = componentMethod.invoke(config, args);
            String componentName =
                    componentMethod.getAnnotation(AppComponent.class).name();
            if (appComponentsByName.containsKey(componentName)) {
                throw new ComponentAlreadyExistsException(componentName);
            }
            appComponents.add(component);
            appComponentsByName.put(componentName, component);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ProcessConfigException(componentMethod, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> components = appComponents.stream()
                .filter(component -> componentClass.isAssignableFrom(component.getClass()))
                .toList();
        if (components.size() > 1) {
            throw new DuplicateComponentException(componentClass);
        }
        if (components.isEmpty()) {
            throw new ComponentNotFoundException(componentClass);
        }
        return (C) components.getFirst();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(String componentName) {
        if (!appComponentsByName.containsKey(componentName)) {
            throw new ComponentNotFoundException(componentName);
        }
        return (C) appComponentsByName.get(componentName);
    }
}
