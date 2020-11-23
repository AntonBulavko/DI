package com.demo.tests.service.impl;

import com.demo.tests.dao.DAO;
import com.demo.tests.service.Service;

public class ServiceWithoutDefaultConstructor implements Service {

    private final DAO dao;

    public ServiceWithoutDefaultConstructor(DAO dao) {
        this.dao = dao;
    }

    @Override
    public String useDAO(String msg) {
        return null;
    }

    @Override
    public DAO getDAO() {
        return null;
    }
}
