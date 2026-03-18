package com.example.dao;

import com.example.pojo.SysUser;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/29 10:15
 **/
public interface UserMapper {

    public SysUser queryUserByUsername(String username);
}
