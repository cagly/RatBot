package bot.rat;

import bot.rat.messages.MessageHandler;
import bot.rat.privateResources.BotToken;
import bot.rat.repositories.UserRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
public class Bot extends ListenerAdapter {

    MessageHandler messageHandler;

    public Bot() throws IOException {
    }

    public static void main(String[] args) throws Exception {
        Bot bot = new Bot();
        ApplicationContext context = new AnnotationConfigApplicationContext(PersistenceJPAConfig.class);
        bot.messageHandler = context.getBean(MessageHandler.class);
        List<GatewayIntent> intentList = new ArrayList<>();
        intentList.add(GatewayIntent.GUILD_MEMBERS);
        intentList.add(GatewayIntent.GUILD_MESSAGES);
        intentList.add(GatewayIntent.GUILD_PRESENCES);
        intentList.add(GatewayIntent.DIRECT_MESSAGES);
        intentList.add(GatewayIntent.DIRECT_MESSAGE_TYPING);
        JDA jda = JDABuilder.createDefault(
                BotToken.TOKEN, intentList).build();
        jda.addEventListener(bot);
        jda.awaitReady();
        jda.getTextChannelsByName("bot-test", true).get(0).sendMessage("RatBot is back online!").complete(true);
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
