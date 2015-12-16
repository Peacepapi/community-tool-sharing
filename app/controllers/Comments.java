package controllers;

import models.Comment;
import models.Users;
import models.Tool;
import play.mvc.*;
import play.mvc.Controller;

import play.data.DynamicForm;
import play.data.Form;
import static play.data.Form.form;

import java.util.Date;

public class Comments extends Controller {

    @Security.Authenticated(UserAuth.class)
	public Result postComment(long id) {
		DynamicForm commentForm = form().bindFromRequest();
        String body = commentForm.data().get("body");

        Users user = Users.find.byId(Long.parseLong(session("user_id")));
        Tool tool = Tool.find.byId(id);

        Comment comment = Comment.createNewComment(user, tool, body);
        comment.save();

        return redirect(routes.Tools.eachTool(comment.tool.id));
	}
}