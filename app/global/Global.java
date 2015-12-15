package global;

import models.Tool;
import models.ToolType;
import models.Users;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import play.Application;
import play.GlobalSettings;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Mike on 12/13/2015.
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application application) {
        addAdmin();
        addDefaultToolTypes();
        addSampleTools();
        super.onStart(application);
    }

    @Override
    public void onStop(Application application) {
        super.onStop(application);
    }

    private void addAdmin() {
        if (Users.find.where().eq("username", "admin").findUnique() == null) {
            Users admin = new Users();
            admin.username = "admin";
            admin.password_hash = BCrypt.hashpw("password", BCrypt.gensalt());
            admin.userType = "Administrator";
            admin.save();
        }
        if (Users.find.where().eq("username", "exampleUser").findUnique() == null) {
            Users dummpyUser = new Users();
            dummpyUser.username = "exampleUser";
            dummpyUser.password_hash = BCrypt.hashpw("password", BCrypt.gensalt());
            dummpyUser.save();

        }
    }
    private void addDefaultToolTypes() {
        String[] defaultTypes =
                {"Misc", "Screwdriver", "Wrenches", "Clamps", "Pliers", "Hammer", "Power Tools"};
        for (String type : defaultTypes) {
            if (ToolType.find.where().eq("name", type).findUnique() == null) {
                ToolType miscTool = ToolType.createNewToolType(type);
                miscTool.save();
            }
        }
    }
    private void addSampleTools() {
        Users user = Users.find.where().eq("username", "admin").findUnique();
        List<ToolType> toolTypes = ToolType.find.all();

        for (int i = Tool.find.all().size(); i < 50; i++) {
            Tool.createNewTool(RandomStringUtils.random(10), RandomStringUtils.random(50)
                    , user, toolTypes.get(new Random().nextInt(toolTypes.size()))).save();

        }
    }
}
