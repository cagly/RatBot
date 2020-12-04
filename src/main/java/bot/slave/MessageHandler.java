package bot.slave;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MessageHandler {

    List<String> firstHalfUni = Arrays.asList("U+0078", "U+0058", "U+0425","U+0445","U+04FC", "U+04FD", "U+04FE", "U+04FF");
//    List<String> firstHalf = Arrays.asList("x", "X", "Х","х","Ӽ", "ӽ", "Ӿ", "ӿ");
//    List<String> secondHalf = Arrays.asList("Ð", "þ", "Ď", "ď", "Đ", "đ", "Ɖ","Ɗ"
//          ,"Ƣ", "ǳ", "ǲ", "Ǳ", "Ƿ", "Ɋ","ɋ","q", "Ḋ","ḋ", "Ϸ", "D", "d", "ϸ", "ь");
    List<String> secondHalfUni = Arrays.asList("U+0044", "U+0064", "U+00D0", "U+00FE", "U+010E", "U+010F",
            "U+0110", "U+0111", "U+0189", "U+018A", "U+01A2", "U+01F3", "U+01F2", "U+01F1", "U+01F7",
            "U+024A", "U+024B", "U+0071", "U+1E0A", "U+1E0B", "U+03F7", "U+03F8", "U+044C");
    String illegalStringSeperators = "[ !,.?]";

    List<String> admins = Arrays.asList("234042381249413130");
    HashSet<String> muteds = new HashSet<>();
    HashSet<String> cheeseList = new HashSet<>();
    Boolean xdIllegal = false;
    Boolean cheese = false;
    Bot bot;
    int cheeseDelay = 100;

    public MessageHandler(Bot bot) {
        this.bot = bot;
    }

    public boolean isAuthorAdmin(@Nonnull GuildMessageReceivedEvent event) {
        return (admins.contains(event.getAuthor().getId()));
    }

    public boolean isAuthorMuted(@Nonnull GuildMessageReceivedEvent event) {
        return (muteds.contains(event.getAuthor().getId()));
    }

    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        try {
            if (!event.getAuthor().isBot()) {
                if (muteHandler(event)) {
                    return;
                }
                if (msg.length() > 6 && msg.substring(0, 7).equals("!slave ")) {
                    commandHandler(msg = msg.substring(7), event);
                } else {
                    if (cheese) {
                        cheeseHandler(event);
                    }
                    if (!isAuthorAdmin(event)) {
                        if (xdIllegal) {
                            manageIllegalMessage(event);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("GuildMessageReceived error with message: " + msg);
        }
    }

    private void manageIllegalMessage(@Nonnull GuildMessageReceivedEvent event) {
        char[] chars = event.getMessage().getContentRaw().replaceAll(illegalStringSeperators, "").toCharArray();
        boolean xFound = false;
        for (char c : chars) {
            if (xFound) {
                if (secondHalfUni.contains(String.format("U+%04X", (int) c))) {
                    event.getMessage().delete().queue();
                    return;
                }
            }
            if (firstHalfUni.contains(String.format("U+%04X", (int) c))) {
                xFound = true;
            } else {
                xFound = false;
            }
        }
    }

    private void commandHandler(String message, @Nonnull GuildMessageReceivedEvent event) {
        if (isAuthorAdmin(event)) {
            adminCommandHandler(message, event);
        }
        userCommandHandler(message, event);
    }

    private void adminCommandHandler(String message, @Nonnull GuildMessageReceivedEvent event) {
        if (message.equals("lxd") || message.equals("legalize xd")) {
            xdIllegal = false;
            event.getMessage().getChannel().sendMessage("'xd' has been legalized.").queue();
            System.out.println("'xd' has been legalized.");
        } else if (message.equals("pxd") || message.equals("prohibit xd")) {
            xdIllegal = true;
            event.getMessage().getChannel().sendMessage("'xd' has been prohibited.").queue();
            System.out.println("'xd' has been prohibited.");
        } else if (message.length() >= 4 && message.substring(0,4).equals("mute")) {
            String id = message.substring(8, message.length() - 1);
            System.out.println("Added " + id + " to muteds.");
            if (muteds.add(id))
                event.getMessage().getChannel().sendMessage("<@" + id + "> has been muted.").queue();
        } else if (message.length() >= 6 && message.substring(0,6).equals("unmute")) {
            String id = message.substring(10, message.length() - 1);
            System.out.println("Removed " + id + " from muteds.");
            if (muteds.remove(id)) {
                event.getMessage().getChannel().sendMessage("<@" + id + "> has been unmuted.").queue();
            }
        }
    }

    private void userCommandHandler(String message, @Nonnull GuildMessageReceivedEvent event) {
        if (message.equals("xd status")) {
            if (xdIllegal) {
                event.getMessage().getChannel().sendMessage("'xd' is currently prohibited.").queue();
            } else {
                event.getMessage().getChannel().sendMessage("'xd' has currently legal.").queue();
            }
        } else if (message.length() == 6 && message.equals("cheese")) {
            cheese = true;
            event.getMessage().getChannel().sendMessage("Cheese activated").queue();
        } else if (message.length() > 6 && message.substring(0,6).equals("cheese")) {
            try {
                String id = message.substring(10, message.length() - 1);
                User tagged = event.getAuthor().getJDA().getUserById(id);
                List<TextChannel> channels = event.getAuthor().getJDA().getTextChannels();
                for (int i = 0; i < channels.size(); i++) {
                    if (channels.get(i).canTalk()) {
                        Message m = channels.get(i).sendMessage("<@" + id + ">")
                                .complete(true);
                        m.delete().queue();
                    }
                }
            } catch (Exception e) {
                System.out.println("Id not working for cheese command?");
            }
        } else if (message.equals("uncheese")) {
            cheese = false;
            event.getMessage().getChannel().sendMessage("Cheese deactivated").queue();
        }  else if (message.equals("code")) {
            event.getMessage().getChannel().sendMessage("https://github.com/cagly/SlaveBot").queue();
        }
    }

    private boolean muteHandler(@Nonnull GuildMessageReceivedEvent event) {
        if (isAuthorMuted(event)) {
            event.getMessage().delete().queue();
            return true;
        }
        return false;
    }

    private void cheeseHandler(@Nonnull GuildMessageReceivedEvent event) {
        event.getMessage().addReaction("U+1F9C0").queue();
        if (event.getMessage().getContentRaw().toLowerCase().contains("cheese")) {
            String id = event.getAuthor().getId();
            if (cheeseList.add(id)) {
                event.getMessage().getChannel().sendMessage(
                        "<@" + id + "> has been added to the cheese list.").queue();
            }
        }
    }
}
