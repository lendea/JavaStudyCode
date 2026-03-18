package com.example.server;

import com.example.bean.HelloService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/22 10:35
 **/
public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {
    public HelloServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String sayHello() throws RemoteException {
        System.out.println("hello!");
        return "hello!";
    }
}
