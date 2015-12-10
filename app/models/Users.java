package models;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.Constraint;
import java.util.List;

/**
 * Created by Mike on 11/22/2015.
 */

@Entity
public class Users extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String username = null;

    public String password_hash = null;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "owner")
    public List<Tool> tools_owned;

    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "borrower")
    public List<Tool> tools_borrowed;

    public static Model.Finder<Long, Users> find = new Finder<Long, Users>(Users.class);


    public boolean authenticate(String password) {
        return BCrypt.checkpw(password, this.password_hash);
    }

    public static Users createNewUser(String username, String password) {

        Users user = Users.find.where().eq("username", username).findUnique();
        if (user != null) {
            user = new Users();
        } else {
            user = new Users();
            user.username = username;
        }

        if(password == null || username == null || password.length() < 8) {
            user.password_hash = null;
        } else {
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            user.password_hash = passwordHash;
        }

        return user;
    }

}
