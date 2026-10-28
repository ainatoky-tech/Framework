package controller;

import org.springframework.beans.factory.annotation.Autowired;

import annotation.Auth;
import annotation.Controller;
import annotation.UrlMapping;
import model.Mapfile;
import model.ModelView;
import service.ImportService;

@Controller
public class TestController {

    @Autowired 
    ImportService importService;

    @Auth 
    @UrlMapping(value = "/testcontrolle/list", method = "get")
    public String listage(){
        return ("-> [CONSOLE] J'affiche la liste des employés (GET) de la méthode listage()");
    }

    @Auth (role = "chief")
    @UrlMapping (value = "/testcontrolle/upload", method = "GET")
    public ModelView afficherFormulaire(){
        ModelView mv = new ModelView("form-upload");
        return mv;
    }

    @Auth (role = "chief")
    @UrlMapping (value = "/testcontrolle/upload", method = "POST")
    public ModelView submitupload(Mapfile file) throws Exception{
        int LastFileIDInserted = importService.insertfiles(file);
        ModelView mv = new ModelView("redirect:/");   // page de résultat
        mv.addItem("message", "fichier inséré avec ID = " + LastFileIDInserted);
        return mv;
    }
}
