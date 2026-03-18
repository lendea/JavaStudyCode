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
