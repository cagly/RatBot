package bot.rat.entities;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Word {

    @Id
    private String word;
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    private Integer count;

}
