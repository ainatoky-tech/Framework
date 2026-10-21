<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String message = (String) request.getAttribute("message");
    if (message != null) {
        out.println("<p>" + message + "</p>");
    } else {
        out.println("Tomcat est bien configuré et le moteur JSP tourne !");
    }
%>