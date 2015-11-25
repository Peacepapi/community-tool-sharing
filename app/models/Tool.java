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


    public String description;

    @ManyToOne
    public ToolType type;

    @ManyToOne
    public Users owner;

    public static Finder<Long, Tool> find = new Finder<Long, Tool>(Tool.class);
}
