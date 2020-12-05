package bot.slave;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Bot extends ListenerAdapter {

    MessageHandler messageHandler = new MessageHandler(this);

    public Bot() throws IOException {
    }

    public static void main(String[] args) throws Exception {
        List<GatewayIntent> intentList = new ArrayList<>();
        intentList.add(GatewayIntent.GUILD_MEMBERS);
        intentList.add(GatewayIntent.GUILD_MESSAGES);
        intentList.add(GatewayIntent.GUILD_PRESENCES);
        intentList.add(GatewayIntent.DIRECT_MESSAGES);
        intentList.add(GatewayIntent.DIRECT_MESSAGE_TYPING);
        Bot bot = new Bot();
        JDA jda = JDABuilder.createDefault(
                BotToken.TOKEN, intentList).build();
        jda.addEventListener(bot);
//        jda.getTextChannels().get(1).sendMessage("IkitBot is back online!").queue();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        messageHandler.onGuildMessageReceived(event);
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
