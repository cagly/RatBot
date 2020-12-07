package bot.rat.messages;

import bot.rat.entities.UserEntity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class Commands {

    protected void adminCommandHandler(String message, @Nonnull GuildMessageReceivedEvent event, MessageHandler messageHandler) {
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
        }
    }

    protected void userCommandHandler(String message, @Nonnull GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        if (message.equals("commands")) {
            printCommands(event);
        } else if (message.equals("xd status")) {
            xdStatus(event, messageHandler);
        } else if (message.equals("cheese")) {
            cheese(event, messageHandler);
        } else if (message.length() > 6 && message.substring(0,6).equals("cheese")) {
            cheeseSomeone(event, message);
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
        } else if (message.length() > 5 && message.substring(0,5).equals("stats")) {
            pingedStats(event, messageHandler, message);
        }
//        else if (message.equals("session zero")) {
//            sessionZero(event);
//        }
    }

//    private void sessionZero(GuildMessageReceivedEvent event) {
//        event.getMessage().getChannel().sendMessage().queue();
//    }

    private void myStats(GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        String id = event.getAuthor().getId();
        UserEntity user = messageHandler.getUser(id);
        printUserStats(user, event);
    }

    private void pingedStats(GuildMessageReceivedEvent event, MessageHandler messageHandler, String message) {
        message = message.substring(9);
        String id = message.substring(0, message.length() - 1);
        UserEntity user = messageHandler.getUser(id);
        printUserStats(user, event);
    }

    private void printUserStats(UserEntity user, GuildMessageReceivedEvent event) {
        event.getMessage().getChannel()
                .sendMessage("<@"+user.getId()+">'s Stats:\n" +
                        "You have " + user.getPoints() + " points.\n" +
                        "Admin status: " + user.getAdmin() + "\n" +
                        "Muted status: " + user.getMuted()).queue();
    }

    private void legalizeXd(GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("'xd' has been legalized.").queue();
        System.out.println("'xd' has been legalized.");
        messageHandler.setXdIllegal(false);
    }

    private void prohibitXd(GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("'xd' has been prohibited.").queue();
        System.out.println("'xd' has been prohibited.");
        messageHandler.setXdIllegal(true);
    }

    private void mute(GuildMessageReceivedEvent event, String message, MessageHandler messageHandler) {
        String id = message.substring(8, message.length() - 1);
        event.getMessage().getChannel().sendMessage(id).queue();
        UserEntity user = messageHandler.getUser(id);
        if (!user.getMuted()) {
            messageHandler.muteUser(user);
            event.getMessage().getChannel().sendMessage("<@" + id + "> has been muted.").queue();
        }
    }

    private void unMute(GuildMessageReceivedEvent event, String message, MessageHandler messageHandler) {
        String id = message.substring(10, message.length() - 1);
        UserEntity user = messageHandler.getUser(id);
        event.getMessage().getChannel().sendMessage(id).queue();
        if (user.getMuted()) {
            messageHandler.unmuteUser(user);
            event.getMessage().getChannel().sendMessage("<@" + id + "> has been unmuted.").queue();
        }
    }

    private void disableCommands(GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("Commands are now disabled.").queue();
        messageHandler.setCommandsDisabled(true);
    }

    private void replyXd(GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        if (messageHandler.getReplyXd()) {
            event.getMessage().getChannel().sendMessage("We xd alone.").queue();
            messageHandler.setReplyXd(false);
        } else {
            event.getMessage().getChannel().sendMessage("We xd together.").queue();
            messageHandler.setReplyXd(true);
        }
    }

    private void printCommands(GuildMessageReceivedEvent event) {
        event.getMessage().getChannel().sendMessage("Here is a list of my commands:\n" +
                "xd status - Displays whether 'xd' is legal or not.\n" +
                "tell us a joke - I tell you a joke.\n" +
                "cheese - Activates cheese mode.\n" +
                "uncheese - Deactivates cheese mode.\n" +
                "cheese @Someone - Cheeses mentioned person.\n" +
                "code - I link you my source code.\n" +
                "reply xd - Toggle unified xd.\n" +
                "my stats - I display your stats").queue();
    }

    private void xdStatus(GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        if (messageHandler.getXdIllegal()) {
            event.getMessage().getChannel().sendMessage("'xd' is currently prohibited.").queue();
        } else {
            event.getMessage().getChannel().sendMessage("'xd' is currently legal.").queue();
        }
    }

    private void cheese(GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("Cheese activated").queue();
        messageHandler.setCheese(true);
    }

    private void cheeseSomeone(GuildMessageReceivedEvent event, String message) {
        try {
//            String id = message.substring(10, message.length() - 1);
//            Role ratRole = event.getAuthor().getJDA().getRolesByName("Cheese", false).get(0);
//            event.getMessage().getGuild().addRoleToMember(id, ratRole).complete();
//            MessageChannel cheeseChannel = event.getMessage().getGuild().getTextChannelsByName("cheese-channel", false).get(0);
//            cheeseChannel.sendMessage("<@" + id + ">").complete();
//            event.getMessage().getGuild().removeRoleFromMember(id, ratRole).complete();
            event.getMessage().delete().queue();
//            event.getMessage().getChannel().sendMessage("Cheesing someone is closed indefinitely.").queue();
        } catch (Exception e) {
            System.out.println("Cheese Someone system failed");
        }
    }

    private void unCheese(GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage("Cheese deactivated").queue();
        messageHandler.setCheese(false);
    }

    private void code(GuildMessageReceivedEvent event) {
        event.getMessage().getChannel().sendMessage("My repository is currently private!").queue();
    }

    private void tellJoke(GuildMessageReceivedEvent event, MessageHandler messageHandler) {
        event.getMessage().getChannel().sendMessage(messageHandler.getJokes().getJoke()).queue();
    }

    public boolean enableCommands(GuildMessageReceivedEvent event) {
        event.getMessage().getChannel().sendMessage("Commands are now enabled.").queue();
        return false;
    }
}
