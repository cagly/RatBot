package bot.rat.entities;

import bot.rat.entities.embeddables.WordUserId;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Word {

    @EmbeddedId
    public WordUserId id;
    public Integer count;


}
