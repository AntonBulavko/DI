package com.demo.tests.dao.impl;

import com.demo.tests.dao.DAO;

public class DAOSecondImpl implements DAO {

    @Override
    public String printMessage(String msg) {
        return "second implementation " + msg;
    }
}
