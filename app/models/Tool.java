package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.ToolType;

/**
 * Created by Mike on 11/14/2015.
 */

@Entity
public class Tool extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String name;

    @ManyToOne
    @Constraints.Required
    public Users owner;

    @ManyToOne
    public Users borrower = null;

    public String description;

    @ManyToOne
    public ToolType type;


    public static Finder<Long, Tool> find = new Finder<Long, Tool>(Tool.class);

    public Tool borrow(Users borrower) {
        if (this.borrower != null) {
            return null;
        } else {
            this.borrower = borrower;
        }
        return this;
    }

}
