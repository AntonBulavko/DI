package com.demo.tests.service.impl;

import com.demo.annotations.Inject;
import com.demo.tests.dao.DAO;
import com.demo.tests.service.Service;

public class ServiceWithTwoInjectConstructors implements Service {

    private final DAO dao;

    @Inject
    public ServiceWithTwoInjectConstructors(DAO dao) {
        this.dao = dao;
    }

    @Inject
    public ServiceWithTwoInjectConstructors(DAO dao,String str){
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
