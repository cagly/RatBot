package bot.rat.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
public class DndReminder {

    @Id
    private String id;
    private Integer daysSinceDnd = 0;
    private Integer remindEveryXDays = 3;
    private Integer daysUntilRemind = 0;

    public DndReminder() {}

    public DndReminder(String id, Integer daysSinceDnd, Integer remindEveryXDays) {
        this.id = id;
        this.daysSinceDnd = daysSinceDnd;
        this.remindEveryXDays = remindEveryXDays;
    }

    public String message() {
        return "It has been " + ChronoUnit.DAYS.between(getSupposedDate(), LocalDateTime.now().withHour(0).withMinute(0).withSecond(2)) + " days since " + id + " last session.";
    }

    public LocalDateTime getSupposedDate(){
        return LocalDateTime.now().minusDays(daysSinceDnd).withHour(0).withMinute(0).withSecond(0);
    }

    public void setDaysSince(Integer days) {
        this.daysSinceDnd = days;
    }

    public boolean trigger() {
        if (daysUntilRemind <= 1) {
            daysUntilRemind = remindEveryXDays;
            return true;
        }
        daysSinceDnd++;
        daysUntilRemind -= 1;
        return false;
    }
}
