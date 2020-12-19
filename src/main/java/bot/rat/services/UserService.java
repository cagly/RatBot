package bot.rat.services;

import bot.rat.entities.UserEntity;
import bot.rat.repositories.UserRepository;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    public void giveUserNPoints(String id, int n) {
        Optional<UserEntity> ouser = userRepository.findById(id);
        if (ouser.isPresent()) {
            UserEntity user = ouser.get();
            user.setPoints(user.getPoints() + n);
            userRepository.save(user);
        }
    }

    public void giveUserPoints(GuildMessageReceivedEvent event){
        String message = event.getMessage().getContentRaw();
        Random rand = new Random();
        rand.setSeed(message.hashCode());
        int num = rand.nextInt(201);
        num = num - 100;
        UserEntity user = userRepository.findById(event.getAuthor().getId()).get();
        user.setPoints(user.getPoints() + num);
        userRepository.save(user);
    }

    public List<UserEntity> getPointBoardFromDb(){
        List<UserEntity> userList = userRepository.findAll();
        userList.sort(Comparator.comparingInt(UserEntity::getPoints).reversed());
        return userList;
    }
}