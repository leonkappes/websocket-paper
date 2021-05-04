package space.kappes.command;

import org.json.JSONObject;
import space.kappes.WebsocketManager;
import space.kappes.entity.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for parsing and executing all incoming commands
 */
public class CommandManager {

    private final WebsocketManager manager;
    private final Map<String, Command> commandMap;

    /**
     * Creates an instance of the commandmanager, responsible for parsing and executing all incoming commands
     * @param manager Main instance of this project, used for accessing various other objects
     */
    public CommandManager(WebsocketManager manager) {
        this.manager = manager;
        this.commandMap = new HashMap<>();
    }

    /**
     * Registers an command to be parsed if received
     * @param command command to register
     */
    public void registerCommand(Command command) {
        commandMap.put(command.getInvoke(), command);
    }

    /**
     * Iteratively calls {@link #registerCommand(Command)} for every passed command
     * @param commands Multiple commands to register
     */
    public void registerCommands(Command... commands) {
        for (Command command : commands)
            registerCommand(command);
    }

    /**
     * Parses an incoming message into an command and executes it when existing
     * @param message Message which is received from websocket
     * @param user user from whom the message origins
     */
    public void parseCommand(String message, User user) {
        // Invoke is determent by the first word before an whitespace, here the string is split at whitespaces and the returning array are all words
        String invoke = message.split("\\s")[0];
        // Abort if this command isn't registered
        if(!commandMap.containsKey(invoke))
            return;
        Command command = commandMap.get(invoke);
        // JSON-Data is everything after the invoke plus the trailing whitespace
        JSONObject jsonObject = new JSONObject(message.replace(invoke+" ",""));
        // Loop through all required arguments and check if the exist, return an error if not so
        for (String required: command.getRequiredArgs()) {
            if (!jsonObject.has(required)) {
                user.send("error", new JSONObject().put("error", "Invalid Command").put("message", String.format("Missing argument: %s", required)));
                return;
            }
        }
        // Run the logic of the command, gets passed the json-data, the user and manager
        command.run(manager, user, jsonObject);
    }
}
