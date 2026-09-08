package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Database {

    private String url;
    private String username;
    private String password;

    public Database() {
        this.url = System.getenv("DB_URL");
        this.username = System.getenv("DB_USER");
        this.password = System.getenv("DB_PASSWORD");
        if(this.url == null){
            url = "jdbc:mysql://mysql:3306/testframework?useSSL=false&allowPublicKeyRetrieval=true";
        }
        if(this.username == null){
            username = "root";
        }
        if(this.password == null){
            password = "";
        }
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
