#!/bin/bash
# 跨平台 Git Hooks 安装脚本
# 支持 macOS、Linux、Windows (Git Bash)

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
HOOKS_DIR="$PROJECT_ROOT/.githooks"
GIT_DIR="$PROJECT_ROOT/.git"

echo "========================================"
echo "Git Hooks 跨平台安装工具"
echo "========================================"
echo ""

# 检测操作系统
detect_os() {
    local os="unknown"

    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        os="linux"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        os="macos"
    elif [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
        os="windows"
    elif [[ -n "$WINDIR" ]]; then
        os="windows"
    fi

    echo "$os"
}

OS=$(detect_os)
echo "检测到操作系统: $OS"
echo ""

# 检查是否在 git 仓库中
if [ ! -d "$GIT_DIR" ]; then
    echo -e "${RED}错误：当前目录不是 Git 仓库${NC}"
    echo "请在项目根目录运行此脚本"
    exit 1
fi

# 安装函数
install_hooks() {
    case "$OS" in
        "macos"|"linux")
            install_unix
            ;;
        "windows")
            install_windows
            ;;
        *)
            echo -e "${YELLOW}无法识别的操作系统，尝试使用 Unix 方案...${NC}"
            install_unix
            ;;
    esac
}

# Unix/Linux/macOS 安装
install_unix() {
    echo -e "${BLUE}[1/3] 配置 Git hooks 路径...${NC}"

    # 使用 .githooks 目录
    git config --local core.hooksPath .githooks
    echo -e "${GREEN}✓ Hooks 路径已设置为: .githooks${NC}"

    echo ""
    echo -e "${BLUE}[2/3] 添加脚本执行权限...${NC}"

    if [ -f "$HOOKS_DIR/pre-commit" ]; then
        chmod +x "$HOOKS_DIR/pre-commit"
        echo -e "${GREEN}✓ pre-commit 已可执行${NC}"
    fi

    if [ -f "$HOOKS_DIR/commit-msg" ]; then
        chmod +x "$HOOKS_DIR/commit-msg"
        echo -e "${GREEN}✓ commit-msg 已可执行${NC}"
    fi

    echo ""
    echo -e "${BLUE}[3/3] 验证配置...${NC}"

    local configured_path
    configured_path=$(git config --local core.hooksPath)
    if [ "$configured_path" = ".githooks" ]; then
        echo -e "${GREEN}✓ 配置验证成功${NC}"
    else
        echo -e "${RED}✗ 配置验证失败${NC}"
        exit 1
    fi

    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}安装完成！${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo "当前配置:"
    echo "  Hooks 路径: $configured_path"
    echo "  可用 Hooks:"
    ls -1 "$HOOKS_DIR"/* 2>/dev/null | grep -v windows | xargs -I {} basename {} | sed 's/^/    - /'
}

# Windows 安装
install_windows() {
    echo -e "${BLUE}[1/4] 检测 Windows 环境...${NC}"

    local git_hooks_dir="$GIT_DIR/hooks"

    echo ""
    echo -e "${BLUE}[2/4] 复制 Windows 脚本到 .git/hooks/...${NC}"

    local windows_dir="$HOOKS_DIR/windows"

    # 复制 PowerShell 脚本
    if [ -f "$windows_dir/pre-commit.ps1" ]; then
        cp "$windows_dir/pre-commit.ps1" "$git_hooks_dir/pre-commit.ps1"
        echo -e "${GREEN}✓ pre-commit.ps1 已复制${NC}"
    fi

    if [ -f "$windows_dir/commit-msg.ps1" ]; then
        cp "$windows_dir/commit-msg.ps1" "$git_hooks_dir/commit-msg.ps1"
        echo -e "${GREEN}✓ commit-msg.ps1 已复制${NC}"
    fi

    # 复制 Batch 脚本作为备用
    if [ -f "$windows_dir/pre-commit.bat" ]; then
        cp "$windows_dir/pre-commit.bat" "$git_hooks_dir/"
        echo -e "${GREEN}✓ pre-commit.bat 已复制${NC}"
    fi

    echo ""
    echo -e "${BLUE}[3/4] 创建 wrapper 脚本...${NC}"

    # 创建调用 PowerShell 的 wrapper
    cat > "$git_hooks_dir/pre-commit" << 'EOF'
#!/bin/sh
# Wrapper for Windows PowerShell hook
if command -v powershell.exe >/dev/null 2>&1; then
    exec powershell.exe -ExecutionPolicy Bypass -File .git/hooks/pre-commit.ps1
elif command -v pwsh >/dev/null 2>&1; then
    exec pwsh -ExecutionPolicy Bypass -File .git/hooks/pre-commit.ps1
else
    echo "警告：未找到 PowerShell，跳过校验"
    exit 0
fi
EOF
    chmod +x "$git_hooks_dir/pre-commit"

    cat > "$git_hooks_dir/commit-msg" << 'EOF'
#!/bin/sh
# Wrapper for Windows PowerShell hook
MSG_FILE="$1"
if command -v powershell.exe >/dev/null 2>&1; then
    exec powershell.exe -ExecutionPolicy Bypass -File .git/hooks/commit-msg.ps1 "$MSG_FILE"
elif command -v pwsh >/dev/null 2>&1; then
    exec pwsh -ExecutionPolicy Bypass -File .git/hooks/commit-msg.ps1 "$MSG_FILE"
else
    echo "警告：未找到 PowerShell，跳过校验"
    exit 0
fi
EOF
    chmod +x "$git_hooks_dir/commit-msg"

    echo -e "${GREEN}✓ Wrapper 脚本已创建${NC}"

    echo ""
    echo -e "${BLUE}[4/4] 配置 Git 使用本地 hooks...${NC}"

    # Windows 使用 .git/hooks 而不是 core.hooksPath
    git config --local --unset core.hooksPath 2>/dev/null || true
    echo -e "${GREEN}✓ 已配置为使用 .git/hooks 目录${NC}"

    echo ""
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}Windows 安装完成！${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo ""
    echo "安装的文件:"
    ls -1 "$git_hooks_dir"/*.* 2>/dev/null | xargs -I {} basename {} | sed 's/^/  - /'
    echo ""
    echo "注意：Windows 需要 PowerShell 5.0 或更高版本"
    echo "      如果无法执行，请以管理员身份运行:"
    echo "      Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser"
}

# 卸载函数
uninstall_hooks() {
    echo -e "${YELLOW}卸载 Git Hooks...${NC}"

    # 重置 hooks 路径
    git config --local --unset core.hooksPath 2>/dev/null || true

    # 删除 Windows 复制的脚本
    if [ -d "$GIT_DIR/hooks" ]; then
        rm -f "$GIT_DIR/hooks/pre-commit.ps1"
        rm -f "$GIT_DIR/hooks/commit-msg.ps1"
        rm -f "$GIT_DIR/hooks/pre-commit.bat"
        echo -e "${GREEN}✓ 已清理 Windows 脚本${NC}"
    fi

    echo -e "${GREEN}✓ Hooks 已卸载${NC}"
}

# 检查依赖
check_dependencies() {
    echo -e "${BLUE}检查依赖...${NC}"

    if ! command -v git >/dev/null 2>&1; then
        echo -e "${RED}错误：未找到 Git${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Git 已安装${NC}"

    if [ "$OS" = "windows" ]; then
        if command -v powershell.exe >/dev/null 2>&1 || command -v pwsh >/dev/null 2>&1; then
            echo -e "${GREEN}✓ PowerShell 已安装${NC}"
        else
            echo -e "${YELLOW}⚠ 未找到 PowerShell，Windows 脚本可能无法执行${NC}"
        fi
    fi

    echo ""
}

# 显示帮助
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  install     安装 Git Hooks (默认)"
    echo "  uninstall   卸载 Git Hooks"
    echo "  check       检查当前配置"
    echo "  help        显示帮助"
    echo ""
    echo "示例:"
    echo "  $0              # 安装 hooks"
    echo "  $0 install      # 安装 hooks"
    echo "  $0 uninstall    # 卸载 hooks"
}

# 检查当前配置
check_config() {
    echo -e "${BLUE}当前 Git Hooks 配置${NC}"
    echo "========================================"

    local hooks_path
    hooks_path=$(git config --local core.hooksPath 2>/dev/null || echo "未设置")

    echo "Hooks 路径: $hooks_path"
    echo ""

    if [ "$hooks_path" = ".githooks" ]; then
        echo -e "${GREEN}✓ 使用项目 .githooks 目录${NC}"
        echo "可用 Hooks:"
        ls -1 "$HOOKS_DIR"/* 2>/dev/null | grep -v windows | while read -r f; do
            if [ -x "$f" ]; then
                echo "  ✓ $(basename "$f") (可执行)"
            else
                echo "  ✗ $(basename "$f") (无执行权限)"
            fi
        done
    elif [ -d "$GIT_DIR/hooks" ]; then
        echo "本地 .git/hooks 目录内容:"
        ls -1 "$GIT_DIR/hooks" 2>/dev/null | sed 's/^/  - /' || echo "  (空)"
    fi
}

# 主逻辑
case "${1:-install}" in
    install)
        check_dependencies
        install_hooks
        ;;
    uninstall)
        uninstall_hooks
        ;;
    check)
        check_config
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}未知选项: $1${NC}"
        show_help
        exit 1
        ;;
esac
