<%--
  Created by IntelliJ IDEA.
  User: dpb
  Date: 2021/3/16
  Time: 16:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
  <title>Title</title>
</head>
<body>
<h1>登录页面</h1>
<form action="/login" method="post">
  账号:<input type="text" name="username"><br>
  密码:<input type="password" name="password"><br>
  <security:csrfInput/>
  <input type="checkbox" name="remember-me" value="true"> 记住我 <br>
  <input type="submit" value="登录">
</form>
</body>
</html>
