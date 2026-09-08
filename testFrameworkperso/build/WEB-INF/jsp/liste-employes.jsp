<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>Liste des employés</title>
</head>
<body>
    <h1 style="color: #2c3e50;">Liste des Employés</h1>

    <script>
        console.log("hello")
    </script>
    <ul>
    <% 
        List<String> employes = (List<String>) request.getAttribute("liste");
        if (employes != null && !employes.isEmpty()) {
            for (String emp : employes) {
    %>
                <li><%= emp %></li>
    <% 
            }
        } else {
    %>
            <li>Aucun employé trouvé.</li>
    <% 
        }
    %>
    </ul>
</body>
</html>

    