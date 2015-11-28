package models;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import play.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.Constraint;
/**
 * Created by Mike on 11/22/2015.
 */


@Entity
public class Users extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String username;

    public String password_hash;

    public static Model.Finder<Long, Users> find = new Model.Finder<Long, Users>(Users.class);

    public boolean authenticate(String password) {
        return BCrypt.checkpw(password, this.password_hash);
    }

    public static Users createNewUser(String username, String password) {
        if(password == null || username == null || password.length() < 8) {
            return null;
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        Users user = new Users();
        user.username = username;
        user.password_hash = passwordHash;

        return user;
    }

}
