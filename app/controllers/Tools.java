package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.annotation.Transactional;
import models.BorrowRequests;
import models.Tool;
import models.ToolType;
import models.Users;
import play.data.DynamicForm;
import play.mvc.*;
import play.mvc.Controller;

import javax.persistence.PersistenceException;
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
        return ok(views.html.tools.browse.render(Tool.find.all()));
    }

    public Result getToolByUser(Long user_id) {
        List<Tool> tools = Tool.find.where().eq("owner_id", user_id).findList();
        return ok(views.html.tools.browse.render(tools));
    }

    public Result getToolByType(Long type_id) {
        return ok(views.html.tools.browse.render(Tool.find.where().eq("tool_type_id", type_id).findList()));
    }

    @Security.Authenticated(UserAuth.class)
    public Result create() {
        DynamicForm toolForm = form().bindFromRequest();
        String toolName = toolForm.data().get("name");
        if(toolName.isEmpty()) {
            flash("error", "Missing name!");
            return redirect(routes.Tools.browse());
        }
        String toolDesc = toolForm.data().get("description");
        String typeId = toolForm.data().get("type_id");
        if(typeId.isEmpty()) {
            flash("error", "Please select a type!");
            return redirect(routes.Tools.browse());
        }

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

        return redirect(routes.Tools.browse());
    }

    @Security.Authenticated(UserAuth.class)
    @Transactional
    public Result requestBorrow(Long id) {

        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        Tool tool = Tool.find.byId(id);
        if (tool == null) {
            flash("error", "The requested tool does not exist!");
            return notFound("No such tool listed");
        }

        if ( tool.owner.id.equals(user.id) ) {
            flash("error","You cannot borrow your own tool!");
            return redirect(routes.Tools.browse());
        } else if (user.borrowingList.contains(tool)) {
            return badRequest(views.html.errors.badrequest.render("The tool is already in your possession"));
        }
        BorrowRequests request = BorrowRequests.createNewBorrowRequest(user, tool);
        try {
            request.save();
            flash("success", "Your request has been sent");
        } catch (PersistenceException e) {
            flash("error","You already requested the tool");
            return redirect(routes.Tools.browse());
//            return badRequest(views.html.errors.badrequest.render("You have already requested for the tool!"));
        }
        return redirect(routes.Tools.browse());
    }

    @Security.Authenticated(UserAuth.class)
    @Transactional
    public Result lendTool(long tool_id, long requester_id){

        BorrowRequests borrowRequests = BorrowRequests.find.where()
                .and(
                    Expr.eq("requester_id", requester_id),
                    Expr.eq("requested_tool_id",tool_id))
                .findUnique();

        if(borrowRequests == null) {
            return notFound(views.html.errors.notfound.render("The request you are looking for does not exist!"));
        }
        Tool tool = borrowRequests.requestedTool;
        long currentUser_id = Long.parseLong(session("user_id"));
        if (tool.owner.id != currentUser_id) {
            return badRequest(views.html.errors.badrequest.render("You're not the own of the requested tool!"));
        }

        Users borrower = borrowRequests.requester;
        tool.borrower = borrower;
        //borrower.borrowingList.add(tool);
        tool.requestList.clear();
        tool.update();
        flash("success","Tool has been lend to " + borrower.username);
        return eachTool(tool_id);
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
            toolList = Tool.find
                    .where()
                    .and(Expr.ne("owner_id", user_id),
                            Expr.eq("borrower_id",null) )
                    .orderBy("name").findList();
        } else {
            toolList = Tool.find.orderBy("toolType.name").findList();
        }
        return ok(views.html.tools.browse.render(toolList));

    }

    public Result eachTool(long id) {
        Tool tool = Tool.find.byId(id);
        if(tool == null)
            return notFound(views.html.errors.notfound.render("The tool you are looking for does not exist."));
        else
        return ok(views.html.tools.eachTool.render(tool));
    }
}
