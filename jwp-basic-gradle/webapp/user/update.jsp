<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="kr">
<head>
    <%@ include file="/include/header.jspf" %>
</head>
<body>
<%@ include file="/include/navigation.jspf" %>

<div class="container" id="main">
    <div class="col-md-8 col-md-offset-2">
        <div class="panel panel-default content-main">
            <h2>회원 정보 수정</h2>
            <h3>정보: ${user.userId}</h3>
            <form action="/user/update" method="post">
                <!-- 변경할 수 없는 사용자 ID값 전달 -->
                <input type="hidden" name="userId" value="${user.userId}">
                이름: <input type="text" name="name" value="${user.name}"><br>
                비밀번호: <input type="text" name="password" value="${user.password}"><br>
                이메일: <input type="text" name="email" value="${user.email}"><br>
                <button type="submit">저장</button>
            </form>
        </div>
    </div>
</div>

<%@ include file="/include/footer.jspf" %>
</body>
</html>