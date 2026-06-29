package src.main.java.controller;

// On importe l'annotation qui est cachée dans ton fichier .jar
import src.framework.annotation.Controller;
import src.framework.annotation.UrlMapping;
import java.util.List;

@Controller
public class EmpController {

    
    private String messageDeBienvenue = "Bonjour de mon Framework !";

    public void afficherConsole() {
        System.out.println("La méthode annotée fonctionne !");
    }

    public void methodeStandard() {
        // Cette méthode n'a pas d'annotation, elle ne doit pas être affichée
    }

    @UrlMapping("/emp/create")
    public void create(){

    }
    @UrlMapping("/emp/list")
    public void lister(){

    }
}