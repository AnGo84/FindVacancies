<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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

</head>
<body>

<c:url var="main_url"  value="/" />

<div class="container-fluid bg-1">
    <div class="container bg-0 ">
        <div class="row">
            <br><br>
            <h2>Application Error, please contact support.</h2>

            <h3>Debug Information:</h3>

            Requested URL= ${url}<br><br>

            Info= ${errorInfo}<br><br>

            Exception= ${exception.message}<br><br>

            <strong>Exception Stack Trace</strong><br>
            <c:forEach items="${exception.stackTrace}" var="ste">
                ${ste}
            </c:forEach> <br><br>
        </div>

        <div class="row">
            <div class="col-12">
                <a class="btn btn-link" href="${main_url}"><spring:message code="button.back"/></a>
            </div>
        </div>
    </div>
</div>
</body>
</html>