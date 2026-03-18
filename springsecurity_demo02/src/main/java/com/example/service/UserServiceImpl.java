package com.example.service;

import com.example.dao.UserMapper;
import com.example.pojo.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/29 10:11
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) {
// 根据账号查询
        SysUser sysUser = userMapper.queryUserByUsername(userName);
        if (sysUser != null) {
            List<GrantedAuthority> list = new ArrayList<>();
            list.add(new SimpleGrantedAuthority("ROLE_USER"));
            // 表示账号存在
            UserDetails userDetails = new User(
                    sysUser.getUserName() // 账号
                    , sysUser.getPassword() // 密码
                    , true
                    , true
                    , true
                    , true
                    , list
            );
            return userDetails;
        }
        // 返回null 表示账号不存在
        return null;
    }


    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        System.out.println("encoder.encode(password) = " + encoder.encode(password));
        System.out.println("encoder.encode(password) = " + encoder.encode(password));
        System.out.println("encoder.encode(password) = " + encoder.encode(password));
        System.out.println("encoder.encode(password) = " + encoder.encode(password));
    }

}
