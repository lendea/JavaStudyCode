package com.example.pojo;

import lombok.Data;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/29 10:14
 **/
@Data
public class SysUser {
    private Integer id;

    private String userName;

    private String password;

    private String salt;
}
