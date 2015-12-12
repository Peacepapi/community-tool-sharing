package models;

import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import javax.persistence.Table;
import javax.persistence.Column;

import javax.validation.Constraint;
import java.util.List;

/**
 * Created by Mike on 11/22/2015.
 */

@Table(name="users")
@Entity
public class Users extends Model {

    @Id
    public Long id;

    @Constraints.Required

    @Column(unique=true)
    public String username;

    @Constraints.Required
    public String email;

    public String password_hash;

    @OneToMany
    public List<Tool> toolList;

    public static Model.Finder<Long, Users> find = new Model.Finder<Long, Users>(Users.class);

    public boolean authenticate(String password) {
        return BCrypt.checkpw(password, this.password_hash);
    }

    public static Users createNewUser(String username, String password,String email) {
        if(password == null || username == null || password.length() < 8 || 
            email == null ) {
            return null;
        }

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
        Users user = new Users();
        user.username = username;
        user.email = email;
        user.password_hash = passwordHash;

        return user;
    }
}
