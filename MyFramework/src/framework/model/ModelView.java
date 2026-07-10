package src.framework.model;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String url; // Le nom de la vue (ex: "liste-employes")
    private final Map<String, Object> data = new HashMap<>(); // Le conteneur de données

    public ModelView() {}
    public ModelView(String url) { this.url = url; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Map<String, Object> getData() { return data; }

    // Équivalent du setAttribute ou addObject de Spring MVC
    public void addItem(String key, Object value) {
        this.data.put(key, value);
    }

}
