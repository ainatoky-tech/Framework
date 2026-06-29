# voici le devoir en gros :
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
version="2.4">
<display-name>Application WEB affichant HelloWorld</display-name>
    <servlet>
        <servlet-name></servlet-name>
        <servlet-class></servlet-class>
    </servlet>
    <servlet-mapping> 
        <servlet-name></servlet-name>
        <url-pattern></url-pattern>
    </servlet-mapping>
</web-app> 
```


spring 2:
on a la liste de controlleur lorsqu 'on tape une url dans le navigateur
lorsqu'on lance une url on doit savoir quel controlleur et quel méthode 
Créer une annotation qu'on puisse mettre sur une méthode mais qui nécéssite cette fois une valeur
-> EmpController (voici la classe a créer n'oublie pas le @Controller )
ex :
on a une méthode liste 

@UrlMapping("/emp/list")
list

on doit afficher 
/emp/list se trouve dans EmpController et execute la methode create
si l'url est inconnue throws exception et montre 

il y a une question de stockage je ne sais pas encore 
créer un repository github et ensuite fait le truc avec main dev pull request push request 


# chose a faire peut être
faire un controlleur EmpController
```java
@Annotation
EmpController {
    /*avec le controller*/
    @UrlMapping("/emp/list") /*pour la méthode list uniquement et qui liste */
    méthode list(){

    }
    @UrlMapping("/emp/new")
    void create(){

    }

}
```
le web.xml on stocke cette route je crois et lorsque on tape celui ci le list il va si tel 
url est écrit alors il associer a tel controller et doit executé tel méthode dans controller associer a lui 
ex: je lance l'application testcontroller 
sur l'url il me montre http://localhost:8080/testframework/

il afficher l'url intercepté :/testframework/
il doit lister les méthodes et les classes qui possède l'annotation @Annotation
il doit maintenant lister les url de @UrlMapping(a créer avec les conditions précédentes)
    /emp/list --> EmpController --> méthode list()
    /emp/new  --> EmpController --> void create()

bien sur pour les méthodes on n'affiche que le nom des méthodes 