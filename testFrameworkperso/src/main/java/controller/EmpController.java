package src.main.java.controller;

// On importe l'annotation qui est cachée dans ton fichier .jar
import src.framework.annotation.Controller;
import src.framework.annotation.UrlMapping;
import java.util.List;

@Controller
public class EmpController {

    
    // Simule une requête GET /emp/list
    @UrlMapping(value = "/emp/list", method = "GET") // Si ton annotation supporte l'attribut method
    public void list() {
        System.out.println("-> [CONSOLE] J'affiche la liste des employés (GET)");
    }

    // Simule une requête POST /emp/list (par exemple pour soumettre un formulaire)
    @UrlMapping(value = "/emp/list", method = "POST")
    public void create() {
        System.out.println("-> [CONSOLE] J'ajoute un employé (POST)");
    }
}