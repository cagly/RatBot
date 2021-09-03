package bot.rat.entities.embeddables;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class WordUserId implements Serializable {

    String word;
    String userId;

    public boolean equals(WordUserId obj) {
        return this.word.equals(obj.word) && this.userId.equals(obj.userId);
    }
}
