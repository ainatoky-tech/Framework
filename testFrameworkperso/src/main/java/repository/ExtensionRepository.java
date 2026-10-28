package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import utils.Database;

@Repository 
public class ExtensionRepository {
    @Autowired 
    private Database database;

    public String findPosteByUserId(int idUser) throws Exception {
        try (Connection connect = database.getConnection()) {
            return findPosteByUserId(connect, idUser);
        }
    }

    public String findPosteByUserId(Connection connection, int idUser) throws Exception {
        String request = "SELECT poste FROM testframework.extension WHERE iduser = ?";
        try (PreparedStatement ps = connection.prepareStatement(request)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("poste"); // On retourne le rôle (ex: "chief")
                }
            }
        }
        return null; // Aucun poste trouvé pour cet utilisateur
    }

}
