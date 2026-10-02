package controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import annotation.APIRest;
import annotation.Controller;
import annotation.UrlMapping;
import model.ModelView;
import model.UserModel;
import service.UserService;

@Controller 
public class RestController {

    @Autowired 
    private  UserService userService;
    
    @UrlMapping (value = "/api/liste" , method = "get")
    @APIRest 
    public ModelView affichage(){
        ModelView mv = new ModelView("");
        List<UserModel> employes = userService.getAllUser();
        mv.addItem("employeLists", employes);
        return mv;
    }

    
    @UrlMapping (value= "/api/test", method = "get")
    @APIRest 
    public ModelView affiche(){
        ModelView mv = new ModelView("");
        // Simulation des données en mémoire
        List<String> employes = new ArrayList<>();
        employes.add("Alice");
        employes.add("Bob");
        employes.add("Charlie");
        mv.addItem("liste", employes);
        return mv;
        //return "salut";
    }
}
