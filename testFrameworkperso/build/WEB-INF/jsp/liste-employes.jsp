<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<html>
<head>
    <title>ITU Framework - Liste</title>
</head>
<body>
    <h1>Tableau des Employés (Données Transmises)</h1>
    
    <ul>
        <%
            // Récupération de l'attribut poussé par le dispatcher du FrontController
            List<String> maliste = (List<String>) request.getAttribute("liste");
            
            if (maliste != null) {
                for (String emp : maliste) {
        %>
                    <li><%= emp %></li>
        <%
                }
            } else {
        %>
                <p>Aucune donnée reçue dans la requête.</p>
        <%
            }
        %>
    </ul>
</body>
</html>