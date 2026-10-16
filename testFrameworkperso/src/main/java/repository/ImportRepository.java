package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import model.Mapfile;
import utils.Database;

@Repository 
public class ImportRepository {
    @Autowired 
    private Database database;

    public int insertfile(Mapfile file) throws Exception{
        try(Connection connect = database.getConnection()) {
            return insertfile(connect, file);
        }
    }
    public int insertfile(Connection connexion , Mapfile file) throws Exception{
        connexion.setAutoCommit(false);
        try {
            String request = "INSERT INTO testframework.files(filename,extension,content) VALUES (? , ? , ?)";
            int fileLastInsertID = -1;
            try(PreparedStatement ps = connexion.prepareStatement(request, java.sql.Statement.RETURN_GENERATED_KEYS))
            {
                ps.setString(1, file.getFilename());
                ps.setString(2, file.getExtension());
                ps.setBytes(3, file.getBytes());
                ps.executeUpdate();
                try(var keys = ps.getGeneratedKeys()){
                    if(keys.next()){
                        fileLastInsertID = keys.getInt(1);
                    }
                }
            }
            connexion.commit();
            return fileLastInsertID;
        } catch (Exception e) {
            connexion.rollback();
            throw e;
        }
    } 


    /*public Mapfile findById(int id) throws Exception {
        String sql = "SELECT filename, extension, content FROM testframework.files WHERE id = ?";
        try (Connection conn = database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Mapfile f = new Mapfile();
                    f.setFilename(rs.getString("filename"));
                    f.setExtension(rs.getString("extension"));
                    f.setBytes(rs.getBytes("content"));   // ← récupère les bytes
                    return f;
                }
            }
        }
        return null;
    }*/
}
