<%@ tag description="admin template" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="body" fragment="true" %>

<t:template>
    <jsp:attribute name="title">
        Admin page
    </jsp:attribute>

    <jsp:body>
        <div class="row">
            <div class="col-sm-3">
                <div class="list-group">
                    <a class="list-group-item" href="<c:url value="/j_spring_security_logout" />">Logout</a>
                </div>
            </div>
            <div class="col-sm-9">
                <jsp:invoke fragment="body"/>
            </div>
        </div>
    </jsp:body>
</t:template>
