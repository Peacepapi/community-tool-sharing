package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;
import java.util.List;


/**
 * Created by Mike on 11/14/2015.
 */

@Entity
public class ToolType extends Model {
    @Id
    public Long id;

    @Constraints.Required
    public String name;

    @OneToMany
    public List<Tool> toolList;

    public static Finder<Long, ToolType> find = new Finder<Long, ToolType>(ToolType.class);

}