package com.demo.tests.service.impl;

import com.demo.annotations.Inject;
import com.demo.tests.dao.DAO;
import com.demo.tests.service.Service;

public class StandartServiceImpl implements Service {

    private final DAO dao;

    @Inject
    public StandartServiceImpl(DAO dao) {
        this.dao = dao;
    }

    @Override
    public String useDAO(String msg) {
        return dao.printMessage(msg);
    }

    @Override
    public DAO getDAO() {
        return dao;
    }

}
