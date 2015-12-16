package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import java.sql.Timestamp;

/**
 * Created by Mike on 12/16/2015.
 */
@Entity
public class Notification extends Model {

    @Id
    public long id;

    @Constraints.Required
    @ManyToOne
    public Users owner;

    @Constraints.Required
    public String message;

    @Constraints.Required
    public Timestamp timestamp;

    public static Model.Finder<Long, Notification> find =
            new Finder<Long, Notification>(Notification.class);

}
