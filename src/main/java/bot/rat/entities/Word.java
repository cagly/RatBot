package bot.rat.entities;

import bot.rat.entities.embeddables.WordUserId;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class Word {

    @EmbeddedId
    public WordUserId id;
    public Integer count;

    public Word(WordUserId id, Integer count) {
        this.id = id;
        this.count = count;
    }
}
