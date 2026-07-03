package src.main.java.controller;

import src.framework.annotation.Controller;
import src.framework.annotation.UrlMapping;

@Controller
public class TestController {
    @UrlMapping(value = "/testcontrolle/list", method = "get")
    public String listage(){
        return ("-> [CONSOLE] J'affiche la liste des employés (GET) de la méthode listage()");
    }
}
