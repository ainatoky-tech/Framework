<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
    <h2>Liste des Employés</h2>
    <ul>
        <% 
            // Attention : getAttribute retourne un Object, il faut caster
            Object obj = request.getAttribute("liste"); 
            if (obj != null) {
                java.util.List<String> list = (java.util.List<String>) obj;
                for (String emp : list) {
                    out.println("<li>" + emp + "</li>");
                }
            } else {
                out.println("<li>Aucune donnée trouvée dans la requête !</li>");
            }
        %>
    </ul>
</body>
</html>