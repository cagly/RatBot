package bot.rat;

import bot.rat.messages.MessageHandler;
import bot.rat.privateResources.BotToken;
import bot.rat.repositories.UserRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
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
        intentList.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        intentList.add(GatewayIntent.GUILD_EMOJIS);
        JDA jda = JDABuilder.createDefault(
                BotToken.TOKEN, intentList)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
        jda.addEventListener(bot);
        jda.awaitReady();
//        jda.getTextChannelsByName("bot-test", true).get(0).sendMessage("RatBot is back online!").complete(true);
        bot.messageHandler.startup();
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


    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getReactionEmote().getAsCodepoints().equals("U+1f972")) {
            event.retrieveMessage().complete().addReaction("U+1F972").queue();
        }
        super.onMessageReactionAdd(event);
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        Message asd = event.retrieveMessage().complete();
        List<MessageReaction> reactList = asd.getReactions();
        boolean onlyRat = false;
        for (MessageReaction react : reactList) {
            if (react.getReactionEmote().getAsCodepoints().equals("U+1f972") && react.hasCount()
            && react.getCount() == 1 && react.isSelf()) {
                onlyRat = true;
            }
        }
        if (onlyRat) {
            asd.removeReaction("U+1F972").queue();
        }
        super.onMessageReactionRemove(event);
    }
}
