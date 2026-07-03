package src.framework.model;

import java.util.Objects;

public class Urlkey {
    private String url;
    private String method;
    public Urlkey() {}
    public Urlkey(String url, String method) {
        this.url = url;
        this.method = method;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Urlkey urlkey = (Urlkey)o;
        return  Objects.equals(url,urlkey.url) && Objects.equals(method, urlkey.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override
    public String toString() {
        return "[" + method + "] " + url;
    }
    
}
