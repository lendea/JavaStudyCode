package com.lendea.githook.controller;

import com.lendea.githook.entity.GitCommitRequest;
import com.lendea.githook.entity.ValidateResult;
import com.lendea.githook.service.ValidateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Git 校验接口控制器
 *
 * @author lendea
 */
@Slf4j
@RestController
@RequestMapping("/api/git")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // 允许跨域调用
public class GitValidateController {

    private final ValidateService validateService;

    /**
     * 综合校验接口 - 同时校验用户名和 commit message
     * POST /api/git/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidateResult> validate(
            @RequestBody @Validated GitCommitRequest request) {
        log.info("收到校验请求 - 项目: {}, 用户: {}, 分支: {}",
                request.getProjectName(),
                request.getUserName(),
                request.getBranchName());

        ValidateResult result = validateService.validate(request);

        return ResponseEntity.ok(result);
    }

    /**
     * 仅校验用户名
     * POST /api/git/validate/user
     */
    @PostMapping("/validate/user")
    public ResponseEntity<ValidateResult> validateUser(
            @RequestParam String userName,
            @RequestParam(required = false) String userEmail) {
        log.info("收到用户名校验请求 - userName: {}", userName);

        ValidateResult result = validateService.validateUser(userName, userEmail);

        return ResponseEntity.ok(result);
    }

    /**
     * 仅校验 commit message
     * POST /api/git/validate/message
     */
    @PostMapping("/validate/message")
    public ResponseEntity<ValidateResult> validateMessage(
            @RequestParam String commitMessage) {
        log.info("收到 commit message 校验请求 - length: {}",
                commitMessage != null ? commitMessage.length() : 0);

        ValidateResult result = validateService.validateCommitMsg(commitMessage);

        return ResponseEntity.ok(result);
    }

    /**
     * 健康检查接口
     * GET /api/git/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

}
