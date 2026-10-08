package controller;

import annotation.Controller;
import annotation.UrlMapping;
import model.ModelView;

@Controller 
public class InsertController {
    @UrlMapping (value = "/emp/save" ,method = "GET")
    public ModelView afficherFormulaire(){
        ModelView mv = new ModelView();
        mv.addItem(("form-objet"), mv);
        return mv;
    }

    @UrlMapping (value = "/emp/save", method = "POST")
}
