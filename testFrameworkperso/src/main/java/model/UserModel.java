package model;

public class UserModel {
    private int id;
    private String username;
    private String function;
    
    public UserModel() {
    }

    public UserModel(int id, String username, String function) {
        this.id = id;   
        this.username = username;
        this.function = function;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
    
    
}
