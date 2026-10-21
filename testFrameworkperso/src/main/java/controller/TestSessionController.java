package controller;

import annotation.Controller;
import annotation.UrlMapping;
import model.ModelView;
import model.MySession;

@Controller 
public class TestSessionController {
    @UrlMapping (value = "/testSession", method = "get")
    public ModelView testSession(MySession session) {
        Integer count = (Integer) session.getVariable("visites");
        if (count == null) count = 0;
        session.addVariable("visites", count + 1);
        ModelView mv = new ModelView("index");
        mv.addItem("message", "Visite n°" + (count + 1));
        return mv;
    }
}
