même url mais et methode http différente requete http différente
assurer l'unicité de l'url on ne peut pas avoir /list et doit savoir si c'est méthode1 ou méthode2 qu'il faut appeller il faut qu'il soit a long terme la solution 
utilisé map pour dire que l'url n'a pas d'être doublé donc on ne peut pas prendre le même url du fait qu'il existe déjà


# voici le sprint 3: suite de sprint 2
3.1 et 3.2(le nouveau indépendant je suppose de sprint 2)

**Contexte :**
la problematique c'est que nous avons testcontroller.java(par exemple) et un url /test de la méthode m1 on peut pas actuellement faire /test pour m2 car dans la mapping on  a 
map - url -instance de classe et de méthode 
quelle modification peut on apporté pour savoir que le même url soit attribué pour deux requete http différente genre 
/test pour post et /test pour get 

alors comment faire pour effectuer cet opération ? 
- creer une classe qui sert de clé a la map(url,methode(getpost))

créer une classe UrlMethode et l'instancier
Urlméthode u1 = new UrlMethode();
u1.setUrl("/test")
u1.setMethode("get")

Urlméthode u2 = new UrlMethode();
u2.setUrl("/test")
u2.setMethode("post")

méthode equals doit être surdéfinis et utilisé un equals si on utilise encore u3.setUrl('test') donc il doit throws exception sur le fait que /test est déja utilisé 
créer une classe urlmethode
 et map avec urlméthode et le tester avec l'instance
**3.1**
**3.2**
appeler la méthode urlméthode par reflect et la méthode doit avoir un sysout et vérifier dans la console qu'on l'a appellé

# TEST POUR SPRINT 03
dans la classe du dev faire deux classe ayant le même méthode et les mêmes url en get et on doit voir une exception qui indique que map doit implémenter equals et hasquote(ou hascode) je crois pour voir s'il y a une clé dans tel endroit cela montre si ils sont égaux lorsqu'ils ont le même méthode et le même url

pourquoi mettre hascode(ou hasquote) je vais voir 
maintenant il y a une question de context key = true et qui fait quoi 