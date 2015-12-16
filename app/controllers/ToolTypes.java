package controllers;

import models.ToolType;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Controller;
import play.mvc.Security;

/**
 * Created by Mike on 12/13/2015.
 */
public class ToolTypes extends Controller {

    @Security.Authenticated(AdminUserAuth.class)
    public Result create() {
        DynamicForm typeForm = Form.form().bindFromRequest();
        String typeName = typeForm.data().get("name");
        ToolType toolType = ToolType.createNewToolType(typeName);
        if (toolType == null) {
            flash("error","Could not create new tool toolType!");
        } else if (toolType.name == null) {
            flash("error","Invalid tool toolType name!");
        } else {
            toolType.save();
            flash("success",toolType.name + " tool toolType added!");
        }
        return redirect(routes.Tools.browse());
    }
}