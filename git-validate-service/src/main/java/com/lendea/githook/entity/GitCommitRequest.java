package com.lendea.githook.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Git 提交校验请求
 *
 * @author lendea
 */
@Data
public class GitCommitRequest {

    /**
     * Git 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /**
     * Git 邮箱
     */
    private String userEmail;

    /**
     * 提交信息
     */
    private String commitMessage;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 分支名称
     */
    private String branchName;

}
