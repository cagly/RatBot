package bot.rat.services;

import bot.rat.entities.UserEntity;
import bot.rat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
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
        return opUser.orElseGet(() -> addUserIfMissing(id));
    }

    public UserEntity addUserIfMissing(String id) {
        if (!userRepository.existsById(id)) {
            return userRepository.save(new UserEntity(id, 0));
        }
        return userRepository.findById(id).get();
    }

    public void updateUser(UserEntity user) {
        Optional<UserEntity> opUser = userRepository.findById(user.getId());
        if (opUser.isPresent()) {
            userRepository.save(user);
        }
    }
}