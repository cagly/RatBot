package bot.rat.messages;

import bot.rat.entities.UserEntity;
import bot.rat.entities.Word;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class Commands {

    protected void adminCommandHandler(String message, @Nonnull MessageReceivedEvent event, MessageHandler messageHandler) {
        if (message.equals("lxd") || message.equals("legalize xd")) {
            legalizeXd(event, messageHandler);
        } else if (message.equals("pxd") || message.equals("prohibit xd")) {
            prohibitXd(event, messageHandler);
        } else if (message.length() >= 4 && message.substring(0,4).equals("mute")) {
            mute(event, message, messageHandler);
        } else if (message.length() >= 6 && message.substring(0,6).equals("unmute")) {
            unMute(event, message, messageHandler);
        } else if (message.equals("dc") || message.equals("disable commands")) {
            disableCommands(event, messageHandler);
        } else if (message.equals("test")) {
            event.getAuthor().getJDA().getTextChannelsByName("bot-test", true).get(0).sendMessage("RatBot is back online!").queue();
        } else if (message.length() > 13 && message.startsWith("give me points")) {
            if (message.length() > 15) {
                giveNPoints(event, messageHandler, message.substring(15));
            } else {
                giveNPoints(event, messageHandler, "1000");
            }
        } else if (message.equals("restart")) {
            try {
                event.getChannel().sendMessage("RatBot going to sleep...").queue();
                String[] args = new String[]{"/bin/bash", "-c", "sudo service ratbot restart"};
                Process proc = new ProcessBuilder(args).start();
            } catch (IOException e) {
                event.getChannel().sendMessage("Ratbot could not restart :(").queue();
            }
        } else if (message.length() > 12 && message.startsWith("new reminder")) {
            createReminder(event, message.substring(13), messageHandler);
        } else if (message.length() > 14 && message.startsWith("reset reminder")) {
            resetReminder(event, message.substring(15), messageHandler);
        } else if (message.length() > 15 && message.startsWith("delete reminder")) {
            deleteReminder(event, message.substring(16), messageHandler);
        } else if (message.equals("clear reminders")) {
            clearReminders(event, messageHandler);
        } else if (message.equals("test reminders")) {
            testReminders(event, messageHandler);
        } else if (message.startsWith("words")) {
            getTopWords(event, messageHandler);
        }
    }

    protected void userCommandHandler(String message, @Nonnull MessageReceivedEvent event, MessageHandler messageHandler) {
        if (message.equals("commands")) {
            printCommands(event);
        } else if (message.equals("xd status")) {
            xdStatus(event, messageHandler);
        } else if (message.equals("cheese")) {
            cheese(event, messageHandler);
        } else if (message.length() > 6 && message.startsWith("cheese")) {
            cheeseSomeone(event, messageHandler, message.substring(7));
        } else if (message.equals("uncheese")) {
            unCheese(event, messageHandler);
        }  else if (message.equals("code")) {
            code(event);
        } else if (message.equals("tell us a joke")) {
            tellJoke(event, messageHandler);
        } else if (message.equals("rxd") || message.equals("reply xd")) {
            replyXd(event, messageHandler);
        } else if (message.equals("stats")) {
            myStats(event, messageHandler);
        } else if (message.length() > 5 && message.startsWith("stats")) {
            pingedStats(event, messageHandler, message);
        } else if (message.equals("pointboard")) {
            pointBoard(event, messageHandler);
        } else if (message.length() > 8 && message.startsWith("coinflip")) {
            coinflip(event, messageHandler, message.substring(9));
        } else if (message.equals("polarize")) {
            polarize(event, messageHandler);
        } else if (message.length() > 8 && message.startsWith("connect4")) {
            messageHandler.getConnect4().connect4Commands(event, messageHandler, message.substring(9));
        } else if (message.length() > 2 && message.startsWith("c4")) {
            messageHandler.getConnect4().connect4Commands(event, messageHandler, message.substring(3));
        } else if (message.length() > 4 && message.startsWith("kill")) {
            kill(event, messageHandler);
        } else if (message.equals("time")) {
            tellTime(event);
        }
    }

    public void getTopWords(MessageReceivedEvent event, MessageHandler messageHandler) {
        List<Member> mentioned = event.getMessage().getMentions().getMembers();
        if (mentioned.size() == 1) {
            String userId = mentioned.get(0).getId();
            List<Word> wordList = messageHandler.getWordService().getTopWords(userId);
            StringBuilder msg = new StringBuilder("The top words used by " + mentioned.get(0).getEffectiveName() + " are: \n");
            for (Word word : wordList) {
                msg.append(word.id.word).append(": ").append(word.count).append(" uses \n");
            }
            event.getChannel().sendMessage(msg.toString()).queue();
        }
    }

    public void tellTime(MessageReceivedEvent event) {
        LocalDateTime time = LocalDateTime.now();
        event.getChannel().sendMessage("It's currently " + time.getHour() + ":" + time.getMinute() + " in Ratland.").queue();
    }

    public void testReminders(MessageReceivedEvent event, MessageHandler handler) {
        handler.getReminderService().test(event.getChannel());
    }

    public void clearReminders(MessageReceivedEvent event, MessageHandler handler) {
        handler.getReminderService().clearReminders();
        event.getChannel().sendMessage("Reminders cleared").queue();
    }

    public void deleteReminder(MessageReceivedEvent event, String message, MessageHandler handler) {
        if (handler.getReminderService().deleteReminder(message)) {
            event.getChannel().sendMessage("Reminder deleted!").queue();
        } else {
            event.getChannel().sendMessage("No such reminder exists").queue();
        }
    }

    public void resetReminder(MessageReceivedEvent event, String message, MessageHandler handler) {
        if (handler.getReminderService().resetReminder(message)) {
            event.getChannel().sendMessage("Reminder reset!").queue();
        } else {
            event.getChannel().sendMessage("No such reminder exists").queue();
        }
    }

    public void createReminder(MessageReceivedEvent event, String message, MessageHandler handler) {
        try {
            String[] parts = message.split(" ");
            String id = parts[0];
            Integer daysSinceDnd = Integer.parseInt(parts[1]);
            Integer remindEveryXDays = Integer.parseInt(parts[2]);
            handler.getReminderService().createReminder(id, daysSinceDnd, remindEveryXDays);
            event.getChannel().sendMessage("New reminder created").queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Invalid reminder creation").queue();
        }
    }

    public void kill(MessageReceivedEvent event, MessageHandler handler) {
//        TODO: Fix this shit, or not...
//        try {
//            makeAvatar(event);
//            System.out.println(event.getJDA().getSelfUser().getAvatarUrl());
//            Member victim = event.getMessage().getMentionedMembers().get(0);
//            File playerAvatarFile = new File("/home/ubuntu/RatPics/" + victim.getId() + ".png");
//            Emote emote1 = event.getGuild().getEmotesByName("ratbot", true).get(0);
//            File playerAvatarFile = new File("C:\\Users\\Hans\\IdeaProjects\\ratbot\\resources\\" + victim.getId() + ".png");
//            Icon icon = Icon.from(playerAvatarFile);
//            Emote emote2 = event.getGuild().createEmote(victim.getId(), icon).complete();
//            String emoji1 = "<:" + emote1.getName() + ":" + emote1.getId() + ">";
//            String emoji2 = "<:" + emote2.getName() + ":" + emote2.getId() + ">";
//            event.getChannel().sendMessage(emoji1 + " :knife: " + emoji2).complete();
//            event.getChannel().sendMessage(":skull_crossbones: " + victim.getEffectiveName() + " is dead :skull_crossbones:").complete();
//            emote2.delete().complete();
//        } catch (IOException e) {
//            event.getChannel().sendMessage("eror").queue();
//        }
    }

    public static void makeAvatar(MessageReceivedEvent event) throws IOException {
//        File file = new File("C:\\Users\\Hans\\IdeaProjects\\ratbot\\resources\\" + event.getMessage().getMentionedMembers().get(0).getId() + ".png");
        File file = new File("/home/ubuntu/RatPics/" + event.getMessage().getMentions().getUsers().get(0).getId() + ".png");
        if (!file.exists() && event.getMessage().getMentions().getUsers().get(0).getAvatarUrl() != null) {
            URL url = new URL(event.getMessage().getMentions().getUsers().get(0).getAvatarUrl());
            URLConnection uc = url.openConnection();
            uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            BufferedImage img = ImageIO.read(uc.getInputStream());
            ImageIO.write(img, "png", file);
        }
    }

    public void polarize(MessageReceivedEvent event, MessageHandler handler) {
        String id = event.getAuthor().getId();
        Random rand = new Random();
        if (handler.getUserService().userHasNPoints(id, 1000)) {
            List<UserEntity> users = handler.getUserService().getAll();
            int choice = rand.nextInt(users.size());
            UserEntity randomUser = users.get(choice);
            while (event.getGuild().getMemberById(randomUser.getId()) == null) {
                choice = rand.nextInt(users.size());
                randomUser = users.get(choice);
            }
            if (randomUser.getPoints() != null) {
                randomUser.setPoints(-1 * randomUser.getPoints());
            }
            handler.getUserService().updateUser(randomUser);
            if (randomUser.getId().equals(id)) {
                handler.getUserService().giveUserNPoints(id, 1000);
            } else {
                handler.getUserService().giveUserNPoints(id, -1000);
            }
            event.getChannel().sendMessage("Your victim was " + event.getGuild().getMemberById(randomUser.getId()).getEffectiveName() + ".").queue();

        } else {
            event.getChannel().sendMessage("You're too poor, man.").queue();
        }
    }

    public void coinflip(MessageReceivedEvent event, MessageHandler handler, String message) {
        try {
            String coinSide = message.substring(0, 5);
            String id = event.getAuthor().getId();
            if (message.substring(6).length() > 15 ||
                    Long.parseLong(message.substring(6)) > 2147483647) {
                event.getChannel().sendMessage("Sorry, Senpai, I can only handle integers. UwU").queue();
                return;
            }
            int n = Integer.parseInt(message.substring(6));
            boolean bool = handler.getUserService().gamblePointsCoinflip(event, id, coinSide, n);
            if (!bool) {
                event.getChannel().sendMessage("Dude, you're so fucking dumb.").queue();
            }
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("Try entering an integer, dumbass.").queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Congratulations, you're stupid.").queue();
        }
    }

    private void giveNPoints(MessageReceivedEvent event, MessageHandler handler, String message) {
        try {
            Integer n = Integer.parseInt(message);
            handler.getUserService().giveUserNPoints(event.getAuthor().getId(), n);
            event.getMessage().getChannel().sendMessage("You got it, boss!").queue();
        } catch (Exception e) {
            event.getMessage().getChannel().sendMessage("Dude you fucking suck.").queue();
        }
    }
    private void pointBoard(MessageReceivedEvent event, MessageHandler messageHandler) {
        List<UserEntity> topList = messageHandler.getUserService().getPointBoardFromDb();
        String board = "Point Hiscores:\n";
        System.out.println(topList.toString());
        for (UserEntity user : topList) {
            Member member = event.getGuild().getMemberById(user.getId());
            if (member != null && !member.getUser().isBot()){
                board += member.getEffectiveName() + ", " + user.getPoints() + " points.\n";
            }
        }
        event.getMessage().getChannel().sendMessage(board).queue();
    }

//    private void sessionZero(MessageReceivedEvent event) {
//        event.getMessage().getChannel().sendMessage().queue();
//    }

    private void myStats(MessageReceivedEvent event, MessageHandler messageHandler) {
        String id = event.getAuthor().getId();
        UserEntity user = messageHandler.getUser(id);
        printUserStats(user, event);
    }

    private void pingedStats(MessageReceivedEvent event, MessageHandler messageHandler, String message) {
        message = message.substring(9);
        String id = message.substring(0, message.length() - 1);
        UserEntity user = messageHandler.getUser(id);
        printUserStats(user, event);
    }

    private void printUserStats(UserEntity user, MessageReceivedEvent event) {
        String adminMessage;
        String muteMessage;
        if(user.getAdmin()) {
            adminMessage = "You are an admin and ";
        } else {
            adminMessage = "You are not an admin and ";
        }
        if (user.getMuted()) {
            muteMessage = "you are muted.";
        } else {
            muteMessage = "you are not muted.";
        }

        event.getMessage().getChannel()
                .sendMessage("<@"+user.getId()+">'s Stats:\n" +
                        "You have " + user.getPoints() + " points.\n" +
                        adminMessage + muteMessage).queue();
    }

    private void legalizeXd(MessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("'xd' has been legalized.").queue();
        System.out.println("'xd' has been legalized.");
        messageHandler.getSettingsService().saveSettingBoolean("xdIllegal", false);
        messageHandler.setXdIllegal(false);
    }

    private void prohibitXd(MessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("'xd' has been prohibited.").queue();
        System.out.println("'xd' has been prohibited.");
        messageHandler.getSettingsService().saveSettingBoolean("xdIllegal", true);
        messageHandler.setXdIllegal(true);
    }

    private void mute(MessageReceivedEvent event, String message, MessageHandler messageHandler) {
        String id = message.substring(8, message.length() - 1);
        UserEntity user = messageHandler.getUser(id);
        if (!user.getMuted()) {
            messageHandler.muteUser(user);
            event.getMessage().getChannel().sendMessage("<@" + id + "> has been muted.").queue();
        }
    }

    private void unMute(MessageReceivedEvent event, String message, MessageHandler messageHandler) {
        String id = message.substring(10, message.length() - 1);
        UserEntity user = messageHandler.getUser(id);
        if (user.getMuted()) {
            messageHandler.unmuteUser(user);
            event.getMessage().getChannel().sendMessage("<@" + id + "> has been unmuted.").queue();
        }
    }

    private void disableCommands(MessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("Commands are now disabled.").queue();
        messageHandler.getSettingsService().saveSettingBoolean("commandsDisabled", true);
        messageHandler.setCommandsDisabled(true);
    }

    private void replyXd(MessageReceivedEvent event, MessageHandler messageHandler) {
        if (messageHandler.getReplyXd()) {
            event.getMessage().getChannel().sendMessage("We xd alone.").queue();
            messageHandler.getSettingsService().saveSettingBoolean("replyXd", false);
            messageHandler.setReplyXd(false);
        } else {
            event.getMessage().getChannel().sendMessage("We xd together.").queue();
            messageHandler.getSettingsService().saveSettingBoolean("replyXd", true);
            messageHandler.setReplyXd(true);
        }
    }

    private void printCommands(MessageReceivedEvent event) {
        System.out.println(event.getMessage().getChannel().getName());
        event.getMessage().getChannel().sendMessage("Here is a list of my commands:\n" +
                "xd status - Displays whether 'xd' is legal or not.\n" +
                "tell us a joke - I tell you a joke.\n" +
                "cheese - Activates cheese mode.\n" +
                "uncheese - Deactivates cheese mode.\n" +
                "cheese @Someone - Cheeses mentioned person.\n" +
                "code - I link you my source code.\n" +
                "reply xd - Toggle unified xd.\n" +
                "stats - I display your stats.\n" +
                "stats @Someone - Display someone's stats.\n" +
                "pointboard - Show point leaderboards.\n" +
                "coinflip [heads/tails] [number] - Gamble away your hard-earned points.\n" +
                "polarize - Costs 1000 points. Random person's points are polarized.\n" +
                "c4 tutorial - Learn how to play connect4 against RatBot (Only works in rat-games channel)\n" +
                "kill @Someone - I will kill someone for you, but you will owe him.\n" +
                "time - I will tell you what time it is in Ratland").queue();
    }

    private void xdStatus(MessageReceivedEvent event, MessageHandler messageHandler) {
        if (messageHandler.getXdIllegal()) {
            event.getMessage().getChannel().sendMessage("'xd' is currently prohibited.").queue();
        } else {
            event.getMessage().getChannel().sendMessage("'xd' is currently legal.").queue();
        }
    }

    private void cheese(MessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("Cheese activated").queue();
        messageHandler.getSettingsService().saveSettingBoolean("cheese", true);
        messageHandler.setCheese(true);
    }

    private void cheeseSomeone(MessageReceivedEvent event, MessageHandler messageHandler, String message) {
        try {
//            message = message.substring(3, message.length() - 1);
            User cheesee = event.getMessage().getMentions().getUsers().get(0);
            PrivateChannel chan = cheesee.openPrivateChannel().complete(true);
            String cheeseUrl = messageHandler.getCheesePics().getJoke();
            chan.sendMessage(cheeseUrl).queue();
            event.getMessage().delete().queue();
        } catch (Exception e) {
            System.out.println("Cheese Someone system failed");
        }
    }

    private void unCheese(MessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("Cheese deactivated").queue();
        messageHandler.getSettingsService().saveSettingBoolean("cheese", false);
        messageHandler.setCheese(false);
    }

    private void code(MessageReceivedEvent event) {
        event.getMessage().getChannel().sendMessage("My repository is currently private!").queue();
    }

    private void tellJoke(MessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage(messageHandler.getJokes().getJoke()).queue();
    }

    public boolean enableCommands(MessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("Commands are now enabled.").queue();
        messageHandler.getSettingsService().saveSettingBoolean("commandsDisabled", false);
        return false;
    }
}
