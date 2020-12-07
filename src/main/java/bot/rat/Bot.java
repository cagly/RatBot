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
        ApplicationContext context = new AnnotationConfigApplicationContext(PersistenceJPAConfig.class);
        context.getBean(UserRepository.class);
        bot.messageHandler = context.getBean(MessageHandler.class);
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
