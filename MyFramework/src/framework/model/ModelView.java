package src.framework.model;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String url;
    private Map<String, Object> data = new HashMap<>();

    public ModelView(String url) { this.url = url; }
    
    public void addItem(String key, Object value) { this.data.put(key, value); }
    public String getUrl() { return url; }
    public Map<String, Object> getData() { return data; }
}