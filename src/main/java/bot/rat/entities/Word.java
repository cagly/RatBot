package bot.rat.entities;

import bot.rat.entities.embeddables.WordUserId;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Word {

    @EmbeddedId
    public WordUserId id;
    public Integer count;

    public Word(){}

    public Word(WordUserId id, Integer count) {
        this.id = id;
        this.count = count;
    }
}
