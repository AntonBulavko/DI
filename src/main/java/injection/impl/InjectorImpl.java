package injection.impl;

import annotations.Inject;
import exceptions.BindingNotFoundException;
import exceptions.TooManyConstructorsException;
import injection.Injector;
import injection.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class InjectorImpl implements Injector {

    public Map<Class<?>, Class<?>> getBindClasses() {
        return bindClasses;
    }

    public Map<Class<?>, Object> getSingletons() {
        return singletons;
    }

    public Map<Class<?>, Object> getPrototypes() {
        return prototypes;
    }

    public Map<Class<?>, Provider<?>> getProviderMap() {
        return providerMap;
    }

    private final Map<Class<?>, Class<?>> bindClasses = new HashMap<>();
    private final Set<Class<?>> singletonsSet = new HashSet<>();
    private final Set<Class<?>> prototypeSet = new HashSet<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Map<Class<?>, Object> prototypes = new HashMap<>();
    private final Map<Class<?>, Provider<?>> providerMap = new HashMap<>();

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        if (prototypeSet.contains(type)){
            return () -> {
                try {
                    return (T) bindClasses.get(type).getConstructor().newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            };
        }
        return null;
    }

    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        bindClasses.put(intf, impl);
    }

    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        if (bindClasses.containsKey(intf)) {
            //make singleton
        } else {
            bindClasses.put(intf, impl);
            // make singleton
        }
    }

    public <T> void bindPrototype(Class<T> intf, Class<? extends T> impl) {
        if (bindClasses.containsKey(intf)) {
            //make prototype
        } else {
            bindClasses.put(intf, impl);
            // make prototype
        }
    }

    public <T> Object createInjection(Class<T> injectClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> constructor = getConstructor(injectClass);
        if (constructor.getParameterTypes().length == 0) {
            T instance = injectClass.getConstructor().newInstance();
            return instance;
        } else {

            return injectClass.getConstructor(injectParameters(constructor)).newInstance(injectObjects(constructor));
        }
    }

    private <T> Constructor<T> getConstructor(Class<T> injectClass) throws NoSuchMethodException {
        Constructor<T>[] constructors = (Constructor<T>[]) injectClass.getConstructors();
        List<Constructor<T>> constructorList = getAnnotatedConstructorsList(constructors);
        return getConstructorFromClass(injectClass, constructorList);
    }

    private <T> List<Constructor<T>> getAnnotatedConstructorsList(Constructor<T>[] constructors) {
        return Arrays.stream(constructors).filter(c -> c.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
    }

    private <T> Constructor<T> getConstructorFromClass(Class<T> injectClass, List<Constructor<T>> constructorList) throws NoSuchMethodException {
        if (constructorList.isEmpty()) {
            return injectClass.getConstructor();
        }
        if (constructorList.size() > 1) {
            throw new TooManyConstructorsException();
        } else {
            return constructorList.get(0);
        }
    }

    private <T> Class<?>[] injectParameters(Constructor<T> constructor) {
        return constructor.getParameterTypes();
    }

    /*
        private <T> void singletonInjection(Class <T> type){
            if (singletonMap.containsKey(type)){
                if(!providerMap.containsKey(type)){
                    providerMap.put(type,provider)
                } else {
                    providerMap.get(type);
                }
            }
        }
        private <T> void prototypeInjection(Class<T> type){
            if (prototypeMap.containsKey(type)){
                providerMap.put(type, provider);
            } else
        }*/
    private <T> Object[] injectObjects(Constructor<T> constructor) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Object[] objects = new Object[constructor.getParameterTypes().length];
        int i = 0;
        for (Class<?> clazz : constructor.getParameterTypes()) {
            prototypeSet.add(clazz);
            Class<?> dependencyClass = getClassFromBinding(clazz);
            if (clazz.isAssignableFrom(dependencyClass)) {
                objects[i] = dependencyClass.getConstructor().newInstance();
                providerMap.put(clazz,getProvider(clazz));
                prototypes.put(clazz, objects[i++]);
            }
        }
        return objects;
    }

    private Class getClassFromBinding(Class<?> clazz) {
        if (bindClasses.get(clazz) != null) {
            return bindClasses.get(clazz);
        } else throw new BindingNotFoundException(clazz.getName());
    }

}
