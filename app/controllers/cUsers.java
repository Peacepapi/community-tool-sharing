package controllers;

/**
 * Created by linmh on 11/25/2015.
 */

import models.BorrowRequests;
import models.Tool;
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
        List<BorrowRequests> requestList = new ArrayList<>();
        return ok(views.html.users.myprofile.render(user, user.userProfile, user.toolList));
    }

    public Result profile(long user_id){
        Users user = Users.find.byId(user_id);
        if (user == null) {
            return notFound(views.html.errors.notfound.render("The user you are looking for does not exist"));
        }
        return ok(views.html.users.profile.render(user.userProfile, user.toolList));
    }
}