package bot.rat.entities.embeddables;

import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
public class WordUserId implements Serializable {

    String word;
    String userId;

    public WordUserId(String word, String userId) {
        this.word = word;
        this.userId = userId;
    }

    public boolean equals(WordUserId obj) {
        return this.word.equals(obj.word) && this.userId.equals(obj.userId);
    }
}
