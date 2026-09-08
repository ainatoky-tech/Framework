package model;

public class UserModel {
    private String username;
    private String function;
    
    public UserModel() {
    }

    public UserModel(String username, String function) {
        this.username = username;
        this.function = function;
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
