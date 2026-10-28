package controller;

import org.springframework.beans.factory.annotation.Autowired;

import annotation.Controller;
import annotation.UrlMapping;
import jakarta.servlet.http.HttpServletRequest;
import model.ModelView;
import model.UserModel;
import service.UserService;

@Controller 
public class AuthController {
    @Autowired 
    private UserService userService;

    @UrlMapping(value = "/login", method = "GET")
    public ModelView afficherFormulaire() {
        return new ModelView("login"); // Ta JSP avec les champs username et poste
    }

    @UrlMapping(value = "/login", method = "POST")
    public ModelView traiterLogin(String username, String poste, HttpServletRequest request) {
        try {
            // Appel de la méthode du service
            UserModel userConnecte = userService.login(username, poste);

            if (userConnecte != null) {
                // SUCCÈS : On met en session avec la clé "user" (celle configurée dans ton web.xml)
                request.getSession().setAttribute("user", userConnecte);
                return new ModelView("redirect:/"); // Redirection vers l'accueil
            } else {
                // ÉCHEC : On renvoie au formulaire avec un message d'erreur
                ModelView mv = new ModelView("login");
                mv.addItem("error", "Nom d'utilisateur ou rôle incorrect.");
                return mv;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ModelView mv = new ModelView("login");
            mv.addItem("error", "Erreur technique : " + e.getMessage());
            return mv;
        }
    }

    // Déconnexion
    @UrlMapping(value = "/logout", method = "GET")
    public ModelView logout(HttpServletRequest request) {
        // Détruit la session (supprime l'utilisateur)
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
        return new ModelView("redirect:/login");
    }


}
