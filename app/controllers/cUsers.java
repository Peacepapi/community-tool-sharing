package controllers;

/**
 * Created by linmh on 11/25/2015.
 */

import com.avaje.ebean.Expr;
import models.BorrowRequests;
import models.Tool;
import models.ToolType;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;


public class cUsers extends Controller {

    @Security.Authenticated(UserAuth.class)
    public Result myProfile() {
        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        return ok(views.html.users.myprofile.render(user, user.userProfile,
                Tool.find.where().eq("owner_id", user.id).orderBy("name").findList(),
                Tool.find.where().eq("borrower_id", user.id).orderBy("name").findList(),
                BorrowRequests.find.where().eq("requester_id",user.id).orderBy("requestTime").findList(),
                Tool.find.where().and(Expr.eq("owner_id", user.id), Expr.gt("requestCount", 0)).orderBy("name").findList(),
                ToolType.find.orderBy("name").orderBy("name").findList()));
    }

    public Result profile(long user_id){
        Users user = Users.find.byId(user_id);
        if (user == null) {
            return notFound(views.html.errors.notfound.render("The user you are looking for does not exist"));
        }
        return ok(views.html.users.profile.render(user, user.toolList));
    }
}