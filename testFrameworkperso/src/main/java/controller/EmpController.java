package controller;

// On importe l'annotation qui est cachée dans ton fichier .jar
import annotation.Controller;
import annotation.UrlMapping;
import model.ModelView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EmpController {

    
    /*Simule une requête GET /emp/list
    @UrlMapping(value = "/emp/list", method = "GET") // Si ton annotation supporte l'attribut method
    public void list() {
        System.out.println("-> [CONSOLE] J'affiche la liste des employés (GET)");
    }

    // Simule une requête POST /emp/list (par exemple pour soumettre un formulaire)
    @UrlMapping(value = "/emp/list/create", method = "GET")
    public void create() {
        System.out.println("-> [CONSOLE] J'ajoute un employé (GET)");
    }

    // ici les tests 

    @UrlMapping(value = "/emp/list/old", method = "GET") 
    public String listAncienne() {
        return ("méthode appellé listancienne()");
    }*/

    @UrlMapping(value = "/emp/list", method = "GET")
    public ModelView list() {
        ModelView mv = new ModelView("liste-employes");

        // Simulation des données en mémoire
        List<String> employes = new ArrayList<>();
        employes.add("Rakoto (Dev Java)");
        employes.add("Rabe (Docker Expert)");
        employes.add("Rasoa (Architecte)");

        // Injection dans le ModelView sous la clé "liste"
        mv.addItem("liste", employes);

        return mv;
    }

    @UrlMapping(value = "/test", method = "GET")
    public ModelView test(){
        ModelView mv = new ModelView("index");
        return mv;
    }

    @UrlMapping(value = "test/controller/list", method = "GET")
    public ModelView liste(){
        ModelView mv = new ModelView("liste-employes");
        return mv;
    }
}