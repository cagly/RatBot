package bot.rat.repositories;

import bot.rat.entities.Word;
import bot.rat.entities.embeddables.WordUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, WordUserId> {
}
