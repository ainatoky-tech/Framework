<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Insertion d'utilisateur</title>
</head>
<body>
    <h1>Ajouter un utilisateur</h1>

    <form action="${pageContext.request.contextPath}/emp/save" method="POST">
        <label>Nom d'utilisateur :</label>
        <input type="text" name="username" required>
        <br>

        <label>Fonction :</label>
        <input type="text" name="function" required>
        <br>

        <label>Poste / Extension :</label>
        <input type="text" name="extension" required>
        <br>

        <button type="submit">Envoyer</button>
    </form>


    <form action="${pageContext.request.contextPath}/emp/save-objet" method="POST">
            <h2>Binding par objet</h2>
            <input type="text" name="username" placeholder="Nom d'utilisateur" required>
            <input type="text" name="function" placeholder="Fonction" required>
            <input type="text" name="extension" placeholder="Poste / Extension" required>
            <button type="submit">Envoyer</button>
        </form>
</body>
</html>