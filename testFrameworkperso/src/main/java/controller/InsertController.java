package controller;

import org.springframework.beans.factory.annotation.Autowired;

import annotation.Controller;
import annotation.UrlMapping;
import model.ModelView;
import model.UserModel;
import service.UserService;

@Controller 
public class InsertController {
    @Autowired
    private UserService userService;

    /**
     * GET /emp/save → affiche le formulaire
     */
    @UrlMapping(value = "/emp/save", method = "GET")
    public ModelView afficherFormulaire() {
        ModelView mv = new ModelView("form-objet");   
        return mv;
    }

    /**
     * POST /emp/save → traite la soumission (binding par paramètres)
     */
    @UrlMapping(value = "/emp/save", method = "POST")
    public ModelView save(String username, String function, String extension) throws Exception {
        // Appel au service qui appelle le repository
        int newId = userService.insertByParameterBinding(username, function, extension);
        
        ModelView mv = new ModelView("redirect:/");   // page de résultat
        mv.addItem("message", "Utilisateur inséré avec ID = " + newId);
        return mv;
    }

    @UrlMapping(value = "/emp/save-objet", method = "POST")
    public ModelView saveByObject(UserModel user, String extension) throws Exception {
        int newId = userService.insertByObjectBinding(user, extension);
        ModelView mv = new ModelView("redirect:/");
        mv.addItem("message", "Utilisateur inséré par objet avec ID = " + newId);
        return mv;
    }
}
