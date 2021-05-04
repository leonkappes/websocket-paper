package space.kappes.commands;

import org.json.JSONObject;
import space.kappes.WebsocketManager;
import space.kappes.command.Command;
import space.kappes.entity.Conference;
import space.kappes.entity.User;

public class Login extends Command {

    /**
     * Used for authenticating on first connect, argument tells if user is confernce leader or not
     * @see Command#Command(String, String[])
     */
    public Login() {
        super("login", new String[]{"privileged", "code"});
    }

    /**
     * @see Command#run(WebsocketManager, User, JSONObject)
     */
    @Override
    public void run(WebsocketManager manager, User user, JSONObject args) {
        boolean privileged = args.getBoolean("privileged");
        String code = args.getString("code");
        user.setPrivileged(privileged);
        user.setCode(code);
        Conference conference = manager.getConferences().get(code);
        // Add identifier and the conference Object to the json-data
        JSONObject conferenceObject = new JSONObject().put("identifier", user.getIdentifier()).put("conference", new JSONObject(conference));
        // Send update command with json-object
        user.send("updateConference", conferenceObject);
    }
}
