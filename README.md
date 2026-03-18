# Java 学习代码

参考项目：[java-common-mistakes](https://github.com/JosephZhu1983/java-common-mistakes)

---

## Git 提交规范

为了确保代码提交的质量和可追溯性，本项目配置了 Git 提交前自动校验。

### 快速开始

**1. 配置 Git Hooks（只需执行一次）**

```bash
git config core.hooksPath .githooks
```

**2. 设置你的 Git 用户名（如果不符合规范，提交会被拒绝）**

```bash
# 格式：2-4个中文汉字 或 2-20个英文字符
git config user.name "张三"
# 或
git config user.name "John Smith"
```

### 提交规范要求

#### 1. 用户信息规范

| 项目 | 要求 | 示例 |
|------|------|------|
| `user.name` | 2-4个中文汉字 或 2-20个英文字符 | `张三`、`李小明`、`John Smith` |
| `user.email` | 有效的邮箱格式（推荐） | `yourname@example.com` |

#### 2. Commit Message 规范

| 规则 | 要求 | 错误示例 |
|------|------|----------|
| 长度 | 10-100 个字符 | ❌ `fix bug` (太短) |
| 首尾 | 不能以空格开头或结尾 | ❌ ` 修复问题` 或 `修复问题 ` |
| 结尾 | 不能以句号结尾 | ❌ `修复登录问题。` |
| 内容 | 必须包含中文或英文字符 | ❌ `1234567890` (纯数字) |

**好的提交示例：**
```bash
git commit -m "修复用户登录时的密码验证逻辑问题"
git commit -m "Add user authentication filter for API endpoints"
git commit -m "重构订单模块的数据库查询代码，提升性能"
```

### 常见问题

**Q: 提交时提示"用户名格式不符合规范"怎么办？**

```bash
# 设置符合规范的用户名
git config user.name "张三"

# 如果已提交过，还需要修改历史记录中的作者信息
git commit --amend --author="张三 <your-email@example.com>" --no-edit
```

**Q: 如何跳过校验（不推荐）？**

```bash
# 仅在紧急情况下使用
 git commit -m "你的提交信息" --no-verify
```

**Q: CI/CD 流水线中也会校验吗？**

是的，在 CI/CD 中运行 `mvn validate` 会执行相同的校验规则。

---

## 项目结构

本项目包含多个子模块，每个模块对应不同的技术主题：

| 模块 | 说明 |
|------|------|
| `java_common_mistakes` | Java 常见错误示例 |
| `concurrent` | Java 并发编程 |
| `java_design_pattern` | 设计模式 |
| `java_netty` | Netty 网络编程 |
| `kafka_demo` | Kafka 消息队列 |
| `auth` | JWT 认证 |
| `micrometedemo` | Micrometer 监控指标 |
| `ratelimit` | 限流算法（Sentinel）|
| `rmi` | Java RMI 远程调用 |
| `springsecurity_demo` | Spring Security + OAuth2 |
| `servletTest` | Servlet 技术 |
