package space.kappes.command;

import org.json.JSONObject;
import space.kappes.WebsocketManager;
import space.kappes.entity.User;

/**
 * Class on which all commands extend
 */
public abstract class Command {
    private final String invoke;
    private String[] requiredArgs = new String[]{};

    /**
     * Constructs a command with no required arguments
     * @param invoke Command invoke/name on which this command should be run
     */
    public Command(String invoke) {
        this.invoke = invoke;
    }

    /**
     * Constructs a command with required arguments, if they are not met a error is returned
     * @param invoke Command invoke/name on which this command should be run
     * @param requiredArgs array of string which have to be in the json-data to meet this commands requirements
     */
    public Command(String invoke, String[] requiredArgs) {
        this.invoke = invoke;
        this.requiredArgs = requiredArgs;
    }

    /**
     * Method which gets run by the Commandmanager, when a matching command is received
     * @param manager Instance of the Managerclass
     * @param user User which called this command
     * @param args arguments provided by command call
     */
    public abstract void run(WebsocketManager manager, User user, JSONObject args);

    public String getInvoke() {
        return invoke;
    }

    public String[] getRequiredArgs() {
        return requiredArgs;
    }

    public void setRequiredArgs(String[] requiredArgs) {
        this.requiredArgs = requiredArgs;
    }
}
