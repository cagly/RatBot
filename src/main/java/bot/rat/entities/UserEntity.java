package bot.rat.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserEntity {

    @Id
    private String id;
    private Integer points;
    private Boolean isAdmin;
    private Boolean isMuted;

    public UserEntity(String id, Integer points) {
        this.id = id;
        this.points = points;
        this.isAdmin = false;
        this.isMuted = false;
    }

    public UserEntity() {}

}
