package controllers;


import models.Users;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Mike on 12/12/2015.
 */
public class AdminUserAuth extends Security.Authenticator {

    @Override
    public String getUsername(final Http.Context context) {
        String userId = context.session().get("user_id");
        if (userId == null) return null;

        Users user = Users.find.byId(Long.parseLong(userId));
        return ((user != null && user.userType.equals("Administrator")) ? user.id.toString() : null);
    }

    @Override
    public Result onUnauthorized(final Http.Context context) {
        context.flash().put("error", "You're not an admin user!.");
        return redirect(routes.Application.index());
    }
}
