package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.Transactional;
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

    @OneToMany(mappedBy = "toolType", cascade = CascadeType.ALL)
    public List<Tool> toolList;

    @PreRemove
    @Transactional
    private void preRemove(){
        ToolType miscType = ToolType.find.where().eq("name", "Misc").findUnique();
        for (Tool tool : toolList) {
            tool.toolType = miscType;
        }
    }

    public static Model.Finder<Long, ToolType> find = new Finder<Long, ToolType>(ToolType.class);

    public static ToolType createNewToolType(String name){
        if (name == null || name.isEmpty()) {
            return null;
        }

        ToolType toolType = new ToolType();
        toolType.name = name;

        return toolType;
    }

}