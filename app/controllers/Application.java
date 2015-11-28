package controllers;

import models.Users;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

import static play.data.Form.form;

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
            session("user_id", user.id.toString());
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
        String username = userForm.data().get("username");
        String password = userForm.data().get("password");

        Users user = Users.createNewUser(username, password);
        if (user == null) {
            flash("error", "Invalid password!");
            return redirect(routes.Application.register());
        }

        user.save();

        flash("success", "Welcome to the community " + user.username);
        session("user_id", user.id.toString());
        return redirect(routes.Application.index());
    }

    public Result logout() {
        session().remove("user_id");
        flash("success", "Log out successful.");
        return redirect(routes.Application.index());
    }
}
