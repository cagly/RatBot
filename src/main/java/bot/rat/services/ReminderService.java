package bot.rat.services;

import bot.rat.entities.DndReminder;
import bot.rat.repositories.DndReminderRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReminderService {

    private JDA jda;
    private TextChannel channel;

    @Autowired
    DndReminderRepository reminderRepository;

    public ReminderService() {
    }

    public void startUp(JDA jda) {
        this.jda = jda;
        List<Guild> guilds = jda.getGuildsByName("BD Squad DnD", true);
        if (!guilds.isEmpty()) {
            Guild guild = guilds.get(0);
            List<TextChannel> channels = guild.getTextChannelsByName("general", true);
            if (!channels.isEmpty()) {
                this.channel = channels.get(0);
            }
        }
    }

    @Scheduled(cron = "0 45 17 * * *")
    public void DndReminder() {
        List<DndReminder> reminders = reminderRepository.findAll();
        for (DndReminder reminder : reminders) {
            if (reminder.trigger()) {
                channel.sendMessage(reminder.message()).queue();
            }
            reminderRepository.save(reminder);
        }
//        rathole.sendMessage("Test").queue();
    }

    public void test(TextChannel channel) {
        List<DndReminder> reminders = reminderRepository.findAll();
        for (int i = 0; i < 5; i++) {
            for (DndReminder reminder : reminders) {
                if (reminder.trigger()) {
                    channel.sendMessage(reminder.message()).queue();
                }
            }
        }
    }

    public void createReminder(String id, Integer daysSinceDnd, Integer remindEveryXDays) {
        DndReminder newReminder = new DndReminder(id, daysSinceDnd, remindEveryXDays);
        reminderRepository.save(newReminder);
    }

    public boolean resetReminder(String id) {
        Optional<DndReminder> reminder = reminderRepository.findById(id);
        if (reminder.isPresent()) {
            DndReminder rem = reminder.get();
            rem.setDaysSince(0);
            reminderRepository.save(rem);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteReminder(String id) {
        Optional<DndReminder> reminder = reminderRepository.findById(id);
        if (reminder.isPresent()) {
            reminderRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public void clearReminders() {
        reminderRepository.deleteAll();
    }
}
