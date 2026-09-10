package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database {

    private String url;
    private String username;
    private String password;

    public Database() {
    }

    

    public void setUrl(String url) {
        this.url = url;
    }



    public void setUsername(String username) {
        this.username = username;
    }



    public void setPassword(String password) {
        this.password = password;
    }



    public Connection getConnection() throws SQLException,ClassNotFoundException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, username, password);
    }    
}
