package service;

import java.util.List;

import org.springframework.stereotype.Service;

import model.UserModel;
import repository.ExtensionRepository;
import repository.Userrepository;

@Service 
public class UserService {
    private Userrepository userRepo;
    private ExtensionRepository extensionRepo;

    public UserService(Userrepository userRepo, ExtensionRepository extensionRepo) {
        this.userRepo = userRepo;
        this.extensionRepo = extensionRepo;
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


     /**
     * Méthode de Login : 
     * 1. Cherche l'utilisateur par son nom.
     * 2. Si trouvé, récupère son poste (rôle) via son ID.
     * 3. Compare le poste en base avec le poste entré dans le formulaire.
     */
    public UserModel login(String username, String posteSaisi) throws Exception {
        // 1. Trouver l'utilisateur
        UserModel user = userRepo.findByName(username);
        
        if (user == null) {
            System.out.println("[LOGIN FAILED] Utilisateur introuvable : " + username);
            return null; 
        }

        // 2. Extraire son poste depuis la table extension
        String posteEnBase = extensionRepo.findPosteByUserId(user.getId());

        // 3. Comparer
        if (posteEnBase != null && posteEnBase.equalsIgnoreCase(posteSaisi)) {
            // On met à jour l'objet user avec le rôle exact de la base
            user.setFunction(posteEnBase); 
            System.out.println("[LOGIN SUCCESS] " + username + " connecté avec le rôle : " + posteEnBase);
            return user; // Authentification réussie
        }

        System.out.println("[LOGIN FAILED] Rôle incorrect pour " + username);
        return null; // Le rôle saisi ne correspond pas à celui de la base
    }
}
