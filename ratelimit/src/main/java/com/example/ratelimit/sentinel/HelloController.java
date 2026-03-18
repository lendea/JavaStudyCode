package com.example.ratelimit.sentinel;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lendea
 * @project JavaStudyCode
 * @description
 * @date 2024/04/25 13:51
 **/
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }
}
