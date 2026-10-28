<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Connexion - Mon Framework</title>
    <style>
        body { font-family: Arial; display: flex; justify-content: center; align-items: center; height: 100vh; background: #f4f4f4; }
        .box { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); width: 300px; }
        input, button { width: 100%; padding: 10px; margin: 10px 0; box-sizing: border-box; }
        .error { color: red; font-size: 14px; text-align: center; }
    </style>
</head>
<body>
    <div class="box">
        <h2 style="text-align: center;">Connexion</h2>
        
        <!-- Affichage du message d'erreur si le login échoue -->
        <% if(request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="POST">
            <label>Nom d'utilisateur :</label>
            <input type="text" name="username" required>

            <label>Poste (Rôle) :</label>
            <input type="text" name="poste" placeholder="ex: chief, mentor, older" required>

            <button type="submit">Se connecter</button>
        </form>
    </div>
</body>
</html>