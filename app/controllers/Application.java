package controllers;

import models.Profile;
import models.Users;
import play.data.DynamicForm;
import play.mvc.*;

import static play.data.Form.form;
import views.html.*;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("CTS | Community Tool Sharing"));
    }

    public Result login() {
        DynamicForm userForm = form().bindFromRequest();
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");

        Users user = Users.find.where().eq("username", username).findUnique();

        if (user != null && user.authenticate(password)) {
            user.loginIP = request().remoteAddress();
            user.update();
            session("user_id", user.id.toString());
            session("username", user.username);
            session("loginIP", user.loginIP);
            flash("success", "Welcome back " + user.username);
        } else {
            flash ("error", "The username and password combination did not match our records. Please Try again");
        }

        return redirect(routes.Application.index());
    }

    public Result onSignUpClick() {
        return ok(views.html.users.register.render());
    }

    public Result register() {
        DynamicForm userForm = form().bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(views.html.index.render("CTS | Community Tool Sharing"));
        }
        String username = userForm.data().get("username");
        String email    = userForm.data().get("email");
        String password = userForm.data().get("password");

        if (!Users.usernamePattern.matcher(username).matches()) {
            flash("error","Username contains invalid characters.\n" +
                    "Only alphabet and numbers are allowed and must be 3 to 20 characters long");
            return redirect(routes.Application.register());
        }
        if (Users.find.where().eq("username", username).findUnique() != null) {
            flash("error","Username already exist!");
            return redirect(routes.Application.register());
        }
        if (Users.find.where().eq("email", email).findUnique() != null) {
            flash("error","email already in use!");
            return redirect(routes.Application.register());
        }

        Users user = Users.createNewUser(username, password, email);
        if (user == null) {
            flash ("error", "Password is invalid!");
            return redirect(routes.Application.register());
        }

        user.save();/*
        Profile profile = Profile.createNewProfile(user);
        profile.save();
        user.userProfile = profile;
        user.update();*/

        user.loginIP = request().remoteAddress();
        user.update();

        flash("success", "Welcome to the community " + user.username);
        session("user_id", user.id.toString());
        session("username", user.username);
        session("loginIP", user.loginIP);

        return redirect(routes.Application.index());
    }

    public Result logout() {
        if (session().containsKey("user_id")) {
            Users user = Users.find.byId(Long.parseLong(session("user_id")));
            user.loginIP = null;
            session().remove("user_id");
            session().remove("username");
            session().remove("loginIP");
            flash("success", "Log out successful.");
        }

        return redirect(routes.Application.index());
    }
}
