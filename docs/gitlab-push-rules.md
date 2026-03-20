# GitLab Push Rules 配置指南

Push Rules 是 GitLab 原生支持的推送限制功能，在 **Project Settings → Repository → Push Rules** 中配置。

## 配置项

### 1. Commit message 正则校验

```
Commit message 正则表达式：
^(?![\s.]).{10,100}$
```

**含义：**
- `(?![\s.])` - 不能以空格或句号开头
- `.{10,100}` - 长度 10-100 字符

### 2. Commit author email 正则

```
Author email 正则表达式：
^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
```

### 3. 分支命名规则

```
Branch name 正则表达式：
^(feature|bugfix|hotfix|release)/.+$
```

强制使用 Git Flow 风格的分支命名。

### 4. 禁止删除分支

勾选：
- [x] **Reject unsigned commits**（可选）
- [x] **Do not allow users to remove Git tags with `git push`**

## 限制

Push Rules 只能做简单的正则校验，**无法校验中文用户名格式**（2-4个中文），需要配合 Server Hooks 或 CI 使用。
