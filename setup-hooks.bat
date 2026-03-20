@echo off
chcp 65001 >nul 2>&1
title Git Hooks 安装工具
echo ========================================
echo Git Hooks 跨平台安装工具 (Windows)
echo ========================================
echo.

set "PROJECT_ROOT=%~dp0"
set "GIT_DIR=%PROJECT_ROOT%.git"
set "HOOKS_DIR=%PROJECT_ROOT%.githooks"

REM 检查是否在 git 仓库中
if not exist "%GIT_DIR%" (
    echo [错误] 当前目录不是 Git 仓库
    echo 请在项目根目录运行此脚本
    pause
    exit /b 1
)

echo [1/4] 检测环境...
echo   项目目录: %PROJECT_ROOT%
echo   Git 目录: %GIT_DIR%
echo.

echo [2/4] 复制 PowerShell 脚本...

REM 复制脚本到 .git/hooks/
if exist "%HOOKS_DIR%\windows\pre-commit.ps1" (
    copy /Y "%HOOKS_DIR%\windows\pre-commit.ps1" "%GIT_DIR%\hooks\" >nul
    echo   [OK] pre-commit.ps1 已复制
)

if exist "%HOOKS_DIR%\windows\commit-msg.ps1" (
    copy /Y "%HOOKS_DIR%\windows\commit-msg.ps1" "%GIT_DIR%\hooks\" >nul
    echo   [OK] commit-msg.ps1 已复制
)

if exist "%HOOKS_DIR%\windows\pre-commit.bat" (
    copy /Y "%HOOKS_DIR%\windows\pre-commit.bat" "%GIT_DIR%\hooks\" >nul
    echo   [OK] pre-commit.bat 已复制
)

echo.
echo [3/4] 配置 Git...

REM 重置 core.hooksPath（Windows 使用 .git/hooks）
git config --local --unset core.hooksPath 2>nul

REM 配置 Windows 使用 .git/hooks 目录
git config --local core.hooksPath "%GIT_DIR%\hooks"

echo   [OK] Git 配置已更新
echo.

echo [4/4] 验证安装...

REM 检查 PowerShell
powershell -Command "Get-Host" >nul 2>&1
if %errorlevel% equ 0 (
    echo   [OK] PowerShell 可用
) else (
    echo   [警告] 未找到 PowerShell，hooks 可能无法执行
)

echo.
echo ========================================
echo 安装完成！
echo ========================================
echo.
echo 使用方法:
echo   1. 确保已安装 PowerShell 5.0+
echo   2. 首次使用需要设置执行策略:
echo      powershell -Command "Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser"
echo.
echo 测试提交:
echo   git commit -m "测试提交信息"
echo.
pause
