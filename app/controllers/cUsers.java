package controllers;

/**
 * Created by linmh on 11/25/2015.
 */

import models.Tool;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;


public class cUsers extends Controller {
    @Security.Authenticated(UserAuth.class)
    public Result profile() {
        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        List<Tool> tools = Tool.find.query().where("owner_id="+user.id).findList();
        return ok(views.html.users.profile.render(user, user.userProfile));
    }
}