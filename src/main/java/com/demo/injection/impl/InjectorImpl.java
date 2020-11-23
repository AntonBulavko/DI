package com.demo.injection.impl;

import com.demo.annotations.Inject;
import com.demo.exceptions.BindingNotFoundException;
import com.demo.exceptions.ConstructorNotFoundException;
import com.demo.exceptions.TooManyConstructorsException;
import com.demo.injection.Injector;
import com.demo.injection.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InjectorImpl implements Injector {

    private final Map<Class<?>, Class<?>> bindClasses;
    private final Set<Class<?>> singletonsSet;
    private final ConcurrentHashMap<Class<?>, Provider<?>> providerMap;

    public InjectorImpl() {
        this.bindClasses = new HashMap<>();
        this.singletonsSet = new HashSet<>();
        this.providerMap = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized <T> Provider<T> getProvider(Class<T> type) {
        if (checkBinding(type)) {
            if (this.singletonsSet.contains(type)) {
                if (this.providerMap.get(type) == null) {
                    putProvider(type);
                }
            } else {
                putProvider(type);
            }
            return (Provider<T>) this.providerMap.get(type);
        } else return null;
    }


    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        this.bindClasses.put(intf, impl);
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        this.bindClasses.put(intf, impl);
        this.singletonsSet.add(intf);
    }

    public <T> Object createInjection(Class<T> injectClass) {
        Constructor<T> constructor = getConstructor(injectClass);
        if (constructor.getParameterTypes().length == 0) {
            return createObjectWithDefaultConstructor(constructor);
        } else {
            return createObjectWithArgsConstructor(constructor);
        }
    }

    public Map<Class<?>, Class<?>> getBindClasses() {
        return bindClasses;
    }

    public Set<Class<?>> getSingletonsSet() {
        return singletonsSet;
    }

    private <T> Constructor<T> getConstructor(Class<T> injectClass) {
        Constructor<T>[] constructors = (Constructor<T>[]) injectClass.getConstructors();
        List<Constructor<T>> constructorList = getAnnotatedConstructorsList(constructors);
        if (constructorList.isEmpty()) {
            return getDefaultConstructor(injectClass);
        }
        return getConstructorFromList(constructorList);
    }

    private <T> List<Constructor<T>> getAnnotatedConstructorsList(Constructor<T>[] constructors) {
        return Arrays.stream(constructors).filter(c -> c.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
    }

    private <T> Constructor<T> getDefaultConstructor(Class<T> injectClass) {
        try {
            return injectClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new ConstructorNotFoundException();
        }
    }

    private <T> Constructor<T> getConstructorFromList(List<Constructor<T>> constructorList) {
        if (constructorList.size() > 1) {
            throw new TooManyConstructorsException();
        } else {
            return constructorList.get(0);
        }
    }

    private <T> T createInstanceFromType(Class<T> type) {
        try {
            return (T) this.bindClasses.get(type).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return (T) e;
        }
    }

    private <T> Object createObjectWithArgsConstructor(Constructor<T> constructor) {
        try {
            return constructor.newInstance(injectObjects(constructor));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return e;
        }
    }

    private <T> Object[] injectObjects(Constructor<T> constructor) {
        Object[] objects = new Object[constructor.getParameterTypes().length];
        int i = 0;
        for (Class<?> type : constructor.getParameterTypes()) {
            if (checkBinding(type)) {
                objects[i++] = getProvider(type).getInstance();
            } else
                throw new BindingNotFoundException(type.getName());
        }
        return objects;
    }

    private <T> Object createObjectWithDefaultConstructor(Constructor<T> constructor) {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return e;
        }
    }

    private <T> void putProvider(Class<T> type) {
        T instance = createInstanceFromType(type);
        Provider<T> provider = () -> instance;
        this.providerMap.put(type, provider);
    }

    public <T> boolean checkBinding(Class<T> type) {
        return this.bindClasses.get(type) != null;
    }
}
