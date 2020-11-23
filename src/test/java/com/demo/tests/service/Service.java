package com.demo.tests.service;

import com.demo.tests.dao.DAO;

public interface Service {

    String useDAO(String msg);
    DAO getDAO();
}
