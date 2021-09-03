package bot.rat.entities.embeddables;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class WordUserId implements Serializable {

    public String word;
    public String userId;

    public WordUserId(){}

    public WordUserId(String word, String userId) {
        this.word = word;
        this.userId = userId;
    }

    public boolean equals(WordUserId obj) {
        return this.word.equals(obj.word) && this.userId.equals(obj.userId);
    }
}
