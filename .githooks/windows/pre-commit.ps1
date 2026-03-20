#!/usr/bin/env pwsh
# pre-commit hook for Windows PowerShell
# Git 提交前校验 - Windows PowerShell 版本

$ErrorActionPreference = "Stop"

# 颜色定义
$Red = "`e[31m"
$Green = "`e[32m"
$Yellow = "`e[33m"
$NC = "`e[0m"

Write-Host "========================================"
Write-Host "Git 提交前校验"
Write-Host "========================================"

# 获取 Git 配置
$USER_NAME = git config user.name
$USER_EMAIL = git config user.email

if ([string]::IsNullOrEmpty($USER_NAME)) {
    Write-Host "${Red}[错误] Git 用户名未设置${NC}"
    Write-Host "请执行: git config user.name '你的名字'"
    exit 1
}

Write-Host "当前用户名: $USER_NAME"

# 校验用户名
# 中文正则：2-4个中文字符
$chinesePattern = "^[\u4e00-\u9fa5]{2,4}$"
# 英文正则：2-20个英文字符，可包含空格
$englishPattern = "^[a-zA-Z ]{2,20}$"

$isValid = $false

if ($USER_NAME -match $chinesePattern) {
    $isValid = $true
}
elseif ($USER_NAME -match $englishPattern) {
    $isValid = $true
}

if (-not $isValid) {
    Write-Host "${Red}[错误] Git 用户名格式不符合规范${NC}"
    Write-Host "当前用户名: $USER_NAME"
    Write-Host ""
    Write-Host "规范要求（满足其一即可）："
    Write-Host "  1. 2-4 个中文汉字（如：张三、李小明）"
    Write-Host "  2. 2-20 个英文字符，可包含空格（如：John Smith）"
    Write-Host ""
    Write-Host "请执行以下命令修改："
    Write-Host "  git config user.name '你的名字'"
    exit 1
}

Write-Host "${Green}[OK] 用户名格式校验通过: $USER_NAME${NC}"

# 邮箱校验（如果配置了）
if (-not [string]::IsNullOrEmpty($USER_EMAIL)) {
    $emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
    if ($USER_EMAIL -match $emailPattern) {
        Write-Host "${Green}[OK] 邮箱格式校验通过: $USER_EMAIL${NC}"
    } else {
        Write-Host "${Yellow}[警告] 邮箱格式可能不正确: $USER_EMAIL${NC}"
    }
}

Write-Host "========================================"
exit 0
