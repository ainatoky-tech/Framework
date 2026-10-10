package model;

public class ExtentionModel {
    private int id;
    private int idUser;      // ✅ AJOUT
    private String extension;

    public ExtentionModel() {}

    public ExtentionModel(int id, int idUser, String extension) {
        this.id = id;
        this.idUser = idUser;
        this.extension = extension;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUser() { return idUser; }              // ✅ AJOUT
    public void setIdUser(int idUser) { this.idUser = idUser; }  // ✅ AJOUT

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
}