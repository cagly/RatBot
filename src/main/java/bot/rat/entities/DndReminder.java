package bot.rat.entities;

import bot.rat.repositories.DndReminderRepository;
import org.hibernate.annotations.Generated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Entity
public class DndReminder {

    @Id
    private String id;
    private Integer daysSinceDnd = 0;
    private Integer remindEveryXDays = 3;
    private Integer daysUntilRemind = 0;
    private LocalDateTime supposedDate;

    public DndReminder() {}

    public DndReminder(String id, Integer daysSinceDnd, Integer remindEveryXDays) {
        this.id = id;
        this.daysSinceDnd = daysSinceDnd;
        this.remindEveryXDays = remindEveryXDays;
        this.supposedDate = LocalDateTime.now().minusDays(daysSinceDnd).withHour(0).withMinute(0).withSecond(0);
    }

    public String message() {
        return "It has been " + ChronoUnit.DAYS.between(supposedDate, LocalDateTime.now().withHour(0).withMinute(0).withSecond(1)) + " days since " + id + " last session.";
    }

    public void setDaysSince(Integer days) {
        this.daysSinceDnd = days;
    }

    public boolean trigger() {
        if (daysUntilRemind <= 1) {
            daysUntilRemind = remindEveryXDays;
            return true;
        }
        daysUntilRemind -= 1;
        return false;
    }
}
