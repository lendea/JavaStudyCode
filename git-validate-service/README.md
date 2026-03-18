# Git Validate Service

Git 提交校验服务端 - 支持用户名和 Commit Message 校验。

## 功能特性

- **用户名校验**：支持 2-4 个中文或 2-20 个英文字符
- **邮箱校验**：可选的邮箱格式验证
- **Commit Message 校验**：长度、格式、内容规范检查
- **接口化架构**：支持本地优先 + 接口增强的混合模式
- **离线降级**：网络不可用时自动降级为本地校验

## 快速开始

### 1. 启动服务

```bash
cd git-validate-service
mvn spring-boot:run
```

服务将在 `http://localhost:8088` 启动。

### 2. 验证服务状态

```bash
curl http://localhost:8088/api/git/health
```

### 3. 测试校验接口

**校验用户名：**
```bash
curl -X POST "http://localhost:8088/api/git/validate/user" \
  -d "userName=张三" \
  -d "userEmail=zhangsan@example.com"
```

**校验 Commit Message：**
```bash
curl -X POST "http://localhost:8088/api/git/validate/message" \
  -d "commitMessage=修复用户登录功能的 bug"
```

**综合校验：**
```bash
curl -X POST "http://localhost:8088/api/git/validate" \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "张三",
    "userEmail": "zhangsan@example.com",
    "commitMessage": "修复用户登录功能的 bug",
    "projectName": "java-study-code",
    "branchName": "feature/login"
  }'
```

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/git/health` | GET | 健康检查 |
| `/api/git/validate` | POST | 综合校验 |
| `/api/git/validate/user` | POST | 仅校验用户名 |
| `/api/git/validate/message` | POST | 仅校验 commit message |

## 配置说明

编辑 `.githooks/pre-commit` 和 `.githooks/commit-msg` 中的配置：

```bash
# ========== 配置 ==========
API_URL="http://localhost:8088/api/git/validate/user"  # 服务端地址
ENABLE_API_CALL=true      # 是否启用接口校验
TIMEOUT=5                 # 接口超时时间（秒）
OFFLINE_MODE=false        # 离线模式（仅本地校验）
```

## 架构说明

采用 **「本地优先 + 接口增强」** 的混合架构：

1. **本地校验**：快速验证基础规则，不依赖网络
2. **接口校验**：服务端验证，支持扩展功能（日志、统计等）
3. **降级策略**：接口失败时自动使用本地校验结果

## 扩展能力

接口化后可轻松扩展：
- 对接员工系统验证用户名
- 项目差异化校验规则
- 提交统计与分析
- AI 辅助改进建议
- 实时通知（钉钉/企业微信）
