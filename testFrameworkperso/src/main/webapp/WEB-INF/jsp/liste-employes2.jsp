<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.UserModel" %>

<% 
    // Récupération de la liste des employés transmise par le contrôleur
    List<UserModel> employes = (List<UserModel>) request.getAttribute("employeListe");
%>

<html>
<head>
    <title>Liste des employés</title>
</head>
<body>
    <h1 style="color: #2c3e50;">Liste des Employés</h1>

    <script>
        console.log("hello");
    </script>

    <ul>
    <% 
        if (employes != null && !employes.isEmpty()) {
            for (UserModel emp : employes) {
    %>
                <li><%= emp.getFunction() %></li>
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