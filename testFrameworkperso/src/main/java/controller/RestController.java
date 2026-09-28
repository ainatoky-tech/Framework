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

    @UrlMapping (value = "/api/status", method = "get")
    @APIRest 
    public String getStatus(){
        return "{\"status\":\"success\", \"message\":\"Le framework fonctionne !\", \"annee\":2026}";
    }
    
    @UrlMapping (value = "/api/liste" , method = "get")
    @APIRest 
    public ModelView affichage(){
        ModelView mv = new ModelView("");
        List<UserModel> employes = userService.getAllUser();
        mv.addItem("employeLists", employes);
        return mv;
    }
}
