package controllers;

import com.avaje.ebean.Expr;
import com.avaje.ebean.annotation.Transactional;
import models.BorrowRequest;
import models.Tool;
import models.ToolType;
import models.Users;
import play.data.DynamicForm;
import play.mvc.*;
import play.mvc.Controller;
import scala.concurrent.java8.FuturesConvertersImpl;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;

import views.html.*;

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
            return notFound("No such tool listed");
        }

        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        if ( tool.owner.id.equals(user.id) ) {
            flash("error","You cannot borrow your own tool!");
            return redirect(routes.Tools.browse());
        } else if (tool.requestList.contains(user)) {
            return badRequest(views.html.errors.badrequest.render("You already quested the tool"));
        } else if (user.borrowingList.contains(tool)) {
            return badRequest(views.html.errors.badrequest.render("The tool is already in your possesion"));
        }

        BorrowRequest request = BorrowRequest.createNewBorrowRequest(user, tool);
        try {
            request.save();
            flash("success", "Your request has been sent");
        } catch (PersistenceException e) {
            flash("error", "Could not borrow the tool, please try again later.");
            return badRequest(views.html.errors.badrequest.render("You have already requested for the tool!"));
        }
        return redirect(routes.Tools.browse());
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
        List<Tool> toolList;
        if (session().containsKey("user_id")) {
            long user_id = Long.parseLong(session("user_id"));
            toolList = Tool.find.where().or(Expr.eq("borrower_id",null),Expr.ne("borrower_id", user_id)).orderBy("name").findList();
        } else {
            toolList = Tool.find.orderBy("toolType.name").findList();
        }
        return ok(views.html.tools.browse.render(toolList));

//        return ok(views.html.tools.browse.render(Tool.find.orderBy("toolType.name").findList()));
    }

    public Result eachTool(long id) {
        Tool tool = Tool.find.byId(id);
        if(tool == null)
            return notFound(views.html.errors.notfound.render("The tool you are looking for does not exist."));
        else
        return ok(views.html.tools.eachTool.render(tool));
    }
}
