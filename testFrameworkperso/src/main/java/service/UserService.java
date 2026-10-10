package service;

import java.util.List;

import org.springframework.stereotype.Service;

import model.UserModel;
import repository.Userrepository;

@Service 
public class UserService {
    private Userrepository userRepo;

    public UserService(Userrepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<UserModel> getAllUser(){
            return userRepo.findall();
    }

    public int insertByObjectBinding(UserModel user, String extension) throws Exception {
        return userRepo.insertByObjectBinding(user, extension);
    }
    
    public int insertByParameterBinding(String username, String function, String extension) throws Exception {
        return userRepo.insertByParameterBinding(username, function, extension);
    }
}
