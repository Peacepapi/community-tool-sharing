package controllers;

import models.Tool;
import models.Users;
import org.h2.engine.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import play.mvc.Controller;

import java.util.List;


import static play.data.Form.form;

/**
 * Created by Mike on 11/14/2015.
 */
public class Tools extends Controller {

    public final static int BORROW = 267769;
    public final static int DELETE = 335383;


    public Result index() {
        List<Tool> tools = null;
//        tools = Tool.find.all();
        return ok(views.html.browse.render(tools));
    }

    public Result getToolByUserId(Long user_id) {
        List<Tool> tools = Tool.find.query().where("owner_id="+user_id).findList();
        return ok(views.html.browse.render(tools));
    }

    @Security.Authenticated(UserAuth.class)
    public Result ownedTools() {
        List<Tool> tools = null;
        if (session("user_id") != null) {
            Long user_id = Long.parseLong(session("user_id"));
            tools = Tool.find.query().where("owner_id="+user_id).findList();
        }
        return ok(views.html.tools.ownedTools.render(tools));
    }

    @Security.Authenticated(UserAuth.class)
    public Result create() {
        DynamicForm toolForm = form().bindFromRequest();
        String toolName = toolForm.data().get("name");
        String toolDesc = toolForm.data().get("description");

        if (toolName == null) {
            flash("error", "Missing too name!");
        } else {
            Tool tool = new Tool();
            tool.name = toolName;
            tool.description = toolDesc;
            tool.owner = Users.find.byId(Long.parseLong(session("user_id")));
            tool.save();
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
        return redirect(routes.Tools.index());
    }
/*
    @Security.Authenticated(UserAuth.class)
    public Result borrowOrDelete(Long id, int requestCode) {
        switch (requestCode) {
            case Tools.DELETE:
                return redirect(routes.Tools.delete(id));
            case Tools.BORROW:
                return redirect(routes.Tools.borrow(id));
            default:
                //log error. Request code not matched
                flash("error", "Invalid request code: " + requestCode + ", ID: " + id);
                break;
        }
        return redirect(routes.Tools.index());
    }
*/
    @Security.Authenticated(UserAuth.class)
    public Result borrow(Long id) {

        Tool tool = Tool.find.byId(id);
        Long userId = Long.parseLong(session("user_id"));

        if (tool == null) {
            flash("error", "The selected tool does not exist!");
            //handle error here!
        } else {
            if ( !tool.owner.id.equals(userId) && tool.borrower == null ) {
                //clear to borrow.
                tool.borrower = Users.find.byId(userId);
                tool.update();
            } else {
                flash("error", "Borrow: You're not the owner of this tool.");
            }
        }
        return redirect(routes.Tools.index());
    }

    @Security.Authenticated(UserAuth.class)
    public Result delete(Long id) {

        Tool tool = Tool.find.byId(id);
        if (tool == null) {
            flash("error", "The selected tool does not exist!");
            //handle error here!
        } else {
            if (tool.owner.id == Long.parseLong(session("user_id"))) {
                //clear for delete.
                tool.delete();
            } else {
                flash("error", "DELETE: You're not the owner of this tool.");
            }
        }
        return redirect(routes.Tools.index());
    }
}
