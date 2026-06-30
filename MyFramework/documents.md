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

## Dans docker 
il faut bien sûr qu'il est bien configurée : il faut au moins qu'il possède servlet-api.jar pour fonctionner dans cet endroi en particulier 
```bash
root@de6ce5beef5d:/usr/local# cd tomcat
root@de6ce5beef5d:/usr/local/tomcat# ls
bin           CONTRIBUTING.md  LICENSE         NOTICE         RUNNING.txt    webapps
BUILDING.txt  filtered-KEYS    logs            README.md      temp           webapps.dist
conf          lib              native-jni-lib  RELEASE-NOTES  upstream-KEYS  work
root@de6ce5beef5d:/usr/local/tomcat# cd lib
root@de6ce5beef5d:/usr/local/tomcat/lib# ls
annotations-api.jar                    jaspic-api.jar         tomcat-i18n-ko.jar
catalina-ant.jar                       jsp-api.jar            tomcat-i18n-pt-BR.jar
catalina-ha.jar                        servlet-api.jar        tomcat-i18n-ru.jar
catalina.jar                           tomcat-api.jar         tomcat-i18n-zh-CN.jar
catalina-ssi.jar                       tomcat-coyote-ffm.jar  tomcat-jdbc.jar
catalina-storeconfig.jar               tomcat-coyote.jar      tomcat-jni.jar
catalina-tribes.jar                    tomcat-dbcp.jar        tomcat-util.jar
ecj-4.27.jar                           tomcat-i18n-cs.jar     tomcat-util-scan.jar
el-api.jar                             tomcat-i18n-de.jar     tomcat-websocket.jar
jakartaee-migration-1.0.12-shaded.jar  tomcat-i18n-es.jar     websocket-api.jar
jasper-el.jar                          tomcat-i18n-fr.jar     websocket-client-api.jar
jasper.jar                             tomcat-i18n-ja.jar
root@de6ce5beef5d:/usr/local/tomcat/lib# 


# pour pouvoir activé la construction du .jar il faut maintenant :
javac -cp "/usr/local/tomcat/lib/servlet-api.jar" -d bin $(find src -name "*.java")
cd bin
jar -cf mon-framework.jar src/
```