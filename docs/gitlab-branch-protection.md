# GitLab 分支保护配置指南

分支保护是 GitLab 中防止不合规代码进入主分支的重要机制。

## 配置路径

```
Project Settings → Repository → Protected Branches
```

## 推荐配置

### 1. 保护 main/master 分支

| 设置项 | 推荐值 | 说明 |
|--------|--------|------|
| Branch | `main` 或 `master` | 主分支 |
| Allowed to push | `No one` 或 `Maintainers` | 禁止直接 push |
| Allowed to merge | `Maintainers` + `Developers` | 允许 MR 合并 |
| Require approval from CODEOWNERS | ✅ 勾选 | 代码所有者审批 |

### 2. 强制 Pipeline 成功

在 **Settings → Merge Requests** 中：

```
[✅] Only allow merge requests to be merged if the pipeline succeeds
[✅] Only allow merge requests to be merged if all discussions are resolved
[✅] Show link to create/view MR when pushing from the command line
```

### 3. 合并检查清单

在 **Settings → Merge Requests → Merge checks**：

```
[✅] Pipeline must succeed
[✅] All threads must be resolved
[✅] No approval from the author of the merge request
[✅] CODEOWNERS approval required (optional)
```

## 完整防护流程

```
开发者 push 到 feature 分支
           │
           ▼
┌─────────────────────┐
│ Server Hook 校验    │  ← 第一层：即时拒绝不合规提交
│ (用户名、message)   │
└──────────┬──────────┘
           │
           ▼
    创建 Merge Request
           │
           ▼
┌─────────────────────┐
│ GitLab CI 校验      │  ← 第二层：Pipeline 必须成功
│ (详细规则检查)      │
└──────────┬──────────┘
           │
           ▼
    Code Review 审批
           │
           ▼
┌─────────────────────┐
│ 分支保护规则        │  ← 第三层：只有特定角色可合并
│ (Maintainer 合并)   │
└──────────┬──────────┘
           │
           ▼
    合并到 main/master
```

## 与 GitHub 对比

| 功能 | GitLab | GitHub |
|------|--------|--------|
| 分支保护 | ✅ Protected Branches | ✅ Branch Protection Rules |
| 强制 Status Check | ✅ Pipelines must succeed | ✅ Require status checks |
| CODEOWNERS | ✅ 支持 | ✅ 支持 |
| Server Hooks | ✅ 原生支持 | ❌ 需 GitHub Enterprise |
| Push Rules | ✅ 支持 | ❌ 不支持 |
| 审批规则 | ✅ 更灵活 | ✅ 基础支持 |
