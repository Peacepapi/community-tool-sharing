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
    public String username;

    public String password_hash;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "owner")
    public List<Tool> tools_owned;

    @OneToMany(cascade = CascadeType.REFRESH, mappedBy = "borrower")
    public List<Tool> tools_borrowed;

    public static Model.Finder<Long, Users> find = new Finder<Long, Users>(Users.class);


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
