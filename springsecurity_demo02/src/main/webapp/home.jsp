<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<html>
<body>
<h2>HOME!</h2>
<security:authentication property="principal.username" />
<security:authorize access="hasAnyRole('ROLE_USER')" >
  <a href="#">用户查询</a><br>
</security:authorize>
<security:authorize access="hasAnyRole('ROLE_ADMIN')" >
  <a href="#">用户添加</a><br>
</security:authorize>
<security:authorize access="hasAnyRole('ROLE_USER')" >
  <a href="#">用户更新</a><br>
</security:authorize>
<security:authorize access="hasAnyRole('ROLE_ADMIN')" >
  <a href="#">用户删除</a><br>
</security:authorize>
</body>
</html>
