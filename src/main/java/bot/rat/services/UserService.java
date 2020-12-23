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

    public boolean userHasNPoints(String id, Integer n) {
        UserEntity user = userRepository.findById(id).get();
        return user.getPoints() >= n;
    }

    public boolean gamblePointsCoinflip(GuildMessageReceivedEvent event, String id, String coinSide, Integer n) {
        // Heads = 0, Tails = 1
        if (n < 1) {
            return false;
        }
        if (!userHasNPoints(id, n)) {
            return false;
        }
        Random rand = new Random();
        int res = rand.nextInt(2);
        String coinflipResult = res == 1 ? "Tails" : "Heads";
        if (coinSide.toLowerCase().equals("heads")) {
            event.getMessage().getChannel().sendMessage("You picked " + coinSide + " and gambled " + n + " points.\n" +
                    "Coinflip result: " + coinflipResult).queue();
            if (res == 0) {
                // Win
                event.getMessage().getChannel().sendMessage("You've won " + n + " points! Wow so amazing!").queue();
                giveUserNPoints(id, n);
                return true;
            } else {
                event.getMessage().getChannel().sendMessage("Damn, you lost. That's pretty embarrassing.").queue();
                giveUserNPoints(id, -n);
                return true;
            }
        } else if (coinSide.toLowerCase().equals("tails")) {
            event.getMessage().getChannel().sendMessage("You picked " + coinSide + " and gambled " + n + " points.\n" +
                    "Coinflip result: " + coinflipResult).queue();
            if (res == 1) {
                // Win
                event.getMessage().getChannel().sendMessage("Congratulations, you won! You are now " + n + " points richer.").queue();
                giveUserNPoints(id, n);
                return true;
            } else {
                event.getMessage().getChannel().sendMessage("Wow, you lost? I almost feel bad.").queue();
                giveUserNPoints(id, -n);
                return true;
            }
        }
        return false;
    }
}