package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Model;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Pattern;

/**
 * Created by Mike on 11/22/2015.
 */

//@Table(name="users")
@Entity
@Table (uniqueConstraints = @UniqueConstraint( columnNames = {"username", "email"}))
public class Users extends Model {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]{3,20}$";
    public static final Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);

    @Id
    public Long id;

    @Constraints.Required
    @Column(unique=true)
    public String username = null;

    @Constraints.Required
    @Constraints.Email
    @Column(unique=true)
    public String email = null;

    @Constraints.Required
    public String password_hash = null;

    @OneToOne
    public Profile userProfile;

    @OneToMany(mappedBy = "owner")
    public List<Tool> toolList;

    @OneToMany(mappedBy = "borrower")
    public List<Tool> borrowingList;

    @Constraints.Required
    public String userType = "Normal";


    @OneToMany(mappedBy = "poster")
    public List<Comment> comments;

    @OneToMany(mappedBy = "requester")
    public List<BorrowRequests> requestList;

    @OneToMany(mappedBy = "owner")
    public List<RequestNotification> notifications = new ArrayList<>();

    public static Model.Finder<Long, Users> find = new Model.Finder<Long, Users>(Users.class);

    public boolean authenticate(String password) {
        return BCrypt.checkpw(password, this.password_hash);
    }

    public static Users createNewUser(String username, String password, String email) {
        if(password == null || username == null || password.length() < 8 || 
            email == null) {
            return null;
        }

        Users user = new Users();
        user.username = username;
        user.email = email;
        user.password_hash = BCrypt.hashpw(password, BCrypt.gensalt());

        return user;
    }

    @PreRemove
    private void preRemove() {
        //do some clean up here
        //more cleanUp to come
    }

    @PostPersist
    private void postPersist() {
        Profile profile = Profile.createNewProfile(this);
        profile.save();
        this.userProfile = profile;
        this.update();
    }
}
