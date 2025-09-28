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
            <h3>사용자 목록</h3>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>#</th>
                    <th>UserId</th>
                    <th>Password</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>관리</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${users}" var="user" varStatus="status">
                    <tr>
                        <th scope="row">${status.count}</th>
                        <td>${user.userId}</td>
                        <td>${user.password}</td>
                        <td>${user.name}</td>
                        <td>${user.email}</td>
                        <td>
                            <a href="/user/updateForm?userId=${user.userId}" class="btn btn-success btn-sm">
                                수정
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/include/footer.jspf" %>
</body>
</html>