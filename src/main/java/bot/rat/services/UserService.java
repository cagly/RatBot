package bot.rat.services;

import bot.rat.entities.UserEntity;
import bot.rat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

//     Do i even need this?
//    public Boolean isUserInDatabase(String id) {
//        boolean ans = userRepository.existsById(id);
//        if (!ans) {
//            UserEntity newUser = new UserEntity(id, 0);
//            userRepository.save(newUser);
//        }
//        return ans;
//    }

    public UserEntity getUserById(String id) {
        Optional<UserEntity> opUser = userRepository.findById(id);
        if (opUser.isPresent()) {
            return opUser.get();
        } else {
            UserEntity newUser = new UserEntity(id, 0);
            userRepository.save(newUser);
            return newUser;
        }
    }
}