package com.example.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/22 10:37
 **/
public interface HelloService extends Remote {
    String sayHello() throws RemoteException;
}