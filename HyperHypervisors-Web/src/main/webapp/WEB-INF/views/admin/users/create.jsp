<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<t:logged_template>
    <jsp:attribute name="body">
        <c:url var="createUserAction" value="/admin/users/create"/>
        <form:form action="${createUserAction}" modelAttribute="user" method="post" cssClass="form-horizontal">
            <fieldset>
                <legend>Create user</legend>
                <div class="form-group">
                    <form:label for="login" path="login" class="col-sm-4 control-label">Login</form:label>

                    <div class="col-sm-8">
                        <form:input id="login" path="login" cssClass="form-control" placeholder="Login"/>
                        <form:errors path="login" cssClass="error"/>
                    </div>
                </div>
                <div class="form-group">
                    <form:label for="password" path="password" class="col-sm-4 control-label">Password</form:label>

                    <div class="col-sm-8">
                        <form:password id="password" path="password" cssClass="form-control" placeholder="Password"/>
                        <form:errors path="password" cssClass="error"/>
                    </div>
                </div>
                <div class="form-group">
                    <form:label for="roles" path="roles" class="col-sm-4 control-label">User roles</form:label>

                    <div class="col-sm-8">
                        <c:forEach items="${userRoles}" var="role">
                            <div class="checkbox">
                                <form:checkbox path="roles" label="${role}" value="${role}"/>
                            </div>
                        </c:forEach>
                        <form:errors path="roles" cssClass="error col-sm-6"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-4 col-sm-8">
                        <input type="submit" value="Save" class="btn btn-primary"/>
                    </div>
                </div>
            </fieldset>
        </form:form>
    </jsp:attribute>
</t:logged_template>