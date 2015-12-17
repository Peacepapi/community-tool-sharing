package controllers;

import com.avaje.ebean.Expr;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.annotation.Transactional;
import com.avaje.ebean.enhance.asm.Type;
import models.BorrowRequests;
import models.Tool;
import models.ToolType;
import models.Users;
import play.data.DynamicForm;
import play.mvc.*;
import play.mvc.Controller;

import javax.persistence.PersistenceException;
import java.util.List;

import static play.data.Form.form;

/**
 * Created by Mike on 11/14/2015.
 */
public class Tools extends Controller {

    public final static int BORROW = 267769;
    public final static int DELETE = 335383;


    public Result index() {
        return ok(views.html.tools.browse.render(
                Tool.find.orderBy("toolType").findList(),ToolType.find.orderBy("name").findList()));
    }

    public Result getToolByUser(Long user_id) {
        List<Tool> toolList;
        if (session().containsKey("user_id")) {
            toolList = Tool.find.where().eq("owner_id", user_id).orderBy("toolType.name").findList();
        } else {
            toolList = Tool.find.orderBy("toolType.name").findList();
        }
        return ok(views.html.tools.browse.render(
                toolList,
                ToolType.find.orderBy("name").findList()));
    }

    public Result getToolByType(Long type_id) {
        List<Tool> toolList;
        if (session().containsKey("user_id")) {
            long user_id = Long.parseLong(session("user_id"));
            toolList = Tool.find.where()
                    .and(Expr.eq("tool_type_id", type_id),Expr.ne("owner_id", user_id))
                    .orderBy("toolType.name").findList();
        } else {
            toolList = Tool.find.orderBy("toolType.name").findList();
        }

        return ok(views.html.tools.browse.render(toolList,
                ToolType.find.orderBy("name").findList()));
    }

    public Result browse() {
        //fetch all tools
        List<Tool> toolList;
        if (session().containsKey("user_id")) {
            long user_id = Long.parseLong(session("user_id"));
            toolList = Tool.find.where().ne("owner_id", user_id).orderBy("toolType.name").findList();
        } else {
            toolList = Tool.find.orderBy("toolType.name").findList();
        }
        return ok(views.html.tools.browse.render(toolList, ToolType.find.orderBy("name").findList()));
    }

    /*public Result browse(int pageIndex) {
        //fetch all tools
        if (pageIndex < 0) pageIndex = 0;
        int itemPerPage = 20;
        PagedList<Tool> pagedList;
        List<Tool> toolList = new ArrayList<>();
        if (session().containsKey("user_id")) {
            long user_id = Long.parseLong(session("user_id"));
            pagedList = Ebean.find(Tool.class).where().ne("owner_id", user_id).order("id").findPagedList(pageIndex,itemPerPage);
            if (pagedList != null) {
                toolList = pagedList.getList();
            }
        } else {
            pagedList = Ebean.find(Tool.class).where().order("id").findPagedList(pageIndex,itemPerPage);
            if (pagedList != null) {
                toolList = pagedList.getList();
            }
        }
        return ok(views.html.tools.browse.render(toolList, ToolType.find.orderBy("name").findList()));
    }*/

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

        Tool tool = Tool.createNewTool(toolName, toolDesc, user, toolType);
        tool.save();
        flash("success", tool.name + " added!");

        return redirect(routes.cUsers.myProfile());
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
            tool.requestCount = tool.requestCount + 1;
            tool.update();
            flash("success", "Your request has been sent");
        } catch (PersistenceException e) {
            flash("error","You already requested the tool");
            return redirect(routes.Tools.browse());
//            return badRequest(views.html.errors.badrequest.render("You have already requested for the tool!"));
        }
        return redirect(routes.Tools.eachTool(id));
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
        tool.requestCount = -1;
        tool.update();
        flash("success","Tool has been lend to " + borrower.username);
        return redirect(routes.Tools.eachTool(tool_id));
    }

    @Security.Authenticated(UserAuth.class)
    @Transactional
    public Result rejectLend(long tool_id, long requester_id){
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

        borrowRequests.delete();
        tool.requestCount = tool.requestCount -  1;
        tool.update();
        return ok(views.html.tools.eachTool.render(tool.owner, tool, tool.comments, false));
    }

    @Security.Authenticated(UserAuth.class)
    @Transactional
    public Result requestReturn(long tool_id) {
        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        Tool tool = Tool.find.byId(tool_id);
        if (tool == null) {
            flash("error", "The requested tool does not exist!");
            return notFound("No such tool listed");
        }

        if ( !tool.borrower.id.equals(user.id) ) {
            flash("error","You cannot return a tool that does not belong to you!");
            return redirect(routes.Tools.eachTool(tool_id));
        }

        tool.requestReturn = true;
        tool.update();
        flash("success", "Your return request has been sent");

        return redirect(routes.Tools.eachTool(tool_id));
    }

    @Security.Authenticated(UserAuth.class)
    @Transactional
    public Result acceptReturn(long tool_id){
        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        Tool tool = Tool.find.byId(tool_id);
        if (tool == null) {
            flash("error", "The requested tool does not exist!");
            return notFound("No such tool listed");
        }

        if ( tool.owner.id != user.id ) {
            flash("error","You're not the owner of this tool!");
            return redirect(routes.Tools.eachTool(tool_id));
        }
        if ( tool.borrower == null  || !tool.requestReturn) {
            flash("error","The tool is ready to be returned!");
            return redirect(routes.Tools.eachTool(tool_id));
        }

        Users borrower = tool.borrower;
        tool.borrower = null;
        borrower.refresh();
        tool.requestReturn = false;
        tool.requestCount = 0;
        tool.update();
        flash("success", "Return request accepted");

        return redirect(routes.Tools.eachTool(tool_id));
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
        return redirect(routes.cUsers.myProfile());
    }

    public Result eachTool(long id) {
        Tool tool = Tool.find.byId(id);
        if(tool == null)
            return notFound(views.html.errors.notfound.render("The tool you are looking for does not exist."));

        boolean isRequested = false;
        if(session().containsKey("user_id") && !session("user_id").isEmpty()) {
            long user_id = Long.parseLong(session("user_id"));
            if (BorrowRequests.find.where()
                    .and(Expr.eq("requester_id",user_id),Expr.eq("requested_tool_id",id))
                    .findUnique() != null) {
                isRequested = true;
            }
        }

        return ok(views.html.tools.eachTool.render(tool.owner, tool, tool.comments, isRequested));
    }
}
