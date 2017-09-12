<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<!DOCTYPE html>
<html>

<style type="text/css">
    span.error {
        color: red;
    }
</style>

<head>
    <meta charset=utf-8>

    <title>Find Vacancies</title>

    <%--<spring:url value="resources/css/bootstrap.css" var="bootstrap"/>--%>

    <link rel="shortcut icon" href="../../resources/images/logo_16.ico" type="image/x-icon">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="../resources/css/bootstrap.min.css" rel="stylesheet" media="screen"/>
    <link href="../resources/css/dataTables.bootstrap.min.css" rel="stylesheet" media="screen"/>
    <link href="../resources/css/bootstrap-select.css" rel="stylesheet" media="screen"/>


    <script src="../resources/js/jquery_latest.js">
        <%--<script src="resources/js/jquery-1.12.4.js">--%>
    </script>

    <script src="../resources/js/bootstrap.js">
    </script>

    <%--Table--%>
    <%--<script src="resources/js/jquery-1.12.4.js">--%>
    <%--</script>--%>
    <script src="../resources/js/jquery.dataTables.min.js">
    </script>
    <script src="../resources/js/dataTables.bootstrap.min.js">
    </script>
    <script src="../resources/js/bootstrap-select.js">
    </script>

</head>
<body>

<%--<c:url var="excelController" value="/excelExport"/>--%>
<%--<c:url var="xmlController" value="/xmlExport"/>--%>
<c:url var="excelController" value="/FindVacancies/excelExport"/>
<c:url var="xmlController" value="/FindVacancies/xmlExport"/>

<c:set var="now" value="<%=new java.util.Date()%>"/>

<c:set var="searchWords" value="${searchWords}"/>
<c:set var="searchDays" value="${searchDays}"/>

<nav class="navbar navbar-default">
    <div class="container">
        <div class="navbar-header">
            <%--<a class="navbar-brand" href="/">Find Vacancies</a>--%>
            <%--<a class="navbar-brand" href="/FindVacancies/">Find Vacancies</a>--%>
            <a class="navbar-brand" href="/FindVacancies/"><spring:message code="navMenu.home"/></a>
        </div>
        <ul class="nav navbar-nav">
            <li class="dropdown">
                <%--<a class="dropdown-toggle" data-toggle="dropdown" href="#">Export--%>
                <a class="dropdown-toggle" data-toggle="dropdown" href="#"><spring:message code="navMenu.export"/>
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">
                    <li><a href="${excelController}">XLS</a></li>
                    <li><a href="${xmlController}">XML</a></li>
                </ul>
            </li>

            <%--<li class="active">--%>
            <%--<a href="uploadXML.html">Upload XML</a>--%>
            <%--</li>--%>
            <%--<li>--%>
            <%--<a href="downloadXML.html">Download XML</a>--%>
            <%--</li>--%>
        </ul>

        <ul class="nav navbar-nav navbar-right">
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true"
                   aria-expanded="false">
                    <span class="glyphicon glyphicon-globe" aria-hidden="true"></span>
                    <%--Language--%>
                    <spring:message code="navMenu.Language"/>
                    <span class="caret"></span></a>
                <ul class="dropdown-menu">
                    <li><a href="<%=request.getContextPath()%>?languageVar=en">EN</a></li>
                    <li><a href="<%=request.getContextPath()%>?languageVar=ru">RU</a></li>
                </ul>
            </li>
        </ul>

    </div>
</nav>

<div class="container-fluid bg-1">
    <div class="container bg-0 ">
        <div class="row">
            <div class="col-12 text-center">
                <h2>
                    <%--<p> Today is <strong>--%>
                    <p><spring:message code="content.todayIsText"/> <strong>
                        <%--<fmt:formatDate value="${now}"  searchLine="yyyy MMMM dd"/>--%>
                        <fmt:formatDate value="${now}" type="date" pattern="dd MMMM yyyy"/>
                    </strong>
                    </p>
                    <%--<p>"${searchWords}" </p>--%>
                    <%--<p>"${searchDays}" </p>--%>
                </h2>
            </div>
        </div>

        <%--<! Search>--%>
        <c:if test="${not empty message}">
            <div>${message}</div>
        </c:if>
        <%--<c:if test="${not empty messageErrore}"><div>${messageErrore}</div></c:if>--%>

        <%--<form role="form" action="/searchVacancies" method="post" commandName="searchParams">--%>
        <form:form method="post" action="/FindVacancies/searchVacancies" commandName="searchParams">
            <div class="form-group row">
                <div class="col-xs-10">
                    <div class="form-group row">
                        <div class="col-xs-7">
                            <span class="error"><form:errors path="searchLine"/></span>
                        </div>
                        <div class="col-xs-5">
                            <span class="error"><form:errors path="days"/></span>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-xs-7">
                            <form:input path="searchLine" id="searchInput" class="form-control"
                                        placeholder="Search by words"/>
                                <%--<h6 style="color:  #c0c0c0">For exclude word set "-" before it. For example "Java Developer -Senior"</h6>--%>
                            <h6 style="color:  #c0c0c0"><spring:message code="content.explanetionText"/></h6>
                        </div>
                        <div class="col-xs-5">
                            <div class="form-group row">
                                <%--<div class="col-xs-1">--%>
                                <%--</div>--%>
                                    <%--<label class="col-xs-6 control-label text-right" for="daysInput">Vacancies for the last</label>--%>
                                <label class="col-xs-6 control-label text-right" for="daysInput"><spring:message
                                        code="content.vacanciesLastTest"/></label>
                                <div class="col-xs-3">
                                    <form:input path="days" id="daysInput" class="form-control"/>
                                </div>
                                    <%--<label class="col-xs-2 control-label" for="daysInput">days</label>--%>
                                <label class="col-xs-2 control-label" for="daysInput"><spring:message
                                        code="content.daysText"/></label>
                            </div>
                        </div>
                    </div>

                    <div class="form-group row">
                        <div class="col-xs-2 right">
                            <button class="btn btn-default" type="submit"
                                    data-toggle="modal" data-target="#waitModalDialog"
                                    data-backdrop="static" data-keyboard="false">
                                <span class="glyphicon glyphicon-search"></span>
                                <spring:message code="content.button.FiendVacancies"/>
                            </button>
                        </div>
                    </div>
                </div>
                <div class="col-xs-2">
                    <div class="form-group row">
                        <div class="has-error">
                                <%--<form:errors path="sites"/>--%>
                            <span class="error"><form:errors path="sites"/></span>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-xs control-lable" for="sites"><spring:message
                                code="content.selectSites"/></label>
                    </div>
                    <div class="form-group row">
                        <div class="col-xs">
                            <form:select path="sites" items="${sites}" multiple="true" itemValue="displayName"
                                         itemLabel="displayName" class="form-control input-sm"/>

                        </div>
                    </div>
                </div>
            </div>
        </form:form>

    </div>

    <br>
    <%--<br>--%>


    <div class="container bg-0 ">
        <table id="vacanciesTable" class="table table-striped table-bordered dataTable table-hover table-condensed"
               cellspacing="0" width="100%">
            <thead>
            <tr>
                <th>
                    #
                </th>
                <th>
                    <%--TITLE--%>
                    <spring:message code="content.tableFieldTitle"/>
                </th>
                <th>
                    <%--SALARY--%>
                    <spring:message code="content.tableFieldSalary"/>
                </th>
                <th>
                    <%--CITY--%>
                    <spring:message code="content.tableFieldCity"/>
                </th>
                <th>
                    <%--COMPANY--%>
                    <spring:message code="content.tableFieldCompany"/>
                </th>
                <th>
                    <%--SITE--%>
                    <spring:message code="content.tableFieldSite"/>
                </th>
                <th>
                    <%--DATE--%>
                    <spring:message code="content.tableFieldDate"/>
                </th>
            </tr>

            </thead>

            <tbody>
            <c:if test="${not empty resultVacanciesList}">

                <c:forEach var="vacList" items="#{resultVacanciesList}" varStatus="loop">
                    <tr>
                        <td>
                            <c:out value="${loop.index+1}"/>
                        </td>
                        <td>
                            <a href="${vacList.url}" target="_blank"> "${vacList.title}" </a>
                        </td>

                        <td>
                            <c:out value="${vacList.salary}"/>
                                <%--<fmt:formatNumber type="number" value="${vacList.salary}"/>--%>
                        </td>
                        <td>
                            <c:out value="${vacList.city}"/>
                        </td>
                        <td>
                            <c:out value="${vacList.companyName}"/>
                        </td>
                        <td>
                                <%--<c:out value="${vacList.siteName}"/>--%>
                            <a href="${vacList.siteName}" target="_blank"> "${vacList.siteName}" </a>
                        </td>
                        <td>
                                <%--<c:out value="${vacList.date}"/>--%>
                            <fmt:formatDate value="${vacList.date}" pattern="yyyy.MM.dd"/>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
            </tbody>

        </table>
    </div>
    <!-- End Table block -->

    <div class="container bg-0 ">
        <div class="row">
            <div class="col-sm-1 text-right">
                <p><strong>
                    <%--<h5>Export to</h5>--%>
                    <h5><spring:message code="content.exportToText"/></h5>
                </strong></p>
                <%--<p>--%>
                <%--<h5>Export to</h5>--%>
                <%--</p>--%>
            </div>
            <div class="col-sm-2">
                <%--<a href="${excelController}">Excel</a>--%>
                <%--<a class="btn btn-default" href="${excelController}" role="button">Excel</a>--%>
                <a class="btn btn-default" href="${excelController}" role="button"><img
                        src="../../resources/images/icon_file-xls_48_48.png" height="20" align="middle"/>
                </a>
                <%--<button class="btn btn-default" href="${excelController}">--%>
                <%--<img src="../../resources/images/icon_file-xls_48_48.png" height="20" align="middle"/>--%>
                <%--</button>--%>
                <%--<a class="btn btn-default" href="${xmlController}" role="button">XML</a>--%>
                <a class="btn btn-default" href="${xmlController}" role="button"><img
                        src="../../resources/images/icon_file-xml_48_48.png" height="20" align="middle"/>
                </a>

            </div>

        </div>
    </div>
    <br><br><br><br>
    <%--<div class="navbar-fixed-bottom row-fluid">--%>
    <%--<div class="navbar-inner">--%>
    <%--<div class="container">--%>
    <%--<div class="row">--%>
    <%----%>
    <%--</div>--%>
    <%--</div>--%>
    <%--</div>--%>
    <%--</div>--%>


</div>

<footer id="footer" class="footer navbar-fixed-bottom">
    <div class="panel-footer">
        <%--<a href="<%=request.getContextPath()%>?languageVar=en">EN</a>--%>
        <%--<a href="<%=request.getContextPath()%>?languageVar=ru">RU</a>--%>
        Developed by AnGo 2017
    </div>

</footer>

<script>
    $(document).ready(function () {
//        $('#vacanciesTable').DataTable({"iDisplayLength": 50});
        $('#vacanciesTable').DataTable();
    });
</script>

<%--<script>--%>
<%--$('.selectpicker').selectpicker();--%>
<%--</script>--%>

</body>

<!-- Modal -->
<div class="modal fade" id="waitModalDialog" role="dialog" data-backdrop="static" key>
    <%--<div class="modal-dialog modal-sm">--%>
    <div class="modal-dialog">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <%--<button type="button" class="close" data-dismiss="modal">&times;</button>--%>
                <%--<h4 class="modal-title">Processing</h4>--%>
                <h4 class="modal-title"><spring:message code="content.modalProcessing"/></h4>
            </div>
            <div class="modal-body">
                <%--<p style="text-align:center;">Data is loading</p>--%>
                <p style="text-align:center;"><spring:message code="content.modalData"/></p>
                <p style="text-align:center;">
                    <img src="../../resources/gif/loading_apple.gif" alt="Waiting animated GIF" align="middle"
                         style="width:256px;height:256px;">
                </p>
                <%--<img src="path/to/animated.gif" alt="This will display an animated GIF" />--%>
                <%--<p style="text-align:center;">Please wait</p>--%>
                <p style="text-align:center;"><spring:message code="content.modalWait"/></p>

            </div>
            <%--<div class="modal-footer">--%>
            <%--<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>--%>
            <%--</div>--%>
        </div>

    </div>
</div>

</html>