package org.example;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * securedPage 页面，实现的功能是，把用户信息作为模型传入了视图，这样打开页面后就能显示用户名和权限。
 * remoteCall 接口，实现的功能是，通过引入 OAuth2RestTemplate，在登录后就可以使用凭据直接从受保护资源服务器拿资源，不需要繁琐地实现获得访问令牌、在请求头里加入访问令牌的过程。
 */
@RestController
public class DemoController {
    @Autowired
    OAuth2RestTemplate restTemplate;

    @GetMapping("/securedPage")
    public ModelAndView securedPage(OAuth2Authentication authentication) {
        return new ModelAndView("securedPage").addObject("authentication", authentication);
    }

    @GetMapping("/remoteCall")
    public String remoteCall() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8081/user/name", String.class);
        return responseEntity.getBody();
    }
}