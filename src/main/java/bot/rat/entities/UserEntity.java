package bot.rat.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class UserEntity {

    @Id
    private String id;
    private Integer points;
    private Boolean isAdmin;
    private Boolean isMuted;
    @OneToMany(mappedBy = "user")
    private List<Word> words;

    public UserEntity(String id, Integer points) {
        this.id = id;
        this.points = points;
        this.isAdmin = false;
        this.isMuted = false;
    }

    public UserEntity() {}

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getMuted() {
        return isMuted;
    }

    public void setMuted(Boolean muted) {
        isMuted = muted;
    }

    public String getId() {
        return id;
    }
}
