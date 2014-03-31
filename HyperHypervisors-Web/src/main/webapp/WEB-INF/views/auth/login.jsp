<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:template>
    <jsp:attribute name="title">
        Login page
    </jsp:attribute>

    <jsp:body>
        <script>
            window.onload = function () {
                document.f.j_username.focus();
            }
        </script>

        <div class="well col-sm-4 col-sm-offset-4">
            <c:if test="${not empty param.login_error}">
                <div class="alert alert-danger">
                    <a class="close" data-dismiss="alert" href="#" aria-hidden="true">&times;</a>
                    <p>
                        Your login attempt was not successful due to error:
                        ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}.
                    </p>
                    <p>Please, try again.</p>
                </div>
            </c:if>

            <form name="f" method="post" action="<c:url value="/j_spring_security_check"/>" class="form-horizontal">
                <fieldset>
                    <legend>Log in</legend>
                    <div class="form-group">
                        <label for="login" class="col-sm-5 control-label">Login</label>

                        <div class="col-sm-7">
                            <input id="login" name="j_username" class="form-control"
                                   placeholder="User name"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="password" class="col-sm-5 control-label">Password</label>

                        <div class="col-sm-7">
                            <input type="password" id="password" name="j_password" class="form-control"
                                   placeholder="Password"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-5 col-sm-7">
                            <input type="submit" value="Log in" class="btn btn-primary"/>
                        </div>
                    </div>
                </fieldset>
            </form>
        </div>
    </jsp:body>
</t:template>
