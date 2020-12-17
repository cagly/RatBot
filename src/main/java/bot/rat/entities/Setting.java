package bot.rat.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Setting {

    @Id
    private String id;
    private Boolean bool;
    private String string;

    public Setting(String id) {
        this.id = id;
    }

    public Setting(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
