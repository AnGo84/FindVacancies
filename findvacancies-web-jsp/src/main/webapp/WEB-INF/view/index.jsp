<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<!DOCTYPE html>
<html>

<head>
    <meta charset=utf-8>

    <title>Find Vacancies</title>

    <link rel="shortcut icon" href="<c:url value="/resources/images/logo_16.ico"/>" type="image/x-icon">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="<c:url value="/resources/css/bootstrap.min.css"/>" rel="stylesheet" media="screen"/>
    <link href="<c:url value="/resources/css/theme.css"/>" rel="stylesheet" media="screen"/>
    <link href="<c:url value="/resources/css/dataTables.bootstrap.min.css"/>" rel="stylesheet" media="screen"/>
    <link href="<c:url value="/resources/css/responsive.dataTables.min.css"/>" rel="stylesheet" media="screen"/>
    <link href="<c:url value="/resources/css/bootstrap-select.css"/>" rel="stylesheet" media="screen"/>

    <link href="<c:url value="/resources/css/bootstrap-multiselect.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/resources/css/app.css"/>" rel="stylesheet"/>

    <script src="<c:url value="/resources/js/jquery_latest.js"/>"></script>

    <script src="<c:url value="/resources/js/bootstrap.js"/>"></script>

    <%--Table--%>
    <script src="<c:url value="/resources/js/jquery.dataTables.min.js"/>"></script>
    <script src="<c:url value="/resources/js/dataTables.bootstrap.min.js"/>"></script>
    <script src="<c:url value="/resources/js/dataTables.responsive.min.js"/>"></script>
    <%--Select--%>
    <script src="<c:url value="/resources/js/bootstrap-select.js"/>"></script>
    <script src="<c:url value="/resources/js/bootstrap-multiselect.js"/>"></script>

</head>
<body>

<c:url var="excelController" value="/excelExport"/>
<c:url var="xmlController" value="/xmlExport"/>

<c:url var="searchVacancies_url" value="/searchVacancies"/>
<c:url var="main_url" value="/"/>

<c:set var="timeNow" value="<%=new java.util.Date()%>"/>

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
                    <li><a href="<%=request.getContextPath()%>?lang=en">EN</a></li>
                    <li><a href="<%=request.getContextPath()%>?lang=ru">RU</a></li>
                    <li><a href="<%=request.getContextPath()%>?lang=uk">UA</a></li>
                </ul>
            </li>
        </ul>

    </div>
</nav>

<div class="container bg-0 ">

    <div class="row top-row">
        <div class="col-xs-12 col-md-8">
            <h4 class="text-center"><spring:message code="content.aboutText"/></h4>
        </div>
        <div class="col-xs-12 col-md-4 text-right">
            <h6>
                <p><spring:message code="content.todayIsText"/>
                    <strong>
                        <fmt:formatDate value="${timeNow}" type="date" pattern="dd MMMM yyyy"/>
                    </strong>
                </p>
            </h6>
        </div>
    </div>

    <%--<! Search>--%>
    <c:if test="${not empty message}">
        <hr/>
        <div>${message}</div>
        <hr/>
    </c:if>
    <%--<c:if test="${not empty messageErrore}"><div>${messageErrore}</div></c:if>--%>

    <form:form method="post" action="${searchVacancies_url}" modelAttribute="viewSearchParams">

        <div class="form-group row search-params">
                <%--Search line--%>
            <div class="col-xs-12 col-lg-6">
                <div class="has-error">
                    <span class="error"><form:errors path="searchLine"/></span>
                </div>
                <div class="col-xs-12 search-line">
                    <form:input path="searchLine" id="searchInput" class="form-control"
                                placeholder="Search by words"/>
                    <h6 class="description"><spring:message code="content.explanationText"/></h6>
                </div>
            </div>
                <%--Sites list--%>
            <div class="col-xs-6 col-sm-6 col-md-4 col-lg-3 extra-small">
                <div class="has-error">
                    <span class="error"><form:errors path="sites"/></span>
                </div>
                <div class="form-group row">
                    <div class="col-xs-12">
                        <form:select id="select_sites" path="sites" items="${sites}" multiple="true"
                                     itemValue="displayName"
                                     itemLabel="displayName" class="form-control input-sm"/>
                    </div>
                </div>
            </div>
                <%--Days--%>
            <div class="col-xs-6 col-sm-6 col-md-4 col-lg-3 extra-small">
                <div class="has-error">
                    <span class="error"><form:errors path="days"/></span>
                </div>
                <div class="form-group row search-days">
                    <label class="control-label" for="daysInput"><spring:message
                            code="content.vacanciesLastTest"/></label>
                    <form:input path="days" id="daysInput" class="form-control"/>
                    <label class="control-label" for="daysInput"><spring:message
                            code="content.daysText"/></label>
                </div>
            </div>
        </div>

        <div class="form-group row">
                <%--Search button--%>
            <div class="col-xs-12 col-md-4 col-lg-3">
                <button class="btn btn-info" type="submit"
                        data-toggle="modal" data-target="#waitModalDialog"
                        data-backdrop="static" data-keyboard="false">
                    <span class="glyphicon glyphicon-search"></span>
                    <spring:message code="content.button.FiendVacancies"/>
                </button>
            </div>
        </div>

    </form:form>

</div>

<%--<br>--%>

<div class="container bg-0 ">
    <div class="form-group row">
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
                    <%--COMPANY--%>
                    <spring:message code="content.tableFieldCompany"/>
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
                            <c:out value="${vacancy.companyName}"/>
                        </td>
                        <td>
                            <c:out value="${vacancy.salary}"/>
                                <%--<fmt:formatNumber type="number" value="${vacList.salary}"/>--%>
                        </td>
                        <td>
                            <c:out value="${vacancy.city}"/>
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
</div>
<!-- End Table block -->
<hr/>
<div class="container bg-0 ">
    <div class="form-inline">
        <div class="form-group">
            <p><strong>
                <h5><spring:message code="content.exportToText"/>: </h5>
            </strong></p>
        </div>
        <a class="btn btn-default export-button" href="${excelController}" role="button">
            <img src="<c:url value="/resources/images/icon_file-xls_48_48.png"/>"/>
        </a>
        <a class="btn btn-default export-button" href="${xmlController}" role="button">
            <img src="<c:url value="/resources/images/icon_file-xml_48_48.png"/>"/>
        </a>

    </div>

    </br>
</div>

<footer id="footer" class="footer">
    <div class="panel-footer">
        <spring:message code="footer.copyRight"/>
    </div>
</footer>

<script>
    if ($(document).height() <= $(window).height())
      $("footer.footer").addClass("navbar-fixed-bottom");
</script>

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
                "iDisplayLength": 15,
                responsive: true
            }
        );
    });
</script>

<script type="text/javascript">
    $(document).ready(function () {
        $('#select_sites').multiselect({
            buttonWidth: '100%',
            includeSelectAllOption: true,
            selectAllText: '<spring:message code="sites.selectAllText" javaScriptEscape="true"/>',
            nonSelectedText: '<spring:message code="sites.nonSelectedText" javaScriptEscape="true"/>',
            allSelectedText: '<spring:message code="sites.allSelectedText" javaScriptEscape="true"/>'
        });
    });
</script>

</body>

<!-- Modal -->
<div class="modal fade" id="waitModalDialog" role="dialog" data-backdrop="static" key>
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title"><spring:message code="content.modalProcessing"/></h4>
            </div>
            <div class="modal-body">
                <%--<p style="text-align:center;">Data is loading</p>--%>
                <p class="waiting-dialog-el"><spring:message code="content.modalData"/></p>
                <p class="waiting-dialog-el">
                    <img class="waiting-dialog-el" src="<c:url value="/resources/gif/loading_apple.gif"/>"
                         alt="Waiting animated GIF">
                </p>
                <p class="waiting-dialog-el"><spring:message code="content.modalWait"/></p>

            </div>
        </div>

    </div>
</div>

</html>