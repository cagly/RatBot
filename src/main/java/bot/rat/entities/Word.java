package bot.rat.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Word {

    @Id
    private String word;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private Integer count;

}
