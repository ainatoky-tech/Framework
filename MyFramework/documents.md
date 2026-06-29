# Voici le devoir de *** que les pétards des prof d'ITU de ** demande : 

faire un framework
- créer un front controller servlet 
- le convertir en .jar 


- le front controller en fait tout les doGet et les doPost doivent passé dans ce controller et on affiche les urls que je tape sur la barre de recherche du navigateur car l'Url passe par le front controller voici ce qu'il manque  

donc creer une class qui sera utilisé dans un projet qui sera instancier dans web.xml et créer une application de test pour effectuer le test du .jar créer 

## comment créer un .jar
```bash
javac -cp "/home/itu/Documents/apache-tomcat-10.0.16/lib/servlet-api.jar" -d bin src/framework/annotation/Controller.java src/framework/annotation/UrlMapping.java src/framework/model/Mapping.java src/framework/utils/AnnotationScanner.java src/framework/servlet/FrontController.java

# ou bien 

javac -cp "/home/itu/Documents/apache-tomcat-10.0.16/lib/servlet-api.jar" -d bin $(find src -name "*.java")
# il faut mettre dans cette commande toute les classes que tu utilises s'ils sont important
# il créera un dossier bin avec la même structure que le dossier dans lequel tu travailles 
cd bin
jar -cf mon-framework.jar framework/
# -cf : createfile pour java

```