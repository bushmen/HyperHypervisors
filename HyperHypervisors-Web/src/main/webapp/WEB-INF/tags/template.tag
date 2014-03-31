<%@ tag description="template" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" fragment="true" %>

<html>
<head>
    <title>
        <jsp:invoke fragment="title"/>
    </title>
    <link href="<c:url value="/resources/css/bootstrap.min.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/resources/css/bootstrap-theme.min.css"/>" rel="stylesheet" type="text/css"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body style="margin-top: 20px;">

<script type="text/javascript" src="<c:url value="/resources/js/jquery-2.1.0.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/js/bootstrap.min.js"/>"></script>

<div id="body" class="container">
    <jsp:doBody/>
</div>
</body>
</html>