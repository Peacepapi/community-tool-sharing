package controllers;

import models.Tool;
import play.data.Form;
import play.mvc.*;
import play.mvc.Controller;

import java.util.List;

/**
 * Created by Mike on 11/14/2015.
 */
public class Tools extends Controller {

    public Result index() {
        List<Tool> tools = Tool.find.all();
        return ok(views.html.tools.index.render(tools));
    }

    public Result create() {
        Tool tool = Form.form(Tool.class).bindFromRequest().get();
        tool.save();
        flash("sucess", "Saved new Tool: " + tool.name);
        return redirect(routes.Tools.index());
    }

}
