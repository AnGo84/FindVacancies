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

    <%--<title>Find Vacancies</title>--%>
    <title>Find Vacancies</title>

    <%--<spring:url value="resources/css/bootstrap.css" var="bootstrap"/>--%>

    <link rel="shortcut icon" href="<c:url value="/resources/images/logo_16.ico"/>" type="image/x-icon">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="<c:url value="/resources/css/bootstrap.min.css"/>" rel="stylesheet" media="screen"/>
    <link href="<c:url value="/resources/css/theme.css"/>" rel="stylesheet" media="screen"/>
    <link href="<c:url value="/resources/css/dataTables.bootstrap.min.css"/>" rel="stylesheet" media="screen"/>
    <link href="<c:url value="/resources/css/bootstrap-select.css"/>" rel="stylesheet" media="screen"/>


    <script src="<c:url value="/resources/js/jquery_latest.js"/>">
        <%--<script src="resources/js/jquery-1.12.4.js">--%>
    </script>

    <script src="<c:url value="/resources/js/bootstrap.js"/>">
    </script>

    <%--Table--%>
    <%--<script src="resources/js/jquery-1.12.4.js">--%>
    <%--</script>--%>
    <script src="<c:url value="/resources/js/jquery.dataTables.min.js"/>">
    </script>
    <script src="<c:url value="/resources/js/dataTables.bootstrap.min.js"/>">
    </script>
    <script src="<c:url value="/resources/js/bootstrap-select.js"/>">
    </script>

</head>
<body>
<%--
<c:url var="excelController" value="/FindVacancies/excelExport"/>
<c:url var="xmlController" value="/FindVacancies/xmlExport"/>
--%>
<c:url var="excelController" value="/excelExport"/>
<c:url var="xmlController" value="/xmlExport"/>

<c:url var="searchVacancies_url"  value="/searchVacancies" />
<c:url var="main_url"  value="/" />

<c:set var="now" value="<%=new java.util.Date()%>"/>

<c:set var="searchWords" value="${searchWords}"/>
<c:set var="searchDays" value="${searchDays}"/>

<c:set var="vacanciesList" value="${resultVacanciesList}"/>

<nav class="navbar navbar-default">
    <div class="container">
        <div class="navbar-header">
            <%--<a class="navbar-brand" href="/">Find Vacancies</a>--%>
            <%--<a class="navbar-brand" href="/FindVacancies/">Find Vacancies</a>--%>
            <a class="navbar-brand" href="${main_url}"><spring:message code="navMenu.home"/></a>
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
                <h3>
                    <%--<p> Today is <strong>--%>
                    <p><spring:message code="content.todayIsText"/> <strong>
                        <%--<fmt:formatDate value="${now}"  searchLine="yyyy MMMM dd"/>--%>
                        <fmt:formatDate value="${now}" type="date" pattern="dd MMMM yyyy"/>
                    </strong>
                    </p>
                    <%--<p>"${searchWords}" </p>--%>
                    <%--<p>"${searchDays}" </p>--%>
                </h3>
            </div>
        </div>

        <%--<! Search>--%>
        <c:if test="${not empty message}">
            <div>${message}</div>
        </c:if>
        <%--<c:if test="${not empty messageErrore}"><div>${messageErrore}</div></c:if>--%>

        <%--<form role="form" action="/searchVacancies" method="post" commandName="searchParams">
        <form:form method="post" action="/FindVacancies/searchVacancies" commandName="searchParams">
        modelAttribute="searchParams"
        --%>

        <form:form method="post" action="${searchVacancies_url}" commandName="searchParams">
            <div class="form-group row">
                <div class="col-xs-10">
                    <div class="form-group row">
                        <div class="col-xs-8">
                            <span class="error"><form:errors path="searchLine"/></span>
                        </div>
                        <div class="col-xs-4">
                            <span class="error"><form:errors path="days"/></span>
                        </div>
                    </div>
                    <div class="form-group row">
                        <div class="col-xs-8">
                            <form:input path="searchLine" id="searchInput" class="form-control"
                                        placeholder="Search by words"/>
                                <%--<h6 style="color:  #c0c0c0">For exclude word set "-" before it. For example "Java Developer -Senior"</h6>--%>
                            <h6 style="color:  #c0c0c0"><spring:message code="content.explanetionText"/></h6>
                        </div>
                        <div class="col-xs-4">
                            <div class="form-group row">
                                    <%--<div class="col-xs-1">--%>
                                    <%--</div>--%>
                                    <%--<label class="col-xs-6 control-label text-right" for="daysInput">Vacancies for the last</label>--%>
                                <label class="col-xs-7 control-label text-right" for="daysInput"><spring:message
                                        code="content.vacanciesLastTest"/></label>
                                <div class="col-xs-2">
                                    <form:input path="days" id="daysInput" class="form-control"/>
                                </div>
                                    <%--<label class="col-xs-2 control-label" for="daysInput">days</label>--%>
                                <label class="col-xs-3 control-label" for="daysInput"><spring:message
                                        code="content.daysText"/></label>
                            </div>
                        </div>
                    </div>

                    <div class="form-group row">
                        <div class="col-xs-2 right">
                            <button class="btn btn-info" type="submit"

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
            <c:if test="${not empty vacanciesList}">
                <%--<c:forEach var="vacancy" items="#{vacanciesList}"  varStatus="loop">--%>
                <c:set var="num" scope="page" value='0'/>
                <c:set var="vacanciesList" value="${resultVacanciesList}"/>
                <%--<c:forEach var="vacancy" items="#{vacanciesList}"  varStatus="loop">--%>
                <c:forEach var="vacancy" items="#{vacanciesList}">
                    <tr>
                        <td>
                                <%--<c:out value="${loop.count}"/>--%>
                            <c:set var="num" value="${num + 1}" scope="page"/>
                            <c:out value="${num}"/>
                        </td>
                        <td>
                            <a href="${vacancy.url}" target="_blank"> "${vacancy.title}" </a>
                        </td>

                        <td>
                            <c:out value="${vacancy.salary}"/>
                                <%--<fmt:formatNumber type="number" value="${vacList.salary}"/>--%>
                        </td>
                        <td>
                            <c:out value="${vacancy.city}"/>
                        </td>
                        <td>
                            <c:out value="${vacancy.companyName}"/>
                        </td>
                        <td>
                                <%--<c:out value="${vacList.siteName}"/>--%>
                            <a href="${vacancy.siteName}" target="_blank"> "${vacancy.siteName}" </a>
                        </td>
                        <td>
                                <%--<c:out value="${vacList.date}"/>--%>
                            <fmt:formatDate value="${vacancy.date}" pattern="yyyy.MM.dd"/>
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
                        src="<c:url value="/resources/images/icon_file-xls_48_48.png"/>" height="15" width="35" align="middle"/>
                </a>
                <%--<button class="btn btn-default" href="${excelController}">--%>
                <%--<img src="../../resources/images/icon_file-xls_48_48.png" height="20" align="middle"/>--%>
                <%--</button>--%>
                <%--<a class="btn btn-default" href="${xmlController}" role="button">XML</a>--%>
                <a class="btn btn-default" href="${xmlController}" role="button"><img
                        src="<c:url value="/resources/images/icon_file-xml_48_48.png"/>" height="15" width="35" align="middle"/>
                </a>

            </div>

        </div>
    </div>
    <br><br><br><br>


</div>

<footer id="footer" class="footer navbar-fixed-bottom">
    <div class="panel-footer">
        <%--<a href="<%=request.getContextPath()%>?languageVar=en">EN</a>--%>
        <%--<a href="<%=request.getContextPath()%>?languageVar=ru">RU</a>--%>
        <%--Developed by AnGo 2017--%>
        <spring:message code="footer.copyRight"/>
    </div>

</footer>

<script>
    $(document).ready(function () {
        // DataTable
        $('#vacanciesTable').DataTable(
            {
                "language": {
                    //"lengthMenu": "Display _MENU_ records per page",
                    "lengthMenu": '<spring:message code="table.lengthMenu" javaScriptEscape="true" />',
                    "zeroRecords": '<spring:message code="table.zeroRecords" javaScriptEscape="true"/>',
                    "info": '<spring:message code="table.info" javaScriptEscape="true"/>',
                    "infoEmpty": '<spring:message code="table.infoEmpty" javaScriptEscape="true"/>',
                    "infoFiltered": '<spring:message code="table.infoFiltered" javaScriptEscape="true"/>',
                    "search": '<spring:message code="table.search" javaScriptEscape="true"/>',
                    "first": '<spring:message code="table.first" javaScriptEscape="true"/>',
                    "last": '<spring:message code="table.last" javaScriptEscape="true"/>',
                    "next": '<spring:message code="table.next" javaScriptEscape="true"/>',
                    "previous": '<spring:message code="table.previous" javaScriptEscape="true"/>'
                },
                "lengthMenu": [[10, 15, 20, 25, 50, -1], [10, 15, 20, 25, 50, "All"]],
                "iDisplayLength": 15
            }
        );

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
                    <img src="<c:url value="/resources/gif/loading_apple.gif"/>" alt="Waiting animated GIF" align="middle"
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