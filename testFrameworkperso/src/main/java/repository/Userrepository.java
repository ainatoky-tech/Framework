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
                    resultset.getString("function") 
                );
                listuser.add(user);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return listuser;
    }


    public int insertByObjectBinding(UserModel user) throws Exception{
        try(Connection connect = database.getConnection()) {
            return insertByObjectBinding(connect, user);
        }
    }
    public int insertByObjectBinding(Connection connection,UserModel user) throws Exception{
        int lastuser= -1;
        String request = "INSERT INTO testframework.users(username,`function`) VALUES (?,?)";
        try(
            PreparedStatement ps = connection.prepareStatement(request,java.sql.Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFunction());
            ps.executeUpdate();
            try(var resultset = ps.getGeneratedKeys()){
                if(resultset.next()){
                    lastuser = resultset.getInt(1);
                    return lastuser;
                } else {
                    return lastuser;
                }
            }
        } 
    }


    public int insertByParameterBinding(String username, String function) throws Exception{
        try(Connection connect = database.getConnection()) {
            return insertByParameterBinding(connect, username, function);
        }
    }
    public int insertByParameterBinding(Connection connection , String username, String function) throws Exception{
        int lastuser = -1;
        String request = "INSERT INTO testframework.users(username,`function`) VALUES (?,?)";
        try(
            PreparedStatement ps = connection.prepareStatement(request,java.sql.Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setString(1, username);
            ps.setString(2, function);
            ps.executeUpdate();
            try(var resultset = ps.getGeneratedKeys()){
                if(resultset.next()){
                    lastuser = resultset.getInt(1);
                    return lastuser;
                } else {
                    return lastuser;
                }
            }
        } 

    }


}
