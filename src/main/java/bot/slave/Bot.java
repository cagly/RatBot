package bot.slave;

import bot.slave.exceptions.MessageListenerException;
import com.sun.javafx.util.Logging;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.annotation.Nonnull;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class Bot extends ListenerAdapter {

    List<String> illegalMessages = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        List<GatewayIntent> intentList = new ArrayList<>();
        intentList.add(GatewayIntent.GUILD_MEMBERS);
        intentList.add(GatewayIntent.GUILD_MESSAGES);
        intentList.add(GatewayIntent.GUILD_PRESENCES);
        Bot bot = new Bot();
        JDA jda = JDABuilder.createDefault(
                "Nzg0Mzk3MDUwNjczNDk2MDc0.X8osrg.ErY7oMOINGOTi1IVNk7JidGoJ5Y", intentList).build();
        jda.addEventListener(bot);
        bot.generateIllegalMessages();
    }

    private void generateIllegalMessages() {
        List<String> firstHalf = Arrays.asList("><", "x", "X", "Х","х","Ӽ", "ӽ", "Ӿ", "ӿ");
        List<String> secondHalf = Arrays.asList("\u0110", "þ", "Ď", "\u0111", "\u00d0", "đ", "Ɖ","Ɗ"
                    ,"Ƣ", "ǳ", "ǲ", "Ǳ", "Ƿ", "Ɋ","ɋ","q", "Ḋ","ḋ", "Ϸ", "D", "d", "ϸ", "ь");
        for (String first : firstHalf) {
            for (String second : secondHalf) {
                illegalMessages.add(first+second);
            }
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        System.out.println(msg);
        try {
            if (msg.contains("cheese")) {
                event.getMessage().addReaction("U+1F9C0").queue();
            }
            for (String illeg : illegalMessages) {
                if (msg.contains(illeg)) {
                    event.getMessage().delete().queue();
                }
            }
        } catch (Exception e) {
            System.out.println("GuildMessageReceived error with message: " + msg);
        }
        super.onGuildMessageReceived(event);
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        try {

        } catch(Exception e) {
            System.out.println("PrivateMessageReceived error with message: " + msg);
        }
        super.onPrivateMessageReceived(event);
    }
}
