package com.demo.tests;

import com.demo.exceptions.BindingNotFoundException;
import com.demo.exceptions.ConstructorNotFoundException;
import com.demo.exceptions.TooManyConstructorsException;
import com.demo.injection.Injector;
import com.demo.injection.Provider;
import com.demo.injection.impl.InjectorImpl;
import com.demo.tests.dao.DAO;
import com.demo.tests.dao.impl.DAOFirstImpl;
import com.demo.tests.dao.impl.DAOSecondImpl;
import com.demo.tests.service.Service;
import com.demo.tests.service.impl.ServiceWithDefaultConstructor;
import com.demo.tests.service.impl.ServiceWithoutDefaultConstructor;
import com.demo.tests.service.impl.StandartServiceImpl;
import com.demo.tests.service.impl.ServiceWithTwoInjectConstructors;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DITest {

    private InjectorImpl injector;

    @Before
    public void init() {
        injector = new InjectorImpl();
    }

    @Test
    public void existingBinding() {
        injector.bind(DAO.class, DAOFirstImpl.class);
        Provider<DAO> daoProvider = injector.getProvider(DAO.class);
        assertNotNull(daoProvider);
        assertNotNull(daoProvider.getInstance());
        assertSame(DAOFirstImpl.class, daoProvider.getInstance().getClass());
    }

    @Test
    public void addClassesToBinding() {
        assertSame(0, injector.getBindClasses().size());
        injector.bind(DAO.class, DAOFirstImpl.class);
        assertSame(1, injector.getBindClasses().size());
    }

    @Test
    public void addPrototypeProviders() {
        injector.bind(DAO.class, DAOSecondImpl.class);
        Set<Provider<?>> providers = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            providers.add(injector.getProvider(DAO.class));
        }
        assertSame(10, providers.size());
    }

    @Test
    public void addSingletonProvider() {
        injector.bindSingleton(DAO.class, DAOFirstImpl.class);
        Set<Provider<?>> providers = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            providers.add(injector.getProvider(DAO.class));
        }
        assertSame(1, injector.getSingletonsSet().size());
        assertSame(1, providers.size());
    }

    @Test
    public void makeInjection() {
        injector.bind(DAO.class, DAOFirstImpl.class);
        Service service = (Service) injector.createInjection(StandartServiceImpl.class);
        String result = service.useDAO("message");
        assertNotNull(result);
        assertEquals("first implementation message", result);
    }

    @Test
    public void makeSingletonInjection() {
        injector.bindSingleton(DAO.class, DAOFirstImpl.class);
        Service firstService = (Service) injector.createInjection(StandartServiceImpl.class);
        Service secondService = (Service) injector.createInjection(StandartServiceImpl.class);
        assertSame(firstService.getDAO(), secondService.getDAO());
    }

    @Test
    public void makePrototypeInjection() {
        injector.bind(DAO.class, DAOFirstImpl.class);
        Service firstService = (Service) injector.createInjection(StandartServiceImpl.class);
        Service secondService = (Service) injector.createInjection(StandartServiceImpl.class);
        assertNotSame(firstService.getDAO(), secondService.getDAO());
    }

    @Test(expected = TooManyConstructorsException.class)
    public void tooManyConstrException() {
        injector.bind(DAO.class, DAOFirstImpl.class);
        injector.createInjection(ServiceWithTwoInjectConstructors.class);
    }

    @Test(expected = BindingNotFoundException.class)
    public void notFoundBindingException() {
        injector.createInjection(StandartServiceImpl.class);
    }

    @Test(expected = ConstructorNotFoundException.class)
    public void notFoundConstructorException() {
        injector.createInjection(ServiceWithoutDefaultConstructor.class);
    }

    @Test
    public void testDefaultConstructor() {
        Service service = (Service) injector.createInjection(ServiceWithDefaultConstructor.class);
        String msg = "message";
        assertSame("default", service.useDAO(msg));
        assertNotEquals(service.useDAO(msg), msg);
    }

    @Test
    public void returnNullIfNotFoundBinding() {
        Provider<String> daoProvider = injector.getProvider(String.class);
        assertNull(daoProvider);
    }


}
