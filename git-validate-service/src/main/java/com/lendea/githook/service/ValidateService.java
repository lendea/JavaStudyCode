package com.lendea.githook.service;

import com.lendea.githook.entity.GitCommitRequest;
import com.lendea.githook.entity.ValidateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Git 校验服务
 *
 * @author lendea
 */
@Slf4j
@Service
public class ValidateService {

    /**
     * 中文用户名正则：2-4个中文字符
     */
    private static final Pattern CHINESE_NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5]{2,4}$");

    /**
     * 英文用户名正则：2-20个英文字符，可包含空格
     */
    private static final Pattern ENGLISH_NAME_PATTERN = Pattern.compile("^[a-zA-Z ]{2,20}$");

    /**
     * 邮箱格式正则
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    /**
     * 执行完整校验
     */
    public ValidateResult validate(GitCommitRequest request) {
        List<String> errors = new ArrayList<>();

        // 1. 校验用户名
        if (!validateUserName(request.getUserName())) {
            errors.add("用户名格式不符合规范：需为2-4个中文或2-20个英文字符");
        }

        // 2. 校验邮箱（如果提供了）
        if (request.getUserEmail() != null && !request.getUserEmail().isEmpty()) {
            if (!validateEmail(request.getUserEmail())) {
                errors.add("邮箱格式不正确");
            }
        }

        // 3. 校验 commit message（如果提供了）
        if (request.getCommitMessage() != null && !request.getCommitMessage().isEmpty()) {
            errors.addAll(validateCommitMessage(request.getCommitMessage()));
        }

        // 4. 记录日志
        log.info("校验完成 - 项目: {}, 用户: {}, 结果: {}",
                request.getProjectName(),
                request.getUserName(),
                errors.isEmpty() ? "通过" : "失败 - " + errors);

        if (errors.isEmpty()) {
            return ValidateResult.success();
        } else {
            return ValidateResult.fail(errors);
        }
    }

    /**
     * 仅校验用户名
     */
    public ValidateResult validateUser(String userName, String userEmail) {
        List<String> errors = new ArrayList<>();

        if (!validateUserName(userName)) {
            errors.add("用户名格式不符合规范：需为2-4个中文或2-20个英文字符");
        }

        if (userEmail != null && !userEmail.isEmpty() && !validateEmail(userEmail)) {
            errors.add("邮箱格式不正确");
        }

        if (errors.isEmpty()) {
            return ValidateResult.success();
        } else {
            return ValidateResult.fail(errors);
        }
    }

    /**
     * 仅校验 commit message
     */
    public ValidateResult validateCommitMsg(String commitMessage) {
        List<String> errors = validateCommitMessage(commitMessage);

        if (errors.isEmpty()) {
            return ValidateResult.success();
        } else {
            return ValidateResult.fail(errors);
        }
    }

    /**
     * 校验用户名格式
     * 支持：2-4个中文字符，或 2-20个英文字符（可包含空格）
     */
    public boolean validateUserName(String userName) {
        if (userName == null || userName.isEmpty()) {
            return false;
        }

        // 检查中文格式
        if (CHINESE_NAME_PATTERN.matcher(userName).matches()) {
            return true;
        }

        // 检查英文格式
        if (ENGLISH_NAME_PATTERN.matcher(userName).matches()) {
            return true;
        }

        return false;
    }

    /**
     * 校验邮箱格式
     */
    public boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return true; // 邮箱可选
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 校验 commit message
     * 返回错误列表，空列表表示通过
     */
    public List<String> validateCommitMessage(String commitMessage) {
        List<String> errors = new ArrayList<>();

        if (commitMessage == null || commitMessage.isEmpty()) {
            errors.add("Commit message 不能为空");
            return errors;
        }

        // 规则1: 长度检查（10-100字符）
        int length = commitMessage.length();
        if (length < 10) {
            errors.add("Commit message 太短（" + length + " 字符），至少需要 10 个字符");
        }
        if (length > 100) {
            errors.add("Commit message 太长（" + length + " 字符），最多 100 个字符");
        }

        // 规则2: 不能以空格开头或结尾
        if (commitMessage.startsWith(" ") || commitMessage.startsWith("\t")) {
            errors.add("Commit message 不能以空格开头");
        }
        if (commitMessage.endsWith(" ") || commitMessage.endsWith("\t")) {
            errors.add("Commit message 不能以空格结尾");
        }

        // 规则3: 不能以句号结尾
        if (commitMessage.endsWith(".") || commitMessage.endsWith("。")) {
            errors.add("Commit message 不能以句号结尾");
        }

        // 规则4: 必须包含中文或英文内容（不能全是数字或符号）
        if (!Pattern.compile("[一-龥a-zA-Z]").matcher(commitMessage).find()) {
            errors.add("Commit message 必须包含中文或英文字符");
        }

        return errors;
    }

}
