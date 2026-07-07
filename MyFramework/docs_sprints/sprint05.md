## Sprint 04 :
* le scan ne se fera plus sur init() mais directement a la main
* il y a question sur le contextlistener 


## Sprint 05 :
integration dans le projet de test couplé avec spring web 
je veux une page de liste qui vient d'une base de donnée 
- créer une classe d'acces au donné (repository avec @Entity , @Repository....) communiquant immédiatement avec le controller
> la partie repository je le veux en spring ,le controller avec le sprint que nous avons créer 
>


### Procédé 
* mettre dans la methode liste dans controller la liste en dure de la base de donné
* afficher cela ensuite dans une vue et savoir quel vue appelé et si elle a besoin de donné 
    * la méthode doit retourné string pour la view
    * prendre un model(notre classe) en paramêtre et on peut faire un setAttribute Map
    * setAttribute Map appelle map.put et on prend le premier élément et second élément et on prend le string pour valeur
    * model and view(avec l'attribut ; map et url (a concaténer avec suffix et préfix)) le boucler avec [string object] 
    * redirection via request.Dispatcher on appelle request.getattribute dans la view jsp  

`'le projet en réalité c'est un clone de spring mvc pour faire le devoir voilà ce qu'il fallait'`

les donnés ne sont plus en dure mais en base de donné 
si on utilise spring et ensuite il y a problème avec la couche service et repository 
il y a maintenant une question sur les singletons bean conteneur spring et de cycle de vie 