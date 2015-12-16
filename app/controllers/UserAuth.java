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
        if (user == null) {
            context.session().remove("user_id");
            context.session().remove("username");
            context.session().remove("loginIP");
            return null;
        }

/*
        String loginIP = context.request().remoteAddress();
        if (!user.loginIP.equals(loginIP)) {
            Http.Context.current().session().clear();
            user.loginIP = loginIP;
            return null;
        }
*/
        return user.id.toString();
    }

    @Override
    public Result onUnauthorized(final Http.Context context) {
        context.flash().put("error", "Please log in to perform that action.");
        return redirect(routes.Application.index());
    }
}
