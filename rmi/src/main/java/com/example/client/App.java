package com.example.client;

import com.example.bean.HelloService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.List;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/22 10:37
 **/
public class App {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try {
//            HelloService helloService = (HelloService) registry.lookup("hello");
//            System.out.println(helloService.sayHello());

            HelloService helloService = (HelloService) new InitialContext().lookup("rmi://127.0.0.1:1099/hello");
            System.out.println(helloService.sayHello());
//        } catch (NotBoundException e) {
//            e.printStackTrace();

        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

}
