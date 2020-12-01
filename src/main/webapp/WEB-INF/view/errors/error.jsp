<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Error Page</title>

    <link rel="shortcut icon" href="<c:url value="/resources/images/logo_16.ico"/>" type="image/x-icon">

    <link href="<c:url value="/resources/css/bootstrap.min.css"/>" rel="stylesheet" media="screen"/>
    <link href="<c:url value="/resources/css/theme.css"/>" rel="stylesheet" media="screen"/>
    <link href="<c:url value="/resources/css/dataTables.bootstrap.min.css"/>" rel="stylesheet" media="screen"/>

    <script src="<c:url value="/resources/js/jquery_latest.js"/>">
    </script>

    <script src="<c:url value="/resources/js/bootstrap.js"/>">
    </script>

    <style>
        table td{
            vertical-align:top;
            border:solid 1px #888;
            padding:10px;
        }
    </style>
</head>
<body>

<c:url var="main_url"  value="/" />

<div class="container-fluid bg-1">
    <div class="container bg-0 ">

    <div class="row">
            <div class="col-12">
                <br>
                <h2>Application Error, please contact support.</h2>
                <br>
                <a class="btn btn-link" href="${main_url}"><spring:message code="button.main"/></a>
                <br>
            </div>
    </div>
    <h3>Debug Information:</h3>
        <table>
            <tr>
                <td>Date</td>
                <td>
                    <!-- ${timestamp} -->
                    <fmt:formatDate value="${timestamp}" type="date" pattern="dd MMMM yyyy HH:mm:ss"/>
                </td>
            </tr>
            <tr>
                <td>Requested URL</td>
                <td>${url}</td>
            </tr>
            <tr>
                <td>Status</td>
                <td>${errorInfo}</td>
            </tr>
            <tr>
                <td>Message</td>
                <td>${errorMsg}</td>
            </tr>
            <tr>
                <td>Exception</td>
                <td>${exception.message}</td>
            </tr>
            <tr>
                <td>Trace</td>
                <td>
                   <!-- <pre>${trace}</pre> -->
                   <c:forEach items="${exception.stackTrace}" var="ste">
                      ${ste}
                   </c:forEach>
                </td>
            </tr>
        </table>

        <div class="row">
            <div class="col-12">
                <a class="btn btn-link" href="${main_url}"><spring:message code="button.back"/></a>
            </div>
        </div>
    </div>
</div>
</body>
</html>