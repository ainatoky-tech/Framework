<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>upload de fichier</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/testcontrolle/upload" 
        method="POST" 
        enctype="multipart/form-data">
        <input type="file" name="file">
        <button type="submit">Uploader</button>
    </form>
</body>
</html>