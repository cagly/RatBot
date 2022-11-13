package bot.rat;

import bot.rat.messages.MessageHandler;
import bot.rat.privateResources.BotToken;
import bot.rat.services.ReminderService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
public class Bot extends ListenerAdapter {

    ReminderService reminderService;
    MessageHandler messageHandler;

    public Bot() throws IOException {
    }

    public static void main(String[] args) throws Exception {
        Bot bot = new Bot();
        ApplicationContext context = new AnnotationConfigApplicationContext(PersistenceJPAConfig.class);
        bot.messageHandler = context.getBean(MessageHandler.class);
        bot.reminderService = context.getBean(ReminderService.class);
        List<GatewayIntent> intentList = new ArrayList<>();
        intentList.add(GatewayIntent.GUILD_MEMBERS);
        intentList.add(GatewayIntent.GUILD_MESSAGES);
        intentList.add(GatewayIntent.GUILD_PRESENCES);
        intentList.add(GatewayIntent.DIRECT_MESSAGES);
        intentList.add(GatewayIntent.MESSAGE_CONTENT);
        intentList.add(GatewayIntent.DIRECT_MESSAGE_TYPING);
        intentList.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        intentList.add(GatewayIntent.GUILD_EMOJIS_AND_STICKERS);
        JDA jda = JDABuilder.createDefault(
                BotToken.TOKEN, intentList)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
        jda.addEventListener(bot);
        jda.awaitReady();
        jda.getTextChannelsByName("bot-test", true).get(0).sendMessage("RatBot is back online!!").complete(true);
        bot.messageHandler.startup();
        bot.reminderService.startUp(jda);
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        messageHandler.onGuildMessageReceived(event);
        super.onMessageReceived(event);
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
//      TODO: Add support for joy emoji shit
//        if (event.getEmoji().getAsReactionCode().equals("U+1f972")) {
//            event.retrieveMessage().complete().addReaction("U+1F972").queue();
//        }
//        super.onMessageReactionAdd(event);
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        // TODO: Same as above, but for removal
//        Message asd = event.retrieveMessage().complete();
//        List<MessageReaction> reactList = asd.getReactions();
//        boolean onlyRat = false;
//        for (MessageReaction react : reactList) {
//            if (react.getReactionEmote().getAsCodepoints().equals("U+1f972") && react.hasCount()
//            && react.getCount() == 1 && react.isSelf()) {
//                onlyRat = true;
//            }
//        }
//        if (onlyRat) {
//            asd.removeReaction("U+1F972").queue();
//        }
//        super.onMessageReactionRemove(event);
    }
}
