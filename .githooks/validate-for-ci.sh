#!/bin/bash
# validate-for-ci.sh - 用于 CI/CD 环境的 Git 信息校验

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================"
echo "CI/CD Git 信息校验"
echo "========================================"

# 从 Git 获取信息
USER_NAME=$(git config user.name)
USER_EMAIL=$(git config user.email)
LATEST_COMMIT_MSG=$(git log -1 --pretty=%B)

# 如果 CI 环境提供了提交信息，使用 CI 的
if [ -n "$CI_COMMIT_MESSAGE" ]; then
    LATEST_COMMIT_MSG="$CI_COMMIT_MESSAGE"
elif [ -n "$GITHUB_SHA" ]; then
    # GitHub Actions
    LATEST_COMMIT_MSG=$(git log -1 --pretty=%B "$GITHUB_SHA" 2>/dev/null || echo "$LATEST_COMMIT_MSG")
elif [ -n "$GITLAB_CI" ]; then
    # GitLab CI
    LATEST_COMMIT_MSG=$(git log -1 --pretty=%B "$CI_COMMIT_SHA" 2>/dev/null || echo "$LATEST_COMMIT_MSG")
fi

echo "用户名: ${USER_NAME:-未设置}"
echo "邮箱: ${USER_EMAIL:-未设置}"
echo "最新 Commit: ${LATEST_COMMIT_MSG:0:50}..."
echo ""

# ===== 校验用户名 =====
if [ -z "$USER_NAME" ]; then
    echo -e "${RED}❌ 错误：Git 用户名未设置${NC}"
    exit 1
fi

is_valid=false

# 使用 Perl 进行 Unicode 校验（如果可用）
if command -v perl >/dev/null 2>&1; then
    if perl -e 'exit 1 unless $ARGV[0] =~ /^[\x{4e00}-\x{9fa5}]{2,4}$/u' "$USER_NAME" 2>/dev/null; then
        is_valid=true
    elif perl -e 'exit 1 unless $ARGV[0] =~ /^[a-zA-Z ]{2,20}$/' "$USER_NAME" 2>/dev/null; then
        is_valid=true
    fi
else
    # 降级方案
    if echo "$USER_NAME" | grep -qE '^[一-龥]{2,4}$' 2>/dev/null; then
        is_valid=true
    elif echo "$USER_NAME" | grep -qE '^[a-zA-Z ]{2,20}$'; then
        is_valid=true
    fi
fi

if [ "$is_valid" = false ]; then
    echo -e "${RED}❌ 错误：Git 用户名格式不符合规范${NC}"
    echo "当前用户名: $USER_NAME"
    echo "规范要求: 2-4个中文汉字 或 2-20个英文字符（可包含空格）"
    exit 1
fi

echo -e "${GREEN}✓ 用户名格式校验通过: $USER_NAME${NC}"

# ===== 校验 Commit Message =====
if [ -z "$LATEST_COMMIT_MSG" ]; then
    echo -e "${RED}❌ 错误：无法获取 Commit message${NC}"
    exit 1
fi

# 取第一行进行校验
FIRST_LINE=$(echo "$LATEST_COMMIT_MSG" | head -n1)
MSG_LENGTH=${#FIRST_LINE}

if [ "$MSG_LENGTH" -lt 10 ] || [ "$MSG_LENGTH" -gt 100 ]; then
    echo -e "${RED}❌ 错误：Commit message 长度不符合要求（10-100字符），当前: ${MSG_LENGTH}字符${NC}"
    echo "内容: $FIRST_LINE"
    exit 1
fi

if [[ "$FIRST_LINE" =~ ^[[:space:]] ]] || [[ "$FIRST_LINE" =~ [[:space:]]$ ]]; then
    echo -e "${RED}❌ 错误：Commit message 不能以空格开头或结尾${NC}"
    exit 1
fi

if [[ "$FIRST_LINE" =~ [。.]$ ]]; then
    echo -e "${RED}❌ 错误：Commit message 不能以句号结尾${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Commit message 校验通过（${MSG_LENGTH}字符）${NC}"

echo ""
echo "========================================"
echo -e "${GREEN}所有校验通过！${NC}"
echo "========================================"
exit 0
