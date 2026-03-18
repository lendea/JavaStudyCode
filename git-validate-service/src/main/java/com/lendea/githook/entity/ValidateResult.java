package com.lendea.githook.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 校验结果
 *
 * @author lendea
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateResult {

    /**
     * 是否通过校验
     */
    private boolean valid;

    /**
     * 错误信息列表
     */
    @Builder.Default
    private List<String> errors = new ArrayList<>();

    /**
     * 改进建议
     */
    private String suggestion;

    /**
     * 获取成功结果
     */
    public static ValidateResult success() {
        return ValidateResult.builder()
                .valid(true)
                .build();
    }

    /**
     * 获取失败结果
     */
    public static ValidateResult fail(List<String> errors) {
        return ValidateResult.builder()
                .valid(false)
                .errors(errors)
                .build();
    }

    /**
     * 添加错误信息
     */
    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(error);
        this.valid = false;
    }

}
