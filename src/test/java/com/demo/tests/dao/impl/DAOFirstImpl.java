package com.demo.tests.dao.impl;

import com.demo.tests.dao.DAO;

public class DAOFirstImpl implements DAO {
    @Override
    public String printMessage(String msg) {
        return "first implementation " + msg;
    }
}
