package model;

import java.util.Objects;

public class Urlkey {
    private String url; // le chemin url 
    private String method; // définit si get ou post,option,put ou patch
    public Urlkey() {}
    public Urlkey(String url, String method) {
        this.url = url;
        this.method = method;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o){ // vérification si deux variable pointent vers la même adresse mémoire et éviter le nullPointerException
        if(this == o) return true; // on le compare à lui même 
        if (o == null || getClass() != o.getClass()) return false; // vérifier si l'objet est un null ou que ce n'est pas un appel de UrlKey
        Urlkey urlkey = (Urlkey)o; //cast de l'objet en urlkey
        return  Objects.equals(url,urlkey.url) && Objects.equals(method, urlkey.method); 
    }

    @Override 
    public int hashCode() {// création d'une signature unique 
        return Objects.hash(url, method);
    }

    @Override
    public String toString() {//affichage de la méthode et de l'url entrer dans le navigateur
        return "[" + method + "] " + url;
    }
    
}
