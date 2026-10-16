package service;

import org.springframework.stereotype.Service;

import model.Mapfile;
import repository.ImportRepository;

@Service 
public class ImportService {
    private ImportRepository importRepo;

    
    public ImportService(ImportRepository importRepo) {
        this.importRepo = importRepo;
    }


    public int insertfiles(Mapfile file) throws Exception{
        return importRepo.insertfile(file);
    }
}
