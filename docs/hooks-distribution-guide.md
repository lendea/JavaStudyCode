# Git Hooks 跨平台分发指南

针对分散在不同设备（Mac、Linux、Windows）的开发者团队，提供多种分发和安装方案。

## 方案对比

| 方案 | 适用平台 | 难度 | 自动化程度 |
|------|---------|------|-----------|
| **Maven 安装** | 全平台 | ⭐⭐ 简单 | 高 |
| **脚本安装** | 全平台 | ⭐⭐ 简单 | 中 |
| **IDE 集成** | 全平台 | ⭐⭐⭐ 中等 | 高 |
| **容器化** | 全平台 | ⭐⭐⭐⭐ 复杂 | 极高 |

---

## 方案一：Maven 安装（macOS / Linux 推荐）

### 前提条件
- 已安装 JDK 8+
- 已安装 Maven
- 已安装 Git

### 安装命令

```bash
# macOS / Linux 用户
mvn -P install-git-hooks initialize
```

> **注意：** Windows 用户请使用下方的「方案二」或「方案三」

---

## 方案二：脚本安装（全平台）

### macOS / Linux

```bash
# 进入项目目录
cd java-study-code

# 执行安装脚本
bash setup-hooks.sh install

# 检查配置
bash setup-hooks.sh check

# 卸载
bash setup-hooks.sh uninstall
```

### Windows（推荐方式）

**方式 1：双击安装（最简单）**

直接双击 `setup-hooks.bat` 文件，自动完成安装。

**方式 2：PowerShell（手动控制）**

```powershell
# 以管理员身份打开 PowerShell
# 进入项目目录
cd C:\path\to\java-study-code

# 设置执行策略（首次使用需要）
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# 手动复制脚本
copy .githooks\windows\*.ps1 .git\hooks\

# 配置 Git 使用本地 hooks
git config --local core.hooksPath .git/hooks
```

---

## 方案三：IDE 集成

### IntelliJ IDEA / Android Studio

1. **安装 Git Hooks 插件**（可选）
   - File → Settings → Plugins → 搜索 "Git Hooks"

2. **配置提交前检查**
   - File → Settings → Version Control → Commit
   - 勾选 "Run Git hooks"

3. **使用 Maven 安装 hooks**
   - 打开 Terminal（Alt+F12）
   - 运行：`mvn install-git-hooks`

### VS Code

1. 安装扩展：**Git Hooks**
2. 在 Terminal 中运行：`mvn install-git-hooks`

---

## 方案四：容器化（团队标准化）

使用 Docker 确保所有开发者环境一致。

### Dockerfile.dev

```dockerfile
FROM maven:3.8-openjdk-8

# 安装 Git
RUN apt-get update && apt-get install -y git

# 设置工作目录
WORKDIR /workspace

# 复制项目
COPY . .

# 安装 hooks
RUN bash setup-hooks.sh install

# 默认命令
CMD ["/bin/bash"]
```

### docker-compose.yml

```yaml
version: '3.8'
services:
  dev:
    build:
      context: .
      dockerfile: Dockerfile.dev
    volumes:
      - .:/workspace
      - ~/.gitconfig:/root/.gitconfig:ro
    stdin_open: true
    tty: true
```

### 使用方式

```bash
# 启动开发环境
docker-compose run dev bash

# 在容器内开发
git commit -m "测试提交"

# 提交会被 hooks 校验
```

---

## 团队部署策略

### 第一步：项目初始化（一次性）

项目负责人在仓库中添加：

1. `.githooks/` 目录（含全平台脚本）
2. `setup-hooks.sh` / `setup-hooks.bat`
3. `pom.xml` 中的 Maven profile

### 第二步：开发者接入指南

创建 `CONTRIBUTING.md`：

```markdown
## 开发环境配置

### 1. 克隆项目
```bash
git clone https://gitlab.com/your-project.git
cd your-project
```

### 2. 安装 Git Hooks（根据你的系统选择）

**macOS / Linux 用户：**
```bash
# Maven 方式
mvn -P install-git-hooks initialize

# 或脚本方式
bash setup-hooks.sh install
```

**Windows 用户：**
```cmd
# 方式 1：双击运行（最简单）
双击 setup-hooks.bat

# 方式 2：命令行
cd C:\path\to\java-study-code
setup-hooks.bat
```

### 3. 验证安装
```bash
bash setup-hooks.sh check
```

### 4. 开始开发
```bash
git checkout -b feature/your-feature
# ... 修改代码 ...
git commit -m "feat: 添加新功能"
# hooks 会自动校验
```

## 常见问题

### Q: Windows 提示 "无法加载脚本，因为在此系统上禁止运行脚本"

**解决：**
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Q: Maven 命令报错 "bash 不是内部或外部命令"

**解决：**
Windows 需要在 Git Bash 中运行 Maven，或安装 WSL。

### Q: 如何临时跳过 hooks？

**解决（不推荐）：**
```bash
git commit -m "紧急修复" --no-verify
```

> ⚠️ 注意：跳过 hooks 的提交会在 CI 阶段被拒绝

---

## 自动化检查清单

- [ ] 所有开发者已安装 hooks
- [ ] CI/CD 配置了强制校验
- [ ] 分支保护已开启
- [ ] 有文档说明如何安装
- [ ] 有新成员接入流程
