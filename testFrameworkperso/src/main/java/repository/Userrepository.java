package repository;

import model.UserModel;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import utils.Database;
import java.sql.*;

@Repository 
public class Userrepository {
    
    @Autowired 
    private Database database;

    public List<UserModel> findall(){
        try(Connection connect = database.getConnection()){
            return findall(connect);
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<UserModel> findall(Connection connection) throws SQLException {
        List<UserModel> listuser = new ArrayList<>();
        String request = "SELECT id, username, `function` FROM testframework.users";
        try(
            Statement statement = connection.createStatement();
            ResultSet resultset = statement.executeQuery(request);
        ) {
            while(resultset.next()){
                UserModel user = new UserModel(
                    resultset.getInt("id"),
                    resultset.getString("username"),
                    resultset.getString("`function`") 
                );
                listuser.add(user);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return listuser;
    }


}
