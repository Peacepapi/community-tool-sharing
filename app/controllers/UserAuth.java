package controllers;

import models.Users;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Mike on 11/23/2015.
 */
public class UserAuth extends Security.Authenticator {

    @Override
    public String getUsername(final Http.Context context) {
        String userId = context.session().get("user_id");
        if (userId == null || userId.isEmpty()) return null;

        Users user = Users.find.byId(Long.parseLong(userId));
        return (user != null ? user.id.toString() : null);
    }

    @Override
    public Result onUnauthorized(final Http.Context context) {
        context.flash().put("error", "Please log in to perform that action.");
        return redirect(routes.Application.index());
    }
}
