# 项目记忆

## Git 校验逻辑接口化改造 (2026-03-18)

### 背景
将现有的 Git 提交前校验逻辑（用户名、commit message 校验）从纯本地 Bash 脚本改造为 **「本地优先 + 接口增强」** 的混合架构。

### 实现内容

1. **新建模块** `git-validate-service/`
   - Spring Boot 2.7.18 服务
   - 端口：8088
   - 提供 REST API 进行用户名和 commit message 校验
   - 主要接口：
     - `POST /api/git/validate` - 综合校验
     - `POST /api/git/validate/user` - 用户名校验
     - `POST /api/git/validate/message` - commit message 校验

2. **改造 Hook 脚本**
   - `.githooks/pre-commit` - 本地校验 + 可选的服务端用户名校验
   - `.githooks/commit-msg` - 本地校验 + 可选的服务端 message 校验
   - 支持降级：接口失败时自动使用本地校验结果
   - 可配置：`ENABLE_API_CALL`、`TIMEOUT`、`OFFLINE_MODE`

### 使用方法

1. 启动服务：
   ```bash
   cd git-validate-service
   mvn spring-boot:run
   ```

2. 提交代码时会自动调用校验：
   - 本地校验始终执行
   - 服务端校验可选（默认开启）

### 架构优势
- 离线可用：本地校验作为兜底
- 扩展性强：可对接员工系统、添加统计分析
- 降级策略：网络故障时不阻断提交

### 安全性说明

**本地 hooks 容易被绕过**：`--no-verify`、删除 `.githooks`、修改脚本等

**真正的强制校验必须在服务端（CI/CD）**：
1. **CI 独立校验**：`.github/workflows/git-validate.yml` / `.gitlab-ci.yml` 不依赖 pom.xml
2. **Code Review**：人工审查 pom.xml 和 CI 配置的改动
3. **分支保护**：GitHub/GitLab Branch Protection + Required Status Checks（最强防线）

**防护层次**：
```
本地 hooks（提醒） → CI 校验（门禁） → 分支保护（机制保证）
     可被绕过            不可绕过              不可绕过
```

### GitLab 特有方案

GitLab 提供 **Server Hooks** 原生支持，在服务器端执行，开发者完全无法绕过：

```
开发者 push ──▶ GitLab Server ──▶ pre-receive hook 校验 ──▶ 拒绝/接受
                                       (服务端执行)
```

| 平台 | Server Hooks | Push Rules | 推荐组合 |
|------|-------------|-----------|---------|
| GitLab | ✅ 原生支持 | ✅ 支持 | Server Hooks + CI + 分支保护 |
| GitHub | ❌ 仅 Enterprise | ❌ 不支持 | CI + 分支保护 |

### 跨平台分发方案

针对分散在不同设备（Mac、Linux、Windows）的开发者团队：

**1. 多平台脚本**
- `.githooks/pre-commit` - macOS/Linux
- `.githooks/windows/pre-commit.ps1` - Windows PowerShell
- `.githooks/windows/pre-commit.bat` - Windows CMD

**2. 自动安装工具**
- `setup-hooks.sh` - Unix/macOS/Windows(Git Bash)
- `setup-hooks.bat` - Windows 双击安装
- Maven Profile: `mvn install-git-hooks`

**3. 安装方式**
```bash
# macOS / Linux
mvn -P install-git-hooks initialize
# 或
bash setup-hooks.sh install

# Windows（推荐双击）
# 双击 setup-hooks.bat
# 或命令行：
setup-hooks.bat
```

**4. 文档**
- `docs/hooks-distribution-guide.md` - 完整分发指南
- `CONTRIBUTING.md`（建议创建）- 新成员接入流程
