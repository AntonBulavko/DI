package com.demo.tests.service.impl;

import com.demo.tests.dao.DAO;
import com.demo.tests.service.Service;

public class ServiceWithDefaultConstructor implements Service {

    private final DAO dao;

    public ServiceWithDefaultConstructor() {
        dao = null;
    }

    @Override
    public String useDAO(String msg) {
        return "default";
    }

    @Override
    public DAO getDAO() {
        return null;
    }
}
