package bot.rat.repositories;

import bot.rat.entities.DndReminder;
import bot.rat.entities.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DndReminderRepository extends JpaRepository<DndReminder, String> {


}
