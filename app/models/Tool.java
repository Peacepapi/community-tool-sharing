package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.Transactional;
import controllers.UserAuth;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;

import models.ToolType;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mike on 11/14/2015.
 */

@Entity
public class Tool extends Model {

    public static int STATUS_PENDING_BORROW = 2000;
    public static int STATUS_PENDING_RETURN = 2500;
    public static int STATUS_BORROWED = 3000;
    public static int STATUS_OPEN = 1000;

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

    public int borrowingStatus = 1000;

    @OneToMany(mappedBy = "tool", cascade = CascadeType.ALL)
    public List<Comment> comments = new ArrayList<>();




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

    public boolean requestBorrow(@Nonnull Tool tool, @Nonnull Users requester){
        if (tool.borrower == null || tool.borrowingStatus == STATUS_OPEN || requester != tool.owner) {
            tool.borrower = requester;
            tool.borrowingStatus = STATUS_PENDING_BORROW;
            return true;
        }
        return false;
    }

    public boolean acceptBorrow(@Nonnull Tool tool) {
        if (tool.borrower != null || tool.borrowingStatus == STATUS_PENDING_BORROW) {
            tool.borrowingStatus = STATUS_BORROWED;
            return true;
        }
        return false;
    }
}
