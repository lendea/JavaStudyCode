# GitLab Server Hooks 配置指南

Server Hooks 是在 **GitLab 服务器端**执行的 hooks，开发者**无法绕过**，是最强力的校验方案。

## 原理

```
开发者本地                    GitLab 服务器
     │                              │
     └── git push ─────────────────▶│
                                    │
                                    ▼
                           ┌─────────────────┐
                           │ pre-receive hook│  ← 在服务器执行
                           │   (无法绕过)    │
                           └────────┬────────┘
                                    │
                    校验通过 ───────┼───────▶ 接受 push
                    校验失败 ───────┼───────▶ 拒绝 push
                                    │
                                    ▼
                              返回错误信息
```

## 安装步骤

### 1. 在 GitLab 服务器上配置

SSH 登录到 GitLab 服务器（需要管理员权限）：

```bash
# 进入 GitLab 仓库目录（自定义安装路径）
cd /var/opt/gitlab/git-data/repositories/@hashed/xx/xx/xxxxxxxx.git

# 或者找到具体项目的仓库
cd /var/opt/gitlab/git-data/repositories/lendea/java-study-code.git

# 创建 custom_hooks 目录
mkdir -p custom_hooks
cd custom_hooks

# 创建 pre-receive hook
touch pre-receive
chmod +x pre-receive
```

### 2. pre-receive hook 脚本

```bash
#!/bin/bash
# /var/opt/gitlab/git-data/repositories/.../custom_hooks/pre-receive

# pre-receive 从标准输入接收：oldrev newrev refname
while read oldrev newrev refname; do
    # 获取提交信息
    USER_NAME=$(git log -1 --pretty=%an $newrev)
    USER_EMAIL=$(git log -1 --pretty=%ae $newrev)
    COMMIT_MSG=$(git log -1 --pretty=%B $newrev | head -n1)

    # 校验用户名
    is_valid=false

    # 检查中文 2-4 个字符
    if echo "$USER_NAME" | grep -qP '[\x{4e00}-\x{9fa5}]{2,4}'; then
        is_valid=true
    fi

    # 检查英文 2-20 个字符
    if echo "$USER_NAME" | grep -qE '^[a-zA-Z ]{2,20}$'; then
        is_valid=true
    fi

    if [ "$is_valid" = false ]; then
        echo ""
        echo "========================================"
        echo "GitLab Server: 提交被拒绝"
        echo "========================================"
        echo "错误：用户名 '$USER_NAME' 格式不符合规范"
        echo "规范：2-4个中文 或 2-20个英文字符"
        echo "========================================"
        echo ""
        exit 1
    fi

    # 校验 Commit Message
    msg_len=${#COMMIT_MSG}

    if [ "$msg_len" -lt 10 ] || [ "$msg_len" -gt 100 ]; then
        echo ""
        echo "========================================"
        echo "GitLab Server: 提交被拒绝"
        echo "========================================"
        echo "错误：Commit message 长度应为 10-100 字符，当前: $msg_len"
        echo "内容: $COMMIT_MSG"
        echo "========================================"
        echo ""
        exit 1
    fi

    if [[ "$COMMIT_MSG" =~ ^[[:space:]] ]] || [[ "$COMMIT_MSG" =~ [[:space:]]$ ]]; then
        echo "错误：Commit message 不能以空格开头或结尾"
        exit 1
    fi

    if [[ "$COMMIT_MSG" =~ [。.]$ ]]; then
        echo "错误：Commit message 不能以句号结尾"
        exit 1
    fi
done

exit 0
```

### 3. Omnibus GitLab 配置

如果是 Omnibus 安装的 GitLab，编辑配置文件：

```bash
# 编辑 gitlab.rb
sudo vim /etc/gitlab/gitlab.rb

# 添加或修改
gitlab_shell['custom_hooks_dir'] = "/opt/gitlab/embedded/service/gitlab-shell/hooks"

# 重载配置
sudo gitlab-ctl reconfigure
```

## 全局 Server Hooks（推荐）

可以为所有仓库配置统一的 hooks：

```bash
# 创建全局 hooks 目录
sudo mkdir -p /opt/gitlab/embedded/service/gitlab-shell/hooks/pre-receive.d

# 创建 hook 脚本
sudo vim /opt/gitlab/embedded/service/gitlab-shell/hooks/pre-receive.d/git-validate

# 添加执行权限
sudo chmod +x /opt/gitlab/embedded/service/gitlab-shell/hooks/pre-receive.d/git-validate
```

## 优缺点

| 优点 | 缺点 |
|------|------|
| **无法绕过** | 需要 GitLab 服务器管理员权限 |
| 执行在服务端，完全可控 | 配置较复杂 |
| 即时反馈，push 瞬间拒绝 | 需要维护服务器脚本 |
| 不依赖 CI/CD 运行 | 升级 GitLab 可能需要重新配置 |

## 与 CI/CD 对比

| 特性 | Server Hooks | GitLab CI |
|------|--------------|-----------|
| 执行时机 | push 瞬间 | pipeline 排队执行 |
| 可被绕过 | ❌ 不能 | ❌ 不能（配合分支保护） |
| 配置位置 | GitLab 服务器 | 仓库内 `.gitlab-ci.yml` |
| 维护成本 | 高（需服务器权限） | 低（版本控制） |
| 灵活性 | 低 | 高 |

**推荐组合**：
- **Server Hooks**：核心规则（用户名格式）
- **GitLab CI**：复杂规则、日志统计、通知
