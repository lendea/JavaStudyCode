#!/usr/bin/env pwsh
# commit-msg hook for Windows PowerShell

param([string]$MsgFile)

$ErrorActionPreference = "Stop"

# 颜色定义
$Red = "`e[31m"
$Green = "`e[32m"
$Yellow = "`e[33m"
$NC = "`e[0m"

Write-Host "========================================"
Write-Host "Commit Message 校验"
Write-Host "========================================"

# 读取提交信息
$COMMIT_MSG = Get-Content $MsgFile -TotalCount 1

if ([string]::IsNullOrEmpty($COMMIT_MSG)) {
    Write-Host "${Red}[错误] Commit message 不能为空${NC}"
    exit 1
}

Write-Host "提交信息: $COMMIT_MSG"

# 规则1: 长度检查（10-100字符）
$msgLength = $COMMIT_MSG.Length

if ($msgLength -lt 10) {
    Write-Host "${Red}[错误] Commit message 太短（$msgLength 字符），至少需要 10 个字符${NC}"
    exit 1
}

if ($msgLength -gt 100) {
    Write-Host "${Red}[错误] Commit message 太长（$msgLength 字符），最多 100 个字符${NC}"
    exit 1
}

# 规则2: 不能以空格开头或结尾
if ($COMMIT_MSG -match '^\s') {
    Write-Host "${Red}[错误] Commit message 不能以空格开头${NC}"
    exit 1
}

if ($COMMIT_MSG -match '\s$') {
    Write-Host "${Red}[错误] Commit message 不能以空格结尾${NC}"
    exit 1
}

# 规则3: 不能以句号结尾
if ($COMMIT_MSG -match '[\.。]$') {
    Write-Host "${Red}[错误] Commit message 不能以句号结尾${NC}"
    exit 1
}

# 规则4: 必须包含中文或英文
if (-not ($COMMIT_MSG -match '[\u4e00-\u9fa5a-zA-Z]')) {
    Write-Host "${Red}[错误] Commit message 必须包含中文或英文字符${NC}"
    exit 1
}

Write-Host "${Green}[OK] Commit message 校验通过${NC}"
Write-Host "========================================"
exit 0
