package com.example.controller;

import com.example.utils.VerifyCode;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.security.RolesAllowed;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/03/29 10:19
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @RolesAllowed(value = {"ROLE_ADMIN"})
    @RequestMapping("/query")
    public String query(){
        System.out.println("用户查询....");
        return "/home.jsp";
    }
    @RolesAllowed(value = {"ROLE_USER"})
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

    @RequestMapping("/code")
    @ResponseBody
    public String code() {
        Map<String, String> result = new HashMap<>();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            VerifyCode verifyCode = new VerifyCode();
            //进行base64编码
            ImageIO.write(verifyCode.getImage(), "png", bos);
            String string = Base64Utils.encodeToString(bos.toByteArray());

            String verifyCodekey = UUID.randomUUID().toString().replace("-", "");
            result.put("verifyCodeKey", verifyCodekey);
            result.put("image", "data:image/png;base64," + string);
            System.out.println(verifyCodekey);
            System.out.println(string);
            return string;
        } catch (IOException e) {
            System.out.println("生成验证码异常");
            throw new RuntimeException("error");
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                System.out.println("生成验证码，关闭流异常");
                throw new RuntimeException("error");
            }
        }
    }
}
