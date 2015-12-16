package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;
import models.Tool;
import models.ToolType;
import models.Users;
import play.data.DynamicForm;
import play.mvc.*;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;


import static play.data.Form.form;

/**
 * Created by Mike on 11/14/2015.
 */
public class Tools extends Controller {

    public final static int BORROW = 267769;
    public final static int DELETE = 335383;


    public Result index() {
        String userId = session("user_id");
        if (userId == null) {
            return ok(views.html.tools.browse.render(Tool.find.all()));
        } else {
            return ownedTools(Long.parseLong(userId));
        }
    }

    public Result getToolByUser(Long user_id) {
        List<Tool> tools = Tool.find.where().eq("owner_id", user_id).findList();
        return ok(views.html.tools.browse.render(tools));
    }

    public Result getToolByType(Long type_id) {
        return ok(views.html.tools.browse.render(Tool.find.where().eq("tool_type_id", type_id).findList()));
    }

    @Security.Authenticated(UserAuth.class)
    public Result ownedTools(Long user_id) {
        List<Tool> tools = new ArrayList<>();
        String currentUserID = session("user_id");
        if (currentUserID != null && Long.parseLong(currentUserID) == user_id) {
            tools = Tool.find.query().where("owner_id="+user_id).findList();
        }
        return ok(views.html.tools.ownedTools.render(tools));
    }

    @Security.Authenticated(UserAuth.class)
    public Result create() {
        DynamicForm toolForm = form().bindFromRequest();
        String toolName = toolForm.data().get("name");
        String toolDesc = toolForm.data().get("description");
        String typeId = toolForm.data().get("type_id");

        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        ToolType toolType = ToolType.find.byId(Long.parseLong(typeId));
        if (toolType == null) {
            flash("error","Requested tool type does not exist!");
            return redirect(routes.Tools.browse());
        }

        if (toolName == null) {
            flash("error", "Missing tool name!");
        } else if (typeId.isEmpty()) {
            flash("error", "Tool type not selected!");
        } else {
            Tool tool = Tool.createNewTool(toolName, toolDesc, user, toolType);
            if (tool.owner == null) {
                flash("error","Owner does not exist!");
            } else if (tool.toolType == null) {
                flash("error","Tool type does not exist");
            } else {
                tool.save();
                flash("success", tool.name + " added!");
            }
        }
        /*
        Form<Tool> form = Form.form(Tool.class).bindFromRequest();
        if (form.hasErrors()) {
            flash("error", "Missing fields!");
        } else {
            Tool tool = form.get();
            tool.owner = Users.find.byId(Long.parseLong(session("user_id")));
            tool.save();
            flash("success", "Saved new Tool: " + tool.name);
        }
        */

        return redirect(routes.Tools.browse());
    }

    @Security.Authenticated(UserAuth.class)
    @Transactional
    public Result requestBorrow(Long id) {

        Tool tool = Tool.find.byId(id);
        if (tool == null) {
            flash("error", "The requested tool does not exist!");
            //handle error here!
            return redirect(routes.Tools.browse());
        }

        if ( tool.owner.id.equals(Long.parseLong(session("user_id"))) ) {
            flash("error","You cannot borrow your own tool!");
            return redirect(routes.Tools.browse());
        }

//        if (requestBorrow())

        return redirect(routes.Tools.browse());
    }

    @Security.Authenticated(UserAuth.class)
    @Transactional
    public Result requestReturn(Long id) {
        Tool tool = Tool.find.byId(id);
        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        if (tool == null) {
            flash("error", "The tool being requested does not exist");
            return redirect(routes.Tools.browse());
        } else if (tool.borrower != user) {
            flash("error", "You are not the borrower of this tool");
            return redirect(routes.Tools.browse());
        } else if (tool.borrowingStatus != Tool.STATUS_BORROWED) {
            flash("error", "Tool cannot currently be returned. Currently request needs to be canceled");
            return redirect(routes.Tools.browse());
        }
        tool.borrowingStatus = Tool.STATUS_PENDING_RETURN;
        flash("success","Request to borrow has been sent!");
        return redirect(routes.Tools.eachTool(tool.id));
    }

    @Security.Authenticated(UserAuth.class)
    @Transactional
    public Result lendTool(Long id) {
        Tool tool = Tool.find.byId(id);
        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        if (tool == null) {
            flash("error", "The tool being requested does not exist");
            return redirect(routes.Tools.browse());
        } else if (tool.borrower != user) {
            flash("error", "You are not the borrower of this tool");
            return redirect(routes.Tools.browse());
        } else if (tool.borrowingStatus != Tool.STATUS_PENDING_BORROW) {
            flash("error", "There is no request to borrow this tool");
            return redirect(routes.Tools.browse());
        }
        tool.borrowingStatus = Tool.STATUS_PENDING_RETURN;
        flash("success","Request to borrow has been sent!");
        return redirect(routes.Tools.eachTool(tool.id));
    }

    @Security.Authenticated(UserAuth.class)
    public Result remove(Long id) {

        Tool tool = Tool.find.byId(id);
        if (tool == null) {
            flash("error", "The selected tool does not exist!");
            //handle error here!
        } else {
            if (tool.owner.id == Long.parseLong(session("user_id"))) {
                //clear for remove.
                tool.delete();
            } else {
                flash("error", "You're not the owner of this tool.");
            }
        }
        return redirect(routes.Tools.browse());
    }

    public Result browse() {
        //fetch all tools
        return ok(views.html.tools.browse.render(Tool.find.orderBy("toolType.name").findList()));
    }

    public Result eachTool(long id) {
        Tool tool = Tool.find.byId(id);
        if(tool == null) 
            return notFound("No such tool listed");
        else
        return ok(views.html.tools.eachTool.render(tool));
    }
}
