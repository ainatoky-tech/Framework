package model;

public class Mapfile {
    private String filename;
    private String extension;
    private byte[] bytes;

    public Mapfile(){}
    
    public Mapfile(String filename, String extension, byte[] bytes) {
        this.filename = filename;
        this.extension = extension;
        this.bytes = bytes;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getExtension() {
        return extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }
    public byte[] getBytes() {
        return bytes;
    }
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
    
}
