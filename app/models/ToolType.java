package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;


/**
 * Created by Mike on 11/14/2015.
 */

@Entity
public class ToolType extends Model {
    @Id
    public Long id;

    @Constraints.Required
    @Column(unique = true)
    public String name;

    @OneToMany(mappedBy = "toolType")
    public List<Tool> toolList;

    public static Model.Finder<Long, ToolType> find = new Model.Finder<Long, ToolType>(ToolType.class);

    public static ToolType createNewToolType(String name){
        if (name == null || name.isEmpty()) {
            return null;
        }

        ToolType toolType = new ToolType();
        toolType.name = name;

        return toolType;
    }

}