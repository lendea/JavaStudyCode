package com.example.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/29 10:20
 **/
@Controller
@RequestMapping("/role")
public class RoleController {

    @Secured("ROLE_USER")
    @RequestMapping("/query")
    public String query(){
        System.out.println("用户查询....");
        return "/home.jsp";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/save")
    public String save(){
        System.out.println("用户添加....");
        return "/home.jsp";
    }

    @RequestMapping("/update")
    public String update(){
        System.out.println("用户更新....");
        return "/home.jsp";
    }
}
