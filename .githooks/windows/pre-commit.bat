@echo off
chcp 65001 >nul 2>&1
setlocal EnableDelayedExpansion

REM pre-commit hook for Windows
REM Git 提交前校验 - Windows 版本

echo ========================================
echo Git 提交前校验
echo ========================================

REM 获取 Git 配置
for /f "tokens=*" %%a in ('git config user.name') do set USER_NAME=%%a
for /f "tokens=*" %%a in ('git config user.email') do set USER_EMAIL=%%a

if "!USER_NAME!"=="" (
    echo [错误] Git 用户名未设置
    echo 请执行: git config user.name "你的名字"
    exit /b 1
)

echo 当前用户名: !USER_NAME!

REM 校验用户名长度
set name_len=0
for /l %%i in (0,1,100) do (
    set "char=!USER_NAME:~%%i,1!"
    if "!char!"=="" goto :check_len
    set /a name_len+=1
)
:check_len

if !name_len! lss 2 (
    echo [错误] 用户名太短，至少需要 2 个字符
    goto :show_help
)
if !name_len! gtr 20 (
    echo [错误] 用户名太长，最多 20 个字符
    goto :show_help
)

REM 简单校验：检查是否全是数字或符号
echo !USER_NAME! | findstr /r "[a-zA-Z一-龥]" >nul
if errorlevel 1 (
    echo [错误] 用户名必须包含中文或英文字符
    goto :show_help
)

echo [OK] 用户名格式校验通过: !USER_NAME!

REM 邮箱校验（如果配置了）
if not "!USER_EMAIL!"=="" (
    echo !USER_EMAIL! | findstr /r "^[a-zA-Z0-9._%%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$" >nul
    if not errorlevel 1 (
        echo [OK] 邮箱格式校验通过: !USER_EMAIL!
    ) else (
        echo [警告] 邮箱格式可能不正确: !USER_EMAIL!
    )
)

echo ========================================
exit /b 0

:show_help
echo.
echo 规范要求（满足其一即可）：
echo   1. 2-4 个中文汉字（如：张三、李小明）
echo   2. 2-20 个英文字符，可包含空格（如：John Smith）
echo.
echo 请执行以下命令修改：
echo   git config user.name "你的名字"
exit /b 1
