package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.util.List;

/**
 * Created by Mike on 12/13/2015.
 */
@Entity
public class Profile extends Model {

    @Id
    public Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @Constraints.Required
    @Column(unique=true)
    public Users user;

    public String fName;

    public String lName;

    public static Finder<Long, Profile> find = new Finder<Long, Profile>(Profile.class);


    public static Profile createNewProfile(@Nonnull Users poster) {
        Profile nProfile = new Profile();
        nProfile.user = poster;
        return nProfile;
    }
}
