package model;

import jakarta.servlet.http.HttpSession;

public class MySesssion {
    private final HttpSession httpsession;

    public MySesssion(HttpSession httpsession) {
        this.httpsession = httpsession;
    }
    public Object getVariable(String name){
        return httpsession.getAttribute(name);
    }
    public void addVariable(String name,Object value){
        httpsession.setAttribute(name, value);
    }
    public void removeVariable(String name){
        httpsession.removeAttribute(name);
    }
    public void invalidate(){
        httpsession.invalidate();
    }
}
