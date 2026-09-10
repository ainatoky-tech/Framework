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
}
