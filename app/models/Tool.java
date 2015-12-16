package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

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

    public String description;

    @ManyToOne
    @Constraints.Required
    public ToolType toolType;

    @ManyToOne
    public Users borrower = null;

    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL)
    public List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "requestedTool")
    public List<BorrowRequests> requestList = new ArrayList<>();

    public static Finder<Long, Tool> find = new Finder<Long, Tool>(Tool.class);

    public static Tool createNewTool(@Nonnull String name,@Nonnull String desc,
                                     @Nonnull Users owner, @Nonnull ToolType toolType){
        Tool tool = new Tool();
        tool.name = name;
        tool.description = desc;
        tool.owner = owner;
        tool.toolType = toolType;
        return tool;
    }
}
