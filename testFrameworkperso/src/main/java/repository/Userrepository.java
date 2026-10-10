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


    public int insertByObjectBinding(UserModel user, String extension) throws Exception {
        try (Connection connect = database.getConnection()) {
            return insertByObjectBinding(connect, user, extension);
        }
    }

    public int insertByObjectBinding(Connection connection, UserModel user, String extension) throws Exception {
        connection.setAutoCommit(false);   // ✅ transaction
        try {
            // 1. INSERT USER
            String requestUser = "INSERT INTO testframework.users(username, `function`) VALUES (?, ?)";
            int userId;
            try (PreparedStatement ps = connection.prepareStatement(
                    requestUser, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getFunction());
                ps.executeUpdate();
                try (var keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        userId = keys.getInt(1);
                        user.setId(userId);   // sync de l'objet
                    } else {
                        throw new Exception("Aucun ID généré pour l'utilisateur.");
                    }
                }
            }

            // 2. INSERT EXTENSION (liée à userId)
            String requestExt = "INSERT INTO testframework.extension(iduser, poste) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(requestExt)) {
                ps.setInt(1, userId);
                ps.setString(2, extension);
                ps.executeUpdate();
            }

            connection.commit();     // ✅ tout a réussi, on valide
            return userId;
        } catch (Exception e) {
            connection.rollback();   // ✅ annule les 2 inserts
            throw e;                 // remonte l'erreur au FrontController
        } finally {
            connection.setAutoCommit(true);   // restaure l'état (bonne pratique)
        }
    }


    public int insertByParameterBinding(String username, String function, String extension) throws Exception {
        try (Connection connect = database.getConnection()) {
            return insertByParameterBinding(connect, username, function, extension);
        }
    }

    public int insertByParameterBinding(Connection connection, String username, String function,
                                        String extension) throws Exception {
        connection.setAutoCommit(false);
        try {
            // 1. INSERT USER
            String requestUser = "INSERT INTO testframework.users(username, `function`) VALUES (?, ?)";
            int userId;
            try (PreparedStatement ps = connection.prepareStatement(
                    requestUser, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, function);
                ps.executeUpdate();
                try (var keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        userId = keys.getInt(1);
                    } else {
                        throw new Exception("Aucun ID généré pour l'utilisateur.");
                    }
                }
            }

            // 2. INSERT EXTENSION
            String requestExt = "INSERT INTO testframework.extension(iduser, poste) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(requestExt)) {
                ps.setInt(1, userId);
                ps.setString(2, extension);
                ps.executeUpdate();
            }

            connection.commit();
            return userId;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }



}
